package info.szadkowski.contact.throttle

interface Throttler {
    fun canProcess(key: String): Boolean
    fun clearExpired()
}
