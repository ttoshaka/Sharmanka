package core.music

import ai.picovoice.porcupine.Porcupine

class PorcupineFeeder(
    private val porcupine: Porcupine
) {
    private val frameLength = porcupine.frameLength
    private val buffer = ShortArray(frameLength)
    private var bufferOffset = 0

    fun feed(samples: ShortArray): Int? {
        var idx = 0
        while (idx < samples.size) {
            val toCopy = minOf(frameLength - bufferOffset, samples.size - idx)
            System.arraycopy(samples, idx, buffer, bufferOffset, toCopy)

            bufferOffset += toCopy
            idx += toCopy

            if (bufferOffset == frameLength) {
                val result = porcupine.process(buffer)
                bufferOffset = 0

                if (result >= 0) {
                    return result // wake word index
                }
            }
        }
        return null
    }
}

class Resampler48kTo16k {

    private val fir = floatArrayOf(
        -0.0102f, 0.0f, 0.0523f, 0.1265f,
        0.2000f,
        0.2000f,
        0.1265f, 0.0523f, 0.0f, -0.0102f
    )

    private val history = FloatArray(fir.size)
    private var histPos = 0

    fun process(input: ShortArray): ShortArray {
        val outSize = input.size / 3
        val output = ShortArray(outSize)
        var outIdx = 0

        var inIdx = 0
        while (inIdx + 2 < input.size) {

            // добавляем ТОЛЬКО нужные сэмплы
            history[histPos] = input[inIdx].toFloat()
            histPos = (histPos + 1) % history.size

            // FIR
            var acc = 0f
            var h = 0
            var p = histPos
            while (h < fir.size) {
                p = if (p == 0) history.lastIndex else p - 1
                acc += history[p] * fir[h]
                h++
            }

            output[outIdx++] =
                acc.toInt().coerceIn(-32768, 32767).toShort()

            // ❗ шаг по времени
            inIdx += 3
        }

        return output
    }
}
