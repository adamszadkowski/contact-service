package info.szadkowski.contact.throttle.time

class SystemTimeProvider : TimeProvider {
    override val currentMillis get() = System.currentTimeMillis()
}
