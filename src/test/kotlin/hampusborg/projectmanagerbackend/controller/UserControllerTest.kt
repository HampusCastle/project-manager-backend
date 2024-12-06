package hampusborg.projectmanagerbackend.controller

import hampusborg.projectmanagerbackend.config.MongoTestConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(MongoTestConfig::class)
@TestPropertySource(properties = ["JWT_SECRET=mock-secret-for-tests"])
class UserControllerTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `test register user with valid data`() {
        val userRequest = """{"username": "testUser", "password": "Test1234", "email": "test@example.com"}"""

        mockMvc.post("/users/register") {
            content = userRequest
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect { status().isOk }
            .andExpect { content().string("User registered successfully") }
    }

    @Test
    fun `test login with valid credentials`() {
        val loginRequest = """{"username": "testUser", "password": "Test1234"}"""

        mockMvc.post("/users/login") {
            content = loginRequest
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.token").exists() }
            .andExpect { jsonPath("$.role").value("ROLE_USER") }
    }

    @Test
    fun `test get user profile`() {
        val token = "Bearer dummy_token"

        mockMvc.get("/users/profile") {
            header("Authorization", token)
        }
            .andExpect { status().isOk }
            .andExpect { jsonPath("$.username").value("testUser") }
    }

    @Test
    fun `test update password`() {
        val passwordUpdateRequest = """{"oldPassword": "Test1234", "newPassword": "NewPassword123"}"""

        mockMvc.put("/users/password") {
            content = passwordUpdateRequest
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer dummy_token")
        }
            .andExpect { status().isOk }
            .andExpect { content().string("Password updated successfully") }
    }

    @Test
    fun `test delete user by admin`() {
        val userId = "123"

        mockMvc.delete("/users/$userId") {
            header("Authorization", "Bearer admin_token")
        }
            .andExpect { status().isOk }
            .andExpect { content().string("User deleted successfully") }
    }
}