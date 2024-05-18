package ru.kosti.googledrivemanager.configuration.filter

import kotlinx.coroutines.runBlocking
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import ru.kosti.googledrivemanager.enumeration.Roles
import ru.kosti.googledrivemanager.service.JwtService
import ru.kosti.googledrivemanager.service.UserService

@Component
@Order(-1)
class JwtFilterGatewayFilterFactory(
    private val jwtService: JwtService,
    private val userService: UserService
) : AbstractGatewayFilterFactory<Config>(Config::class.java) {

    override fun apply(config: Config): GatewayFilter =
        GatewayFilter { exchange, chain ->
            return@GatewayFilter filter(exchange, chain, config)
        }

    private fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain, configure: Config): Mono<Void> {
        val request = exchange.request
        val config = configure.getByMethod(request.method)
            ?.also { requestConfig ->
                if (!requestConfig.verified)
                    return chain.filter(exchange)
            }
            ?: throw Exception("Method ${request.method} not allowed")
        val header = request.headers["Authorization"]
            ?.get(0)
            ?: run { throw Exception("Auth header is not presented") }
        val token: String
        val role: Roles
        if (header.startsWith("Bearer ")) {
            token = header.substring(7)
            val u = jwtService.decode(token)
            role = runBlocking{ userService.findByIdOrNull(u.uuid).role }
            if (Roles.valueOf(config.role) != role)
                throw Exception("Role has insufficient rights")
        } else throw Exception("Auth header is not valid")
        return chain.filter(exchange)
    }
}