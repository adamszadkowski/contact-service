package info.szadkowski.contact.service.mail

import info.szadkowski.contact.model.MessageRequest
import info.szadkowski.contact.properties.MailAddressesProperties
import info.szadkowski.contact.service.MessageService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import strikt.api.expectThrows
import strikt.assertions.*
import javax.mail.Message
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@ExtendWith(MockKExtension::class)
class MailMessageServiceTest {
    lateinit var service: MailMessageService

    @RelaxedMockK
    lateinit var sender: JavaMailSender

    @RelaxedMockK
    lateinit var mimeMessage: MimeMessage

    @BeforeEach
    fun setUp() {
        val mailAddressesProperties = MailAddressesProperties().apply {
            senderMail = "sender@address.com"
            recipientMail = "recipient@address.com"
        }
        every { sender.createMimeMessage() } returns mimeMessage
        service = MailMessageService(mailAddressesProperties, sender)
    }

    @Test
    fun `Should send mail`() = runBlocking<Unit> {
        service.send(
            MessageRequest(
                subject = "subject",
                content = "content"
            )
        )

        with(mimeMessage) {
            verify { subject = "subject" }
            verify { setFrom(InternetAddress("sender@address.com")) }
            verify { setRecipient(Message.RecipientType.TO, InternetAddress("recipient@address.com")) }
            verify { setText("content") }
        }

        verify { sender.send(mimeMessage) }
    }

    @Test
    fun `Should wrap MailException`(@MockK mailException: MailException) {
        every { mailException.cause } returns null
        every { sender.send(mimeMessage) } throws mailException
        val request = MessageRequest(
            subject = "subject",
            content = "content"
        )

        expectThrows<MessageService.MessageSendException> {
            service.send(request)
        }.cause.isEqualTo(mailException)
    }

    @Test
    fun `Should throw on invalid request`() {
        val request = MessageRequest()

        expectThrows<MessageService.MessageSendException> {
            service.send(request)
        }.message
            .isNotNull()
            .contains("Subject must not be null")
    }
}
