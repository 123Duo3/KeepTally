package com.konyaco.keeptally.service

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode

object SecretGenerator {
    private val argon2 = Argon2Kt()
    private val salt = "\$AwesomeKeepTally\$"

    /**
     * Argon2 encode password
     */
    fun generateSecret(password: String): String {
        val hash = argon2.hash(
            mode = Argon2Mode.ARGON2_ID,
            password = password.toByteArray(),
            salt = salt.toByteArray(),
        )
        return hash.rawHashAsHexadecimal(true)
    }
}