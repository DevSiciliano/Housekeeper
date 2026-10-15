package local.noto.housekeeper.user

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class LoginController(
    private val login: LoginService
) {

    // 200 on success; failed logins end up in GlobalExceptionHandler as 401
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResponse =
        LoginResponse(
            user = login.login(
                email = request.email,
                password = request.password,
            ).toResponse()
        )
}
