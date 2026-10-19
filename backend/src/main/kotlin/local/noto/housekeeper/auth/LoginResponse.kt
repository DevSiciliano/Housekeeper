package local.noto.housekeeper.auth

import local.noto.housekeeper.user.UserResponse

// Response body for POST /api/auth/login; the refresh token will be added here later
data class LoginResponse(
    val accessToken: String,
    val user: UserResponse,
)
