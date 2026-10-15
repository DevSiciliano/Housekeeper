package local.noto.housekeeper.user

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class RegistrationController(
    private val registration: RegistrationService
) {

    // @Valid triggers the constraints on RegisterRequest; invalid input never reaches the service
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): UserResponse =
        registration.register(
            name = request.name,
            email = request.email,
            password = request.password,
        ).toResponse()
}
