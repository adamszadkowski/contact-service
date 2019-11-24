package info.szadkowski.contact

import info.szadkowski.contact.controller.MessageController
import info.szadkowski.contact.controller.exception.ExceptionHandlerController
import info.szadkowski.contact.properties.MailAddressesProperties
import info.szadkowski.contact.properties.TemplateProperties
import info.szadkowski.contact.properties.ThrottlingProperties
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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
    }
}
