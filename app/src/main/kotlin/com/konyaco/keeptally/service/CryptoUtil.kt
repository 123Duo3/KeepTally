package com.konyaco.keeptally.service

import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class CryptoUtil {
    fun getKeyFromPassword(password: String, salt: String): SecretKey {
        val factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec =
            PBEKeySpec(password.toCharArray(), salt.toByteArray(), 65536, 256)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }

/*    fun encrypt(
        algorithm: String?, input: String, key: SecretKey?,
        iv: IvParameterSpec?
    ): String? {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(input.toByteArray())
        return Base64.getDecoder()
            .encodeToString(cipherText)
    }

    fun decrypt(
        algorithm: String?, cipherText: String?, key: SecretKey?,
        iv: IvParameterSpec?
    ): String? {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(
            Base64.getDecoder()
                .decode(cipherText)
        )
        return kotlin.String(plainText)
    }*/
}