package io.atlassian.micros.studio.testsupport.fixtures

import io.atlassian.micros.studio.common.AtlassianAccountId
import java.util.UUID
import kotlin.random.Random

object IdentityFixtures {
    object AtlassianAccount {
        fun randomId(): AtlassianAccountId =
            when (Random.nextInt(3)) {
                0 -> randomHex24()
                1 -> randomUuid()
                else -> randomCompound()
            }

        // Legacy, still supported
        private fun randomHex24(): AtlassianAccountId = (1..24).joinToString("") { Random.nextInt(16).toString(16) }

        // Legacy, deprecated / internal
        private fun randomUuid(): AtlassianAccountId = UUID.randomUUID().toString()

        // Current standard
        private fun randomCompound(): AtlassianAccountId = "${randomNumericPrefix()}:${UUID.randomUUID()}"

        private fun randomNumericPrefix(): String = Random.nextInt(100_000, 1_000_000).toString()
    }
}
