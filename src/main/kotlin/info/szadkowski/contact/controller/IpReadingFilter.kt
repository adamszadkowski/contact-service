package info.szadkowski.contact.controller

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain

@Component
class IpReadingFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain) =
        chain.filter(exchange).subscriberContext {
            it.put("ip", exchange.request.remoteAddress?.address?.hostAddress ?: "missing")
        }
}
