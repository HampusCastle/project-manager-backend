package hampusborg.projectmanagerbackend.assertions

import hampusborg.projectmanagerbackend.config.MongoTestConfig
import hampusborg.projectmanagerbackend.request.LoginRequest
import hampusborg.projectmanagerbackend.request.UserRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.data.mongodb.core.MongoTemplate
import hampusborg.projectmanagerbackend.service.UserService
import hampusborg.projectmanagerbackend.model.User
import hampusborg.projectmanagerbackend.model.Role
import hampusborg.projectmanagerbackend.repository.UserRepository
import hampusborg.projectmanagerbackend.util.JwtUtil
import org.junit.jupiter.api.Assertions.*
import org.springframework.context.annotation.Import

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestConfig::class)
class AssertionsTest {

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    private val regularUser = User(
        username = "testuser",
        password = "password123",
        email = "testuser@example.com",
        role = Role.ROLE_USER
    )

    @BeforeEach
    fun setUp() {
        val passwordEncoder = BCryptPasswordEncoder()
        val encodedPassword = passwordEncoder.encode("password123")

        Mockito.`when`(userRepository.findByUsername("testuser"))
            .thenReturn(regularUser.copy(password = encodedPassword))
        Mockito.`when`(userRepository.save(any()))
            .thenReturn(regularUser.copy(password = encodedPassword))
        Mockito.`when`(jwtUtil.generateToken(any(), any()))
            .thenReturn("mock-jwt-token")

        mongoTemplate.save(regularUser)
    }

    @Test
    fun `test user service functionality with assertions`() {
        val user = userService.getUserProfile("testuser")
        assertNotNull(user)
        assertEquals("testuser", user.username)
        assertEquals(Role.ROLE_USER, user.role)
        assertEquals("testuser@example.com", user.email)

        val token = jwtUtil.generateToken(user.username, user.role.name)
        assertNotNull(token)
        assertTrue(token.startsWith("mock"))

        val passwordEncoder = BCryptPasswordEncoder()
        assertTrue(passwordEncoder.matches("password123", user.password))

        user.email = "newemail@example.com"
        mongoTemplate.save(user)
        val updatedUser = userService.getUserProfile("testuser")
        assertEquals("newemail@example.com", updatedUser.email)

        val newUserRequest = UserRequest(username = "newuser", password = "password123", email = "newuser@example.com")
        userService.registerUser(newUserRequest)

        val loginRequest = LoginRequest(username = "testuser", password = "password123")
        val loginToken = userService.loginUser(loginRequest)
        assertNotNull(loginToken)
        assertTrue(loginToken.startsWith("mock"))

        val updatedPassword = "newpassword123"
        val userToUpdate = userService.getUserProfile("testuser")
        userService.updatePassword(userToUpdate, updatedPassword)

        val updatedUserProfile = userService.getUserProfile("testuser")
        val newPasswordEncoder = BCryptPasswordEncoder()
        assertTrue(newPasswordEncoder.matches(updatedPassword, updatedUserProfile.password))

        val newLoginRequest = LoginRequest(username = "testuser", password = "newpassword123")
        val newLoginToken = userService.loginUser(newLoginRequest)
        assertNotNull(newLoginToken)
        assertTrue(newLoginToken.startsWith("mock"))
    }
}