package hampusborg.projectmanagerbackend.service

import hampusborg.projectmanagerbackend.config.MongoTestConfig
import hampusborg.projectmanagerbackend.request.LoginRequest
import hampusborg.projectmanagerbackend.request.UserRequest
import hampusborg.projectmanagerbackend.model.User
import hampusborg.projectmanagerbackend.model.Role
import hampusborg.projectmanagerbackend.repository.UserRepository
import hampusborg.projectmanagerbackend.util.JwtUtil
import hampusborg.projectmanagerbackend.exception.UserNotFoundException
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestConfig::class)
@TestPropertySource(properties = ["JWT_SECRET=mock-secret-for-tests"])
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    @MockBean private val userRepository: UserRepository,
    @MockBean private val passwordEncoder: PasswordEncoder,
    @MockBean private val jwtUtil: JwtUtil
) {
    @Test
    fun `test registerUser successfully registers a new user`() {
        val userRequest = UserRequest(username = "testuser", password = "password123", email = "test@user.com")

        whenever(passwordEncoder.encode(eq("password123"))).thenReturn("hashedpassword")
        whenever(userRepository.save(any<User>())).thenReturn(User(username = "testuser", password = "hashedpassword", email = "test@user.com"))

        userService.registerUser(userRequest)

        verify(userRepository).save(any())
    }

    @Test
    fun `test loginUser returns token when credentials are valid`() {
        val loginRequest = LoginRequest(username = "testuser", password = "password123")
        val user = User(username = "testuser", password = "hashedpassword", email = "test@user.com", role = Role.ROLE_USER)

        whenever(userRepository.findByUsername(eq("testuser"))).thenReturn(user)
        whenever(passwordEncoder.matches(eq("password123"), eq("hashedpassword"))).thenReturn(true)
        whenever(jwtUtil.generateToken(eq(user.username), eq(user.role.name)))
            .thenReturn("mocked-jwt-token")

        val token = userService.loginUser(loginRequest)

        assertEquals("mocked-jwt-token", token)
        verify(userRepository).findByUsername(eq("testuser"))
    }

    @Test
    fun `test loginUser throws Exception for invalid credentials`() {
        val loginRequest = LoginRequest(username = "testuser", password = "wrongpassword")
        val user = User(username = "testuser", password = "hashedpassword", email = "test@user.com", role = Role.ROLE_USER)

        whenever(userRepository.findByUsername(eq("testuser"))).thenReturn(user)
        whenever(passwordEncoder.matches(eq("wrongpassword"), eq("hashedpassword"))).thenReturn(false)

        assertFailsWith<Exception> {
            userService.loginUser(loginRequest)
        }
    }

    @Test
    fun `test getUserProfile returns user successfully`() {
        val username = "testuser"
        val user = User(username = "testuser", password = "hashedpassword", email = "test@user.com", role = Role.ROLE_USER)

        whenever(userRepository.findByUsername(eq(username))).thenReturn(user)

        val result = userService.getUserProfile(username)

        assertEquals("testuser", result.username)
        assertEquals("test@user.com", result.email)

        verify(userRepository).findByUsername(eq(username))
    }

    @Test
    fun `test getUserProfile throws UserNotFoundException for non-existing user`() {
        val username = "nonexistentuser"

        whenever(userRepository.findByUsername(eq(username))).thenReturn(null)

        assertFailsWith<UserNotFoundException> {
            userService.getUserProfile(username)
        }
    }

    @Test
    fun `test updatePassword successfully updates user password`() {
        val user = User(username = "testuser", password = "hashedpassword", email = "test@user.com", role = Role.ROLE_USER)
        val newPassword = "newpassword123"

        whenever(userRepository.findByUsername(eq("testuser"))).thenReturn(user)
        whenever(passwordEncoder.encode(eq(newPassword))).thenReturn("newhashedpassword")

        userService.updatePassword(user, newPassword)

        assertEquals("newhashedpassword", user.password)

        verify(userRepository).save(eq(user))
    }

    @Test
    fun `test deleteUser successfully deletes user`() {
        val userId = "1"
        val user = User(username = "testuser", password = "hashedpassword", email = "test@user.com", role = Role.ROLE_USER)

        whenever(userRepository.findById(eq(userId))).thenReturn(Optional.of(user))
        Mockito.doNothing().`when`(userRepository).deleteById(eq(userId))

        userService.deleteUser(userId)

        verify(userRepository).deleteById(eq(userId))
    }
}