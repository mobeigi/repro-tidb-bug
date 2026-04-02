package io.atlassian.micros.studio.common.spring.restclient

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class RestClientInterceptor(
    private val clientName: String,
) : ClientHttpRequestInterceptor {
    private val log = LoggerFactory.getLogger(RestClientInterceptor::class.java)

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        val start = System.currentTimeMillis()
        val response = execution.execute(request, body)
        val duration = System.currentTimeMillis() - start
        log.info(
            "EGRESS_LOG client={} method={} path={} status={} durationMs={}",
            clientName,
            request.method.name(),
            request.uri.path,
            response.statusCode.value(),
            duration,
        )
        return response
    }
}
