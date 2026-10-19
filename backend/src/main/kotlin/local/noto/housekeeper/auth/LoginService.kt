package local.noto.housekeeper.auth

import local.noto.housekeeper.exception.InvalidCredentialsException
import local.noto.housekeeper.user.AppUser
import local.noto.housekeeper.user.AppUserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val users: AppUserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(email: String, password: String): AppUser {
        // Same normalization as on registration, otherwise "Max@x.ch" would not find "max@x.ch"
        val normalizedEmail = email.trim().lowercase()

        // Unknown e-mail and wrong password throw the same exception, so nobody can probe which e-mails exist
        val user = users.findByEmail(normalizedEmail)
            ?: throw InvalidCredentialsException()

        // BCrypt salts every hash, so the plain password has to be checked with matches() instead of ==
        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        return user
    }
}
