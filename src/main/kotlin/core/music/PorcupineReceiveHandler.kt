package core.core.music

import ai.picovoice.porcupine.Porcupine
import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.UserAudio
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.abs
import kotlin.system.measureTimeMillis

/**
 * Обработчик входящего аудио от пользователей Discord-канала.
 *
 * Детектирует wake-слово "OK Google" через Porcupine, после чего записывает аудио
 * до наступления тишины длительностью [maxSilenceMillis] мс и сохраняет результат в WAV-файл.
 *
 * Все операции с разделяемыми буферами ([monoBuffer], [recordedBuffer]) защищены
 * блоком `synchronized(this)`, так как JDA вызывает [handleUserAudio] из нескольких
 * потоков одновременно — по одному на каждого говорящего пользователя.
 *
 * @property accessKey ключ доступа Picovoice для инициализации Porcupine
 */
class PorcupineReceiveHandler(
    accessKey: String
) : AudioReceiveHandler {
    private val porcupine: Porcupine = Porcupine.Builder()
        .setAccessKey(accessKey)
        .setBuiltInKeyword(Porcupine.BuiltInKeyword.OK_GOOGLE)
        .build()

    private val monoBuffer = mutableListOf<Short>()
    private val frameSize = porcupine.frameLength
    private val overlap = frameSize / 2

    private var isRecording = false
    private val recordedBuffer = mutableListOf<Short>()

    companion object {
        private val logger = LoggerFactory.getLogger(PorcupineReceiveHandler::class.java)
    }

    private val silenceThreshold = 200
    private val maxSilenceMillis = 1500L  // 1.5 секунды тишины
    private var lastAudioTime = System.currentTimeMillis()

    override fun canReceiveUser() = true
    override fun canReceiveCombined() = false

    override fun handleUserAudio(userAudio: UserAudio) {
        val data = userAudio.getAudioData(1.0)
        val resampled = convertStereo48kToMono16k(data)
        synchronized(this) {
            monoBuffer.addAll(resampled.toList())
            processBuffer()
            lastAudioTime = System.currentTimeMillis()
        }
    }


    /**
     * Конвертирует стерео PCM 48 кГц (big-endian) в моно PCM 16 кГц простым децимированием.
     *
     * Берёт только левый канал и применяет фактор децимации 3 (48k / 3 = 16k).
     *
     * @param input сырые байты аудио в формате Discord (48 кГц, стерео, 16 бит, big-endian)
     * @return массив отсчётов PCM 16 кГц моно
     */
    fun downsampleStereo48kToMono16k(input: ByteArray): ShortArray {
        val inputShorts = ShortArray(input.size / 2)
        // Discord: big endian
        for (i in inputShorts.indices) {
            inputShorts[i] = ((input[i * 2].toInt() shl 8) or (input[i * 2 + 1].toInt() and 0xFF)).toShort()
        }

        // берем левый канал и делаем downsample (48k -> 16k)
        val factor = 3
        val result = ShortArray(inputShorts.size / 4 / factor)
        var j = 0
        for (i in inputShorts.indices step 4 * factor) {
            result[j++] = inputShorts[i] // левый канал
        }
        return result
    }
    /**
     * Конвертирует стерео PCM 48 кГц в моно PCM 16 кГц через ресемплирование Java Audio API (TarsosDSP).
     *
     * В отличие от [downsampleStereo48kToMono16k], использует правильный ресемплер, что даёт
     * корректное качество звука для Porcupine.
     *
     * @param input сырые байты аудио в формате Discord (48 кГц, стерео, 16 бит, big-endian)
     * @return массив отсчётов PCM 16 кГц моно (little-endian)
     */
    fun convertStereo48kToMono16k(input: ByteArray): ShortArray {
        val sourceFormat = AudioFormat(48000f, 16, 2, true, true)
        val sourceStream = AudioInputStream(
            ByteArrayInputStream(input),
            sourceFormat,
            input.size / 4L
        )

        val targetFormat = AudioFormat(16000f, 16, 1, true, false)
        val resampledStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream)

        val baos = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var read: Int
        while (resampledStream.read(buffer).also { read = it } != -1) {
            baos.write(buffer, 0, read)
        }
        val bytes = baos.toByteArray()
        val shorts = ShortArray(bytes.size / 2)
        for (i in shorts.indices) {
            shorts[i] = ((bytes[i * 2 + 1].toInt() shl 8) or (bytes[i * 2].toInt() and 0xFF)).toShort()
        }
        return shorts
    }

    private fun processBuffer() {
        while (monoBuffer.size >= frameSize) {
            val frame = monoBuffer.take(frameSize).toShortArray()
            val keywordIndex = porcupine.process(frame)

            if (!isRecording && keywordIndex >= 0) {
                logger.info("Wake word detected! Starting recording...")
                isRecording = true
                recordedBuffer.clear()
                lastAudioTime = System.currentTimeMillis()
            }

            if (isRecording) {
                recordedBuffer.addAll(frame.toList())

                val avgAmplitude = frame.map { abs(it.toInt()) }.average()
                val now = System.currentTimeMillis()

                // Если долго нет аудио (тишина), останавливаем запись
                if (now - lastAudioTime > maxSilenceMillis) {
                    logger.info("Recording stopped due to silence. Saving to file...")
                    saveRecordingToWav(recordedBuffer.toShortArray(), "recorded.wav")
                    logger.info("File saved: recorded.wav")
                    isRecording = false
                }
            }

            monoBuffer.subList(0, overlap).clear()
        }
    }

    private fun saveRecordingToWav(data: ShortArray, fileName: String) {
        val byteBuffer = ByteArray(data.size * 2)
        for (i in data.indices) {
            byteBuffer[i * 2] = (data[i].toInt() and 0xFF).toByte()
            byteBuffer[i * 2 + 1] = ((data[i].toInt() shr 8) and 0xFF).toByte()
        }

        val audioFormat = AudioFormat(
            16000f,
            16,
            1,
            true,
            false
        )
        val audioInputStream = AudioInputStream(ByteArrayInputStream(byteBuffer), audioFormat, data.size.toLong())

        try {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, File(fileName))
        } catch (e: IOException) {
            logger.error("Failed to save recording to WAV file: $fileName", e)
        }
    }
}
