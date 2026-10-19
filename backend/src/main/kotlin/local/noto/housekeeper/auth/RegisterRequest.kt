package local.noto.housekeeper.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// Request body for POST /api/auth/register; only these fields can be set by the client
data class RegisterRequest(
    @NotBlank
    @Size(max = 50)
    val name: String,

    @NotBlank
    @Email
    @Size(max = 100)
    val email: String,

    // BCrypt only uses the first 72 bytes of a password, longer ones are rejected by the encoder
    @Size(min = 8, max = 72)
    val password: String,
)
