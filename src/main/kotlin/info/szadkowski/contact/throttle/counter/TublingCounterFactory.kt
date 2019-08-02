package info.szadkowski.contact.throttle.counter

interface TumblingCounterFactory {
    fun create(): TumblingCounter
}
