package info.szadkowski.contact

import info.szadkowski.contact.controller.MessageController
import info.szadkowski.contact.controller.exception.ExceptionHandlerController
import info.szadkowski.contact.properties.MailAddressesProperties
import info.szadkowski.contact.properties.TemplateProperties
import info.szadkowski.contact.properties.ThrottlingProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class ContactServiceApplicationTest {

    @Autowired
    lateinit var controller: MessageController

    @Autowired
    lateinit var exceptionController: ExceptionHandlerController

    @Autowired
    lateinit var mailAddressesProperties: MailAddressesProperties

    @Autowired
    lateinit var throttlingProperties: ThrottlingProperties

    @Autowired
    lateinit var templateProperties: TemplateProperties

    @Test
    fun `Should inject main beans`() {
        assertThat(controller).isNotNull()
        assertThat(exceptionController).isNotNull()
        assertThat(mailAddressesProperties).isNotNull()
        assertThat(throttlingProperties).isNotNull()
        assertThat(templateProperties).isNotNull()
    }
}
