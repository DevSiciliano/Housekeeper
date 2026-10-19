package local.noto.housekeeper.user

import local.noto.housekeeper.auth.RegistrationService
import local.noto.housekeeper.security.JwtConfig
import local.noto.housekeeper.security.TokenService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class CurrentUserControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val registration: RegistrationService,
    @Autowired private val tokens: TokenService,
    @Autowired private val encoder: JwtEncoder
) {

    private lateinit var registered: AppUser

    @BeforeEach
    fun setUp() {
        registered = registration.register("Max", "max@test.com", "Password123")
    }

    @Test
    fun `returns the user of a valid token`() {
        mockMvc.get("/api/users/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer ${tokens.createAccessToken(registered)}")
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(registered.id.toString()) }
            jsonPath("$.email") { value("max@test.com") }
            jsonPath("$.passwordHash") { doesNotExist() }
        }
    }

    @Test
    fun `returns 401 without a token`() {
        mockMvc.get("/api/users/me").andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `returns 401 for a token signed with another key`() {
        val foreignKey = Base64.getEncoder().encodeToString(Random.nextBytes(32))
        val token = TokenService(JwtConfig(foreignKey).jwtEncoder()).createAccessToken(registered)

        mockMvc.get("/api/users/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `returns 401 for an expired token`() {
        // Spring allows 60 seconds of clock skew, so the token has to be clearly expired
        val expiresAt = Instant.now().minus(5, ChronoUnit.MINUTES)
        val claims = JwtClaimsSet.builder()
            .subject(registered.id.toString())
            .issuedAt(expiresAt.minus(15, ChronoUnit.MINUTES))
            .expiresAt(expiresAt)
            .build()
        val header = JwsHeader.with(MacAlgorithm.HS256).build()
        val token = encoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue

        mockMvc.get("/api/users/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `returns 401 when the account of the token no longer exists`() {
        // Never saved, so the token is genuine but points to no account
        val ghost = AppUser("Ghost", "ghost@test.com", "hash", Instant.now())

        mockMvc.get("/api/users/me") {
            header(HttpHeaders.AUTHORIZATION, "Bearer ${tokens.createAccessToken(ghost)}")
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.title") { value("Unknown account") }
        }
    }
}
