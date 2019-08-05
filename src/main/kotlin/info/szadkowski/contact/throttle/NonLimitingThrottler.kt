package info.szadkowski.contact.throttle

internal class NonLimitingThrottler : Throttler {
    override fun canProcess(key: String) = true
    override fun clearExpired() = Unit
}
