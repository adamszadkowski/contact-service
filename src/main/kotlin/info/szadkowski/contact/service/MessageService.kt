package info.szadkowski.contact.service

import info.szadkowski.contact.model.MessageRequest

interface MessageService {
    suspend fun send(message: MessageRequest)

    class MessageSendException : RuntimeException {
        constructor()
        constructor(cause: Throwable) : super(cause)
    }
}
