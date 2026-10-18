package local.noto.housekeeper.user

import local.noto.housekeeper.exception.UnknownAccountException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class CurrentUserController(
    private val users: AppUserRepository
) {

    // Only reached with a valid token; the subject is the user id set by TokenService
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal jwt: Jwt): UserResponse {
        val id = UUID.fromString(jwt.subject)
        return users.findByIdOrNull(id)?.toResponse()
            ?: throw UnknownAccountException(id)
    }
}
