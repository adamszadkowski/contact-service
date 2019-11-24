package info.szadkowski.contact.controller.exception

import info.szadkowski.contact.exception.ThrottledRequestException
import info.szadkowski.contact.service.MessageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandlerController {

    @ExceptionHandler(ThrottledRequestException::class)
    fun handleThrottling() = ResponseEntity<Unit>(HttpStatus.TOO_MANY_REQUESTS)

    @ExceptionHandler(MessageService.MessageSendException::class)
    fun handleBadRequest() = ResponseEntity<Unit>(HttpStatus.BAD_REQUEST)
}
