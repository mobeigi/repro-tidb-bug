package io.atlassian.micros.studio.config

import com.github.benmanes.caffeine.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfig {
    @Bean
    fun cacheManager(caches: Map<String, Cache<Any, Any>>): CacheManager {
        val cacheManager = CaffeineCacheManager()
        for ((name, cache) in caches) {
            cacheManager.registerCustomCache(name, cache)
        }
        return cacheManager
    }
}
