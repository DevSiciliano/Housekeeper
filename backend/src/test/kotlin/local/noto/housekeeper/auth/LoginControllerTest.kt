package local.noto.housekeeper.auth

import com.jayway.jsonpath.JsonPath
import local.noto.housekeeper.user.AppUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class LoginControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val registration: RegistrationService,
    @Autowired private val decoder: JwtDecoder
) {

    private lateinit var registered: AppUser

    @BeforeEach
    fun setUp() {
        registered = registration.register("Max", "max@test.com", "Password123")
    }

    @Test
    fun `returns the user for correct credentials`() {
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"Max@Test.com","password":"Password123"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.user.name") { value("Max") }
            jsonPath("$.user.email") { value("max@test.com") }
            jsonPath("$.user.passwordHash") { doesNotExist() }
        }
    }

    @Test
    fun `returns an access token for the logged-in user`() {
        val body = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"max@test.com","password":"Password123"}"""
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        val token = JsonPath.read<String>(body, "$.accessToken")

        // Decoding also verifies the signature, so the token was signed with the configured key
        assertEquals(registered.id.toString(), decoder.decode(token).subject)
    }

    @Test
    fun `returns 401 for a wrong password`() {
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"max@test.com","password":"WrongPassword"}"""
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.detail") { value("Invalid e-mail or password.") }
        }
    }

    @Test
    fun `returns the same 401 for an unknown email`() {
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"nobody@test.com","password":"Password123"}"""
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.detail") { value("Invalid e-mail or password.") }
        }
    }

    @Test
    fun `returns 400 for a blank password`() {
        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"max@test.com","password":""}"""
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
