package local.noto.housekeeper.user

import java.util.UUID

// What the client gets back about a user; never contains the password hash
data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
)

fun AppUser.toResponse() = UserResponse(
    id = id,
    name = name,
    email = email,
)
