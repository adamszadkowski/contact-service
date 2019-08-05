package info.szadkowski.contact.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "mail")
class MailAddressesProperties {
    lateinit var recipientMail: String
    lateinit var senderMail: String
}
