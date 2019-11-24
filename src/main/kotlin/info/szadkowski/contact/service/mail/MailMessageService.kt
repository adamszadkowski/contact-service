package info.szadkowski.contact.service.mail

import info.szadkowski.contact.model.MessageRequest
import info.szadkowski.contact.properties.MailAddressesProperties
import info.szadkowski.contact.service.MessageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper

class MailMessageService(
    private val mailAddressesProperties: MailAddressesProperties,
    private val javaMailSender: JavaMailSender
) : MessageService {
    override suspend fun send(message: MessageRequest) {
        val helper = MimeMessageHelper(javaMailSender.createMimeMessage())

        try {
            withContext(Dispatchers.IO) {
                javaMailSender.send(helper.run {
                    setSubject(message.subject)
                    setFrom(mailAddressesProperties.senderMail)
                    setTo(mailAddressesProperties.recipientMail)
                    setText(message.content)

                    mimeMessage
                })
            }
        } catch (e: Exception) {
            throw MessageService.MessageSendException(e)
        }
    }
}
