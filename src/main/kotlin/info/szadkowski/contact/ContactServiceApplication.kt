package info.szadkowski.contact

import info.szadkowski.contact.configuration.MainConfiguration
import info.szadkowski.contact.controller.MessageController
import info.szadkowski.contact.properties.MailAddressesProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackageClasses = [
        MainConfiguration::class,
        MessageController::class
    ]
)
@ConfigurationPropertiesScan(
    basePackageClasses = [MailAddressesProperties::class]
)
class ContactServiceApplication

fun main(args: Array<String>) {
    runApplication<ContactServiceApplication>(*args)
}
