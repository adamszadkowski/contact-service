package info.szadkowski.contact.throttle

import info.szadkowski.contact.service.MessageService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
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
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var messageService: MessageService

    @Test
    fun `Should throttle requests`() {
        requestFromIp("10.0.0.1").andExpect(status().isOk())
        requestFromIp("10.0.0.1").andExpect(status().isTooManyRequests())
        requestFromIp("10.0.0.2").andExpect(status().isOk())
        requestFromIp("10.0.0.3").andExpect(status().isTooManyRequests())
    }

    private fun requestFromIp(address: String): ResultActions {
        return mvc.perform(
            post("/v1/message")
                .with(remoteAddr(address))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                           "subject": "subject",
                           "content": "content"
                        }
                    """.trimIndent()
                )
        )
    }

    private fun remoteAddr(remoteAddr: String) = RequestPostProcessor {
        it.remoteAddr = remoteAddr
        it
    }
}
