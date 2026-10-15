package local.noto.housekeeper.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class GlobalExceptionHandlerTest(
    @Autowired private val mockMvc: MockMvc
) {

    private val requestBody = """{"name":"Max","email":"max@test.com","password":"Password123"}"""

    @Test
    fun `returns 409 as problem detail when email is already registered`() {
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect { status { isCreated() } }

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isConflict() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.title") { value("E-mail already registered") }
            jsonPath("$.detail") { value("This e-mail is already registered.") }
        }
    }

    // No login endpoint yet, so the handler is called directly
    @Test
    fun `returns 401 as problem detail when credentials are invalid`() {
        val problem = GlobalExceptionHandler().handleInvalidCredentials()

        assertEquals(HttpStatus.UNAUTHORIZED.value(), problem.status)
        assertEquals("Invalid credentials", problem.title)
        assertEquals("Invalid e-mail or password.", problem.detail)
    }
}
