package ru.kosti.googledrivemanager.configuration.filter

import org.springframework.http.HttpMethod

/**
 * Config class contain information about every http method.
 *
 * Default for all methods is config for role - ADMIN and required verification.
 *
 * @property post Config about http method POST.
 * @property get Config about http method GET.
 * @property patch Config about http method PATCH.
 * @property delete Config about http method DELETE.
 */
class Config(
    private val post: ConfigMethod? = null,
    private val get: ConfigMethod? = null,
    private val patch: ConfigMethod? = null,
    private val delete: ConfigMethod? = null,
) {
    fun getByMethod(method: HttpMethod) =
        when (method) {
            HttpMethod.POST -> post
            HttpMethod.GET -> get
            HttpMethod.PATCH -> patch
            HttpMethod.DELETE -> delete
            else -> throw Exception()
        }
}