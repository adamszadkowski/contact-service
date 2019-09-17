package info.szadkowski.contact.properties

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExtendWith(SpringExtension::class)
@ContextConfiguration
@TestPropertySource(
    properties = [
        "mail.recipientMail=recipient@address.com",
        "mail.senderMail=sender@address.com"
    ]
)
@EnableConfigurationProperties(MailAddressesProperties::class)
class MailAddressesPropertiesTest {

    @Test
    fun `Should inject mail addresses`(@Autowired p: MailAddressesProperties) {
        expectThat(p) {
            get { recipientMail }.isEqualTo("recipient@address.com")
            get { senderMail }.isEqualTo("sender@address.com")
        }
    }
}
