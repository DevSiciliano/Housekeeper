package local.noto.housekeeper.user

// Response body for POST /api/auth/login; the access and refresh tokens will be added here with JWT
data class LoginResponse(
    val user: UserResponse,
)
