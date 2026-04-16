package core.core.music

import java.nio.ByteBuffer
import java.nio.ByteOrder

class Downsampler48kTo16k {

    fun process(input: ByteArray): ShortArray {
        // ByteArray -> ShortArray (little-endian)
        val totalSamples = input.size / 2
        val pcm = ShortArray(totalSamples)

        var i = 0
        var j = 0
        while (i < input.size) {
            pcm[j++] = (
                    (input[i].toInt() and 0xFF) or
                            (input[i + 1].toInt() shl 8)
                    ).toShort()
            i += 2
        }

        // Stereo → Mono
        val mono48k = ShortArray(pcm.size / 2)
        var m = 0
        var s = 0
        while (s < pcm.size) {
            val left = pcm[s++]
            val right = pcm[s++]
            mono48k[m++] = ((left + right) / 2).toShort()
        }

        // 48 kHz → 16 kHz (decimation x3)
        val mono16k = ShortArray(mono48k.size / 3)
        var out = 0
        var idx = 0
        while (idx < mono48k.size) {
            mono16k[out++] = mono48k[idx]
            idx += 3
        }
        return mono16k
    }
}

