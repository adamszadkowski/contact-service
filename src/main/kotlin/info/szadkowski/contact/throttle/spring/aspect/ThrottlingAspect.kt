package info.szadkowski.contact.throttle.spring.aspect

import info.szadkowski.contact.exception.ThrottledRequestException
import info.szadkowski.contact.throttle.Throttler
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
class ThrottlingAspect(
    private val ipThrottler: Throttler,
    private val allThrottler: Throttler
) {

    @Before("@annotation(info.szadkowski.contact.throttle.spring.aspect.Throttle)")
    fun throttle() {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request

        if (!ipThrottler.canProcess(request.remoteAddr) || !allThrottler.canProcess("all")) {
            throw ThrottledRequestException()
        }
    }
}
