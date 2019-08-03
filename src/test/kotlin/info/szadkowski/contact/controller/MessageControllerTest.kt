package info.szadkowski.contact.controller

import info.szadkowski.contact.controller.exception.ExceptionHandlerController
import info.szadkowski.contact.model.MessageRequest
import info.szadkowski.contact.service.MessageService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class MessageControllerTest : MessageService {
    lateinit var messageService: (MessageRequest) -> Unit
    lateinit var messages: MutableList<MessageRequest>
    lateinit var mvc: MockMvc

    override fun send(message: MessageRequest) = messageService(message)

    @BeforeEach
    fun setUp() {
        messages = mutableListOf()
        val messageController = MessageController(this) {
            "templated:" + it["content"]
        }
        mvc = MockMvcBuilders.standaloneSetup(messageController)
            .setControllerAdvice(ExceptionHandlerController())
            .build()
    }

    @Test
    fun `Should send correct mail request`() {
        messageService = { messages.add(it) }

        mvc.perform(
            post("/v1/message")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "subject": "mySubject",
                            "content": "myContent"
                        }
                    """.trimIndent()
                )
        ).andExpect(status().isOk())

        assertThat(messages).containsExactly(
            MessageRequest(
                subject = "mySubject",
                content = "templated:myContent"
            )
        )
    }

    @Test
    fun `Should fail on incorrect mail request`() {
        messageService = { throw MessageService.MessageSendException() }

        mvc.perform(
            post("/v1/message")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{}")
        ).andExpect(status().isBadRequest())
    }
}
