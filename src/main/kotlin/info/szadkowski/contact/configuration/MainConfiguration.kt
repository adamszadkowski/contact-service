package info.szadkowski.contact.configuration

import info.szadkowski.contact.properties.MailAddressesProperties
import info.szadkowski.contact.service.mail.MailMessageService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration
class MainConfiguration {

    @Bean
    fun mailMessageService(
        mailAddressesProperties: MailAddressesProperties,
        javaMailSender: JavaMailSender
    ) = MailMessageService(mailAddressesProperties, javaMailSender)
}
