package info.szadkowski.contact.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

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
        assertThat(p.recipientMail).isEqualTo("recipient@address.com")
        assertThat(p.senderMail).isEqualTo("sender@address.com")
    }
}
