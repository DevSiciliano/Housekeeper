package local.noto.housekeeper.user

import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

// ### EXCEPTION NEEDS TO MOVE IN TO EXCEPTION PACKAGE WHEN CREATED ###
class EmailAlreadyRegisteredException(email: String) :
    RuntimeException("The E-Mail $email is already registered.")

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