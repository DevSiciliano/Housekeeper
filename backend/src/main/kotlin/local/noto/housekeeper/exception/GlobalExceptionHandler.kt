package local.noto.housekeeper.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

// Central place that turns exceptions into HTTP responses for all controllers
@RestControllerAdvice
class GlobalExceptionHandler {

    // 409: the request is valid, but conflicts with an existing account
    @ExceptionHandler(EmailAlreadyRegisteredException::class)
    fun handleEmailAlreadyRegistered(ex: EmailAlreadyRegisteredException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            // Fixed text instead of ex.message, so internal wording never reaches the client
            "This e-mail is already registered.",
        ).apply {
            title = "E-mail already registered"
        }

    // 401: authentication failed; one neutral message for unknown e-mail and wrong password alike
    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Invalid e-mail or password.",
        ).apply {
            title = "Invalid credentials"
        }
}
