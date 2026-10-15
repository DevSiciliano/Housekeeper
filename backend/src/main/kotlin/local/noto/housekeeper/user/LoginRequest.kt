package local.noto.housekeeper.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// Request body for POST /api/auth/login
data class LoginRequest(
    @NotBlank
    @Size(max = 100)
    val email: String,

    // Same upper limit as on registration, so BCrypt never gets an input it rejects
    @NotBlank
    @Size(max = 72)
    val password: String,
) {
    // The generated toString of a data class would print the plain password into logs
    override fun toString() = "LoginRequest(email=$email, password=***)"
}
