package local.noto.housekeeper.auth

import local.noto.housekeeper.exception.EmailAlreadyRegisteredException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.*

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class RegistrationServiceTest(
    @Autowired private val registration: RegistrationService,
    @Autowired private val passwordEncoder: PasswordEncoder
) {

    @Test
    fun `saves passwort as a hash`() {
        val user = registration.register("Max", "max@test.com", "Password123")

        assertNotEquals("Test123", user.passwordHash)
        assertTrue(passwordEncoder.matches("Password123", user.passwordHash))
    }

    @Test
    fun `normalized a email correctly`() {
        val user = registration.register("Max", " Max@teSt.COM ", "Password123")

        assertEquals("max@test.com", user.email)
    }

    @Test
    fun `decline an exsting email for registration`() {
        val user1 = registration.register("Max", "max@test.com", "Password123")

        assertFailsWith<EmailAlreadyRegisteredException> {
            registration.register("Maxi", "MAX@test.com", "anderes456")
        }
    }
}