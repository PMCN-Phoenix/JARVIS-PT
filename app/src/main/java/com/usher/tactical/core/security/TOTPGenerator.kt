package com.usher.tactical.core.security

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

/**
 * TOTP 令牌生成器（RFC 6238）
 * 用于双端重新编译验证，30秒步长，6位数字
 */
object TOTPGenerator {

    private const val TIME_STEP = 30L
    private const val DIGITS = 6
    private const val ALGORITHM = "HmacSHA1"

    // 默认种子（首次使用时生成，存储在 EncryptedSharedPreferences）
    private var seed: ByteArray = "USHER_TACTICAL_DEFAULT_SEED".toByteArray()

    fun initialize(seedBase32: String) {
        seed = base32Decode(seedBase32)
    }

    fun initialize(seedBytes: ByteArray) {
        seed = seedBytes
    }

    /**
     * 生成当前时刻的6位令牌
     */
    fun generate(): String {
        val counter = System.currentTimeMillis() / 1000 / TIME_STEP
        return generateTOTP(seed, counter, DIGITS)
    }

    /**
     * 验证令牌（允许前后各1个时间窗口）
     */
    fun verify(token: String, window: Int = 1): Boolean {
        val counter = System.currentTimeMillis() / 1000 / TIME_STEP
        for (i in -window..window) {
            if (generateTOTP(seed, counter + i, DIGITS) == token) return true
        }
        return false
    }

    private fun generateTOTP(key: ByteArray, counter: Long, digits: Int): String {
        val counterBytes = ByteArray(8)
        var c = counter
        for (i in 7 downTo 0) {
            counterBytes[i] = (c and 0xFF).toByte()
            c = c shr 8
        }

        val mac = Mac.getInstance(ALGORITHM)
        mac.init(SecretKeySpec(key, ALGORITHM))
        val hash = mac.doFinal(counterBytes)

        val offset = (hash[hash.size - 1].toInt() and 0x0F)
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                      ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                      ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                      (hash[offset + 3].toInt() and 0xFF)

        val otp = binary % 10.0.pow(digits).toInt()
        return otp.toString().padStart(digits, '0')
    }

    private fun base32Decode(base32: String): ByteArray {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val clean = base32.uppercase().replace("=", "").replace(" ", "")
        val result = mutableListOf<Byte>()
        var buffer = 0
        var bitsLeft = 0

        for (char in clean) {
            val value = alphabet.indexOf(char)
            if (value == -1) continue
            buffer = (buffer shl 5) or value
            bitsLeft += 5
            if (bitsLeft >= 8) {
                bitsLeft -= 8
                result.add((buffer shr bitsLeft).toByte())
            }
        }
        return result.toByteArray()
    }
}
