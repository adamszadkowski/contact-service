package info.szadkowski.contact.throttle

import info.szadkowski.contact.service.MessageService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.server.adapter.ForwardedHeaderTransformer
import java.net.InetSocketAddress

@SpringBootTest
@AutoConfigureWebTestClient
@TestPropertySource(
    properties = [
        "throttling.ip.limit=1",
        "throttling.ip.window=1h",
        "throttling.all.limit=2",
        "throttling.all.window=1h"
    ]
)
class RequestThrottlingTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockBean
    lateinit var mailMessageService: MessageService

    @Test
    fun `Should throttle requests`() {
        requestFromIp("10.0.0.1").expectStatus().isOk
        requestFromIp("10.0.0.1").expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
        requestFromIp("10.0.0.2").expectStatus().isOk
        requestFromIp("10.0.0.3").expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
    }

    private fun requestFromIp(address: String) = client
        .remoteAddr(address)
        .post()
        .uri("/v1/message")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            """
                {
                   "subject": "subject",
                   "content": "content"
                }
            """.trimIndent()
        ).exchange()

    private fun WebTestClient.remoteAddr(address: String) = mutateWith { _, httpHandlerBuilder, _ ->
        httpHandlerBuilder?.forwardedHeaderTransformer(object : ForwardedHeaderTransformer() {
            override fun apply(request: ServerHttpRequest) = object : ServerHttpRequest by request {
                override fun getRemoteAddress() = InetSocketAddress(address, 80)
            }
        })
    }
}
