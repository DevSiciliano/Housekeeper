package local.noto.housekeeper.user

import jakarta.transaction.Transactional
import local.noto.housekeeper.exception.EmailAlreadyRegisteredException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RegistrationService(
    private val users: AppUserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // Transactional provides the security to roll back if something failes while register
    @Transactional
    fun register(name: String, email: String, password: String): AppUser {
        val normalizedEmail = email.trim().lowercase()

        // Call exception if email already exists
        if (users.existsByEmail(normalizedEmail)) {
            throw EmailAlreadyRegisteredException(normalizedEmail)
        }

        return users.save(
            AppUser(
                name = name.trim(),
                email = normalizedEmail,
                passwordHash = passwordEncoder.encode(password)!!,
                createdAt = Instant.now(),
            )
        )
    }
}