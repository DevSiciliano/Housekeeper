package local.noto.housekeeper.user

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import kotlin.test.*

@DataJpaTest
@ActiveProfiles("local")
class AppUserRepositoryTest(@Autowired private val users: AppUserRepository) {

    @Test
    fun `finds a user by his email`() {
        val max = users.save(AppUser("Max", "max.mustermann@test.com", "hash", Instant.now()))

        assertEquals(max.email, users.findByEmail("max.mustermann@test.com")?.email)
        assertTrue(users.existsByEmail("max.mustermann@test.com"))
    }

    @Test
    fun `recives NULL if email not found`() =
        assertNull(users.findByEmail("max.testmann@test.com"))

}