package com.konyaco.keeptally.service

import org.junit.Test

class SecretGeneratorTest {

    @Test
    fun generateSecret() {
        println(SecretGenerator.generateSecret("asdasdasdasdasd"))
    }
}