package hampusborg.projectmanagerbackend.security

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.MediaType
import hampusborg.projectmanagerbackend.ProjectManagerBackendApplication
import hampusborg.projectmanagerbackend.model.Role
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import hampusborg.projectmanagerbackend.repository.UserRepository
import hampusborg.projectmanagerbackend.model.User
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.boot.test.mock.mockito.MockBean
import com.jayway.jsonpath.JsonPath
import hampusborg.projectmanagerbackend.config.MongoTestConfig
import org.mockito.Mockito.`when`
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest(classes = [ProjectManagerBackendApplication::class])
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(MongoTestConfig::class)
@TestPropertySource(properties = ["JWT_SECRET=mock-secret-for-tests"])
class SecurityConfigTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    lateinit var userRepository: UserRepository

    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    @BeforeEach
    fun setUp() {
        val user = User(
            username = "testuser",
            password = bCryptPasswordEncoder.encode("password123"),
            email = "testuser@example.com",
            role = Role.ROLE_USER
        )

        `when`(userRepository.findByUsername("testuser")).thenReturn(user)
    }

    @Test
    fun `test user registration endpoint`() {
        val userRequest = """
            {
                "username": "testuser",
                "password": "password123",
                "email": "testuser@example.com"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/users/register")
                .content(userRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("User registered successfully"))
    }

    @Test
    fun `test login endpoint`() {
        val loginRequest = """
            {
                "username": "testuser",
                "password": "password123"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/users/login")
                .content(loginRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.role").value("ROLE_USER"))
    }

    @Test
    fun `test get user profile after login`() {
        val loginRequest = """
        {
            "username": "testuser",
            "password": "password123"
        }
    """.trimIndent()

        val loginResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/login")
                .content(loginRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val responseJson = loginResult.response.contentAsString
        val token = JsonPath.parse(responseJson).read<String>("$.token")
        assertNotNull(token)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/users/profile")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("testuser@example.com"))
    }
}