package local.noto.housekeeper.security

import local.noto.housekeeper.user.AppUser
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.Base64
import kotlin.random.Random
import kotlin.test.*

// Plain unit tests like JwtConfigTest: the real encoder and decoder, but no Spring context
class TokenServiceTest {

    private val config = JwtConfig(Base64.getEncoder().encodeToString(Random.nextBytes(32)))
    private val tokens = TokenService(config.jwtEncoder())

    // The token only needs the id, so the user does not have to be stored in the database
    private val user = AppUser("Max", "max@test.com", "hash", Instant.now())

    @Test
    fun `token carries the user id as subject`() {
        val jwt = config.jwtDecoder().decode(tokens.createAccessToken(user))

        assertEquals(user.id.toString(), jwt.subject)
        assertEquals(TokenService.ISSUER, jwt.getClaimAsString("iss"))
    }

    @Test
    fun `token expires after 15 minutes`() {
        val jwt = config.jwtDecoder().decode(tokens.createAccessToken(user))

        assertEquals(Duration.ofMinutes(15), Duration.between(jwt.issuedAt, jwt.expiresAt))
    }

    @Test
    fun `token does not contain the e-mail or the password hash`() {
        val jwt = config.jwtDecoder().decode(tokens.createAccessToken(user))

        assertFalse(jwt.claims.values.any { it == user.email || it == user.passwordHash })
    }
}
