package local.noto.housekeeper.user

import local.noto.housekeeper.exception.InvalidCredentialsException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.*

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class LoginServiceTest(
    @Autowired private val login: LoginService,
    @Autowired private val registration: RegistrationService
) {

    private lateinit var registered: AppUser

    // Every test starts with one known account; @Transactional rolls it back afterwards
    @BeforeEach
    fun setUp() {
        registered = registration.register("Max", "max@test.com", "Password123")
    }

    @Test
    fun `returns the user for correct credentials`() {
        val user = login.login("max@test.com", "Password123")

        assertEquals(registered.id, user.id)
    }

    @Test
    fun `normalizes the email before the lookup`() {
        val user = login.login(" MAX@Test.com ", "Password123")

        assertEquals(registered.id, user.id)
    }

    @Test
    fun `declines an unknown email`() {
        assertFailsWith<InvalidCredentialsException> {
            login.login("nobody@test.com", "Password123")
        }
    }

    @Test
    fun `declines a wrong password`() {
        assertFailsWith<InvalidCredentialsException> {
            login.login("max@test.com", "WrongPassword")
        }
    }
}
