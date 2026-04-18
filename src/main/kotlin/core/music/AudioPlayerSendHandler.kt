package core.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioPlayerSendHandler(
    private val mainPlayer: AudioPlayer,
    private val backgroundPlayer: AudioPlayer
) : AudioSendHandler {

    private val mainFrameBuffer = ByteBuffer.allocate(FRAME_SIZE)
    private val backgroundFrameBuffer = ByteBuffer.allocate(FRAME_SIZE)
    private val outputBuffer = ByteBuffer.allocate(FRAME_SIZE)
    private val silenceBuffer: ByteBuffer = ByteBuffer.allocate(FRAME_SIZE).also { buf ->
        repeat(FRAME_SIZE) { buf.put(0) }
        buf.flip()
    }

    private val mainFrame = MutableAudioFrame().apply {
        setBuffer(mainFrameBuffer)
    }
    private val backgroundFrame = MutableAudioFrame().apply {
        setBuffer(backgroundFrameBuffer)
    }

    override fun canProvide(): Boolean {
        val hasMain = mainPlayer.provide(mainFrame)
        val hasBackground = backgroundPlayer.provide(backgroundFrame)
        return hasMain || hasBackground
    }

    override fun provide20MsAudio(): ByteBuffer {
        outputBuffer.clear()

        val hasMain = mainFrame.data != null && mainFrame.dataLength > 0
        val hasBackground = backgroundFrame.data != null && backgroundFrame.dataLength > 0

        when {
            hasMain && hasBackground -> mixFrames()
            hasMain -> copyFrame(mainFrame)
            hasBackground -> copyFrame(backgroundFrame)
            else -> fillSilence()
        }

        outputBuffer.flip()
        clearFrames()
        return outputBuffer
    }

    private fun mixFrames() {
        val mainData = mainFrame.data
        val bgData = backgroundFrame.data
        val mainBuffer = ByteBuffer.wrap(mainData).order(ByteOrder.BIG_ENDIAN)
        val bgBuffer = ByteBuffer.wrap(bgData).order(ByteOrder.BIG_ENDIAN)

        outputBuffer.order(ByteOrder.BIG_ENDIAN)

        val samples = minOf(mainFrame.dataLength, backgroundFrame.dataLength) / 2
        repeat(samples) {
            val mainSample = mainBuffer.short.toInt()
            val bgSample = bgBuffer.short.toInt()
            val mixed = (mainSample + bgSample).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            outputBuffer.putShort(mixed.toShort())
        }
    }

    private fun copyFrame(frame: MutableAudioFrame) {
        outputBuffer.put(frame.data, 0, frame.dataLength)
    }

    private fun fillSilence() {
        outputBuffer.put(silenceBuffer.duplicate())
    }

    private fun clearFrames() {
        mainFrame.setBuffer(mainFrameBuffer.clear() as ByteBuffer)
        backgroundFrame.setBuffer(backgroundFrameBuffer.clear() as ByteBuffer)
    }

    override fun isOpus(): Boolean = false

    companion object {
        private const val FRAME_SIZE = 3840 // 20ms of 48kHz stereo 16-bit PCM
    }
}