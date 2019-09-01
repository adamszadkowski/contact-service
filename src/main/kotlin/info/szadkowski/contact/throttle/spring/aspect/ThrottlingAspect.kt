package info.szadkowski.contact.throttle.spring.aspect

import info.szadkowski.contact.exception.ThrottledRequestException
import info.szadkowski.contact.throttle.Throttler
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Aspect
class ThrottlingAspect(
    private val ipThrottler: Throttler,
    private val allThrottler: Throttler
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain) =
        chain.filter(exchange).subscriberContext {
            it.put("ip", exchange.request.remoteAddress?.address?.hostAddress ?: "missing")
        }

    @Around("@annotation(info.szadkowski.contact.throttle.spring.aspect.Throttle)")
    fun throttle(proceedingJoinPoint: ProceedingJoinPoint): Any? {
        val mono = proceedingJoinPoint.args[0] as Mono<*>
        val throttlingMono = mono.flatMap { msg ->
            Mono.subscriberContext()
                .map {
                    it.get<String>("ip")
                }.map {
                    if (!ipThrottler.canProcess(it) || !allThrottler.canProcess("all")) {
                        throw ThrottledRequestException()
                    }
                    msg
                }
        }
        return proceedingJoinPoint.proceed(arrayOf(throttlingMono))
    }
}
