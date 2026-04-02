package io.atlassian.micros.studio.domain.tms.util

import org.springframework.stereotype.Service

interface TmsPasswordDecryptor {
    fun decrypt(password: String): String
}

/**
 * No-op decryptor: returns the password as-is.
 * Replaces the Atlassian-internal CryptorSidecarClient-based implementation
 * to allow the repo to build from open-source dependencies only.
 */
@Service
class PassthroughTmsPasswordDecryptor : TmsPasswordDecryptor {
    override fun decrypt(password: String): String = password
}
