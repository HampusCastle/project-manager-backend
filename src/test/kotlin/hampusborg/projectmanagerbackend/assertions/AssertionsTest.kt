package hampusborg.projectmanagerbackend.assertions

import hampusborg.projectmanagerbackend.request.LoginRequest
import hampusborg.projectmanagerbackend.request.UserRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import hampusborg.projectmanagerbackend.service.UserService
import hampusborg.projectmanagerbackend.model.User
import hampusborg.projectmanagerbackend.model.Role
import hampusborg.projectmanagerbackend.repository.UserRepository
import hampusborg.projectmanagerbackend.util.JwtUtil
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest
class AssertionsTest {

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var userService: UserService

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
        val updatedUser = userService.getUserProfile("testuser")
        assertEquals("newemail@example.com", updatedUser.email)

        val newUserRequest = UserRequest(username = "newuser", password = "password123", email = "newuser@example.com")
        userService.registerUser(newUserRequest)

        val loginRequest = LoginRequest(username = "testuser", password = "password123")
        val loginToken = userService.loginUser(loginRequest)
        assertNotNull(loginToken)
        assertTrue(loginToken.startsWith("mock"))

        val updatedPassword = "newpassword123"
        val userToUpdate = userService.getUserProfile("testuser")  // Fetch the user first
        userService.updatePassword(userToUpdate, updatedPassword)  // Send the user with the new password

        val updatedUserProfile = userService.getUserProfile("testuser")
        val newPasswordEncoder = BCryptPasswordEncoder()
        assertTrue(newPasswordEncoder.matches(updatedPassword, updatedUserProfile.password))  // Verify password change

        val newLoginRequest = LoginRequest(username = "testuser", password = "newpassword123")
        val newLoginToken = userService.loginUser(newLoginRequest)
        assertNotNull(newLoginToken)
        assertTrue(newLoginToken.startsWith("mock"))
    }
}