package local.noto.housekeeper.security

import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import kotlin.random.Random
import kotlin.test.*

// Plain unit tests: JwtConfig only needs the secret, no Spring context required
class JwtConfigTest {

    private val config = JwtConfig(randomSecret())

    @Test
    fun `decoder accepts a token signed by the encoder`() {
        val token = encode(config.jwtEncoder(), subject = "42")

        val jwt = config.jwtDecoder().decode(token)

        assertEquals("42", jwt.subject)
    }

    @Test
    fun `decoder rejects a token signed with another key`() {
        val foreignToken = encode(JwtConfig(randomSecret()).jwtEncoder(), subject = "42")

        assertFailsWith<JwtException> {
            config.jwtDecoder().decode(foreignToken)
        }
    }

    @Test
    fun `decoder rejects an expired token`() {
        // Spring allows 60 seconds of clock skew, so the token has to be clearly expired
        val token = encode(config.jwtEncoder(), subject = "42", expiresAt = Instant.now().minus(5, ChronoUnit.MINUTES))

        assertFailsWith<JwtException> {
            config.jwtDecoder().decode(token)
        }
    }

    @Test
    fun `declines a secret shorter than 32 bytes`() {
        val shortSecret = Base64.getEncoder().encodeToString(Random.nextBytes(16))

        assertFailsWith<IllegalArgumentException> {
            JwtConfig(shortSecret)
        }
    }

    private fun randomSecret(): String = Base64.getEncoder().encodeToString(Random.nextBytes(32))

    private fun encode(
        encoder: JwtEncoder,
        subject: String,
        expiresAt: Instant = Instant.now().plus(15, ChronoUnit.MINUTES)
    ): String {
        val claims = JwtClaimsSet.builder()
            .subject(subject)
            .issuedAt(expiresAt.minus(15, ChronoUnit.MINUTES))
            .expiresAt(expiresAt)
            .build()
        // The header has to name HS256 explicitly, otherwise the encoder defaults to RS256 and finds no matching key
        val header = JwsHeader.with(MacAlgorithm.HS256).build()
        return encoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }
}
