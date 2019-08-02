package info.szadkowski.contact.service.mail

import info.szadkowski.contact.model.MessageRequest
import info.szadkowski.contact.properties.MailAddressesProperties
import info.szadkowski.contact.service.MessageService
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import javax.mail.Message
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@ExtendWith(MockitoExtension::class)
class MailMessageServiceTest {
    lateinit var service: MailMessageService

    @Mock
    lateinit var sender: JavaMailSender

    @Mock
    lateinit var mimeMessage: MimeMessage

    @BeforeEach
    fun setUp() {
        val mailAddressesProperties = MailAddressesProperties()
        mailAddressesProperties.senderMail = "sender@address.com"
        mailAddressesProperties.recipientMail = "recipient@address.com"
        Mockito.`when`(sender.createMimeMessage()).thenReturn(mimeMessage)
        service = MailMessageService(mailAddressesProperties, sender)
    }

    @Test
    fun `Should send mail`() {
        service.send(
            MessageRequest(
                subject = "subject",
                content = "content"
            )
        )

        Mockito.verify(mimeMessage).subject = "subject"
        Mockito.verify(mimeMessage).setFrom(InternetAddress("sender@address.com"))
        Mockito.verify(mimeMessage).setRecipient(Message.RecipientType.TO, InternetAddress("recipient@address.com"))
        Mockito.verify(mimeMessage).setText("content")

        Mockito.verify(sender).send(mimeMessage)
    }

    @Test
    fun `Should wrap MailException`(@Mock mailException: MailException) {
        Mockito.doThrow(mailException).`when`(sender).send(Mockito.any(MimeMessage::class.java))
        val request = MessageRequest(
            subject = "subject",
            content = "content"
        )

        assertThatThrownBy { service.send(request) }
            .isExactlyInstanceOf(MessageService.MessageSendException::class.java)
            .hasCause(mailException)
    }

    @Test
    fun `Should throw on invalid request`() {
        val request = MessageRequest()

        assertThatThrownBy { service.send(request) }
            .isExactlyInstanceOf(MessageService.MessageSendException::class.java)
            .hasMessageContaining("Subject must not be null")
    }
}
