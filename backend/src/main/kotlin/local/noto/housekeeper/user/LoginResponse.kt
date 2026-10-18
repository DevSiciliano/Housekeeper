package local.noto.housekeeper.user

// Response body for POST /api/auth/login; the refresh token will be added here later
data class LoginResponse(
    val accessToken: String,
    val user: UserResponse,
)
