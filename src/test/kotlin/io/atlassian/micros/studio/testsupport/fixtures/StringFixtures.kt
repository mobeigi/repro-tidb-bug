package io.atlassian.micros.studio.testsupport.fixtures

import kotlin.random.Random

object StringFixtures {
    fun generateRandomAlphanumericString(length: Int): String {
        val alphabet = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(length) { alphabet.random(Random.Default) }.joinToString("")
    }
}
