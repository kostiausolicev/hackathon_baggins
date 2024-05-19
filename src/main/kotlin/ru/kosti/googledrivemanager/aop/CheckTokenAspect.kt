package ru.kosti.googledrivemanager.aop

import kotlinx.coroutines.runBlocking
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import ru.kosti.googledrivemanager.enumeration.Roles
import ru.kosti.googledrivemanager.service.JwtService
import ru.kosti.googledrivemanager.service.UserService


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CheckToken(val role: Roles)

@Aspect
@Component
class CheckTokenAspect(
    private val jwtService: JwtService,
    private val userService: UserService
) {

    @Pointcut("@annotation(checkToken)")
    fun checkTokenPointcut(checkToken: CheckToken) {
    }

    @Around("checkTokenPointcut(checkToken)")
    fun checkToken(pjp: ProceedingJoinPoint, checkToken: CheckToken): Any {
        val roleRequired = checkToken.role
        val token = (pjp.args[0] as String).let {
            if (it.startsWith("Bearer")) it.split(' ')[1] else it
        }
        val user = try {
            jwtService.decode(token)
        } catch (ex: Exception) {
            return ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED)
        }
        val currentRole = try {
            runBlocking { userService.findById(user.uuid) }
        } catch (ex: Exception) {
            return ResponseEntity<String>(ex.message, HttpStatus.BAD_REQUEST)
        }
        if (!currentRole.isConformed) {
            return ResponseEntity<String>("Access deny", HttpStatus.FORBIDDEN)
        }
        if (roleRequired.order < currentRole.role.order) {
            return ResponseEntity<String>("Access deny", HttpStatus.FORBIDDEN)
        }
        return pjp.proceed()
    }
}


