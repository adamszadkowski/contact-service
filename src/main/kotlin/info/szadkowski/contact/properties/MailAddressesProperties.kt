package info.szadkowski.contact.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "mail")
data class MailAddressesProperties(
    val recipientMail: String,
    val senderMail: String
)
