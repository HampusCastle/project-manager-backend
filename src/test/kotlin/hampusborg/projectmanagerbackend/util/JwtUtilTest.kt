package hampusborg.projectmanagerbackend.util

import hampusborg.projectmanagerbackend.config.MongoTestConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestConfig::class)
@TestPropertySource(properties = ["JWT_SECRET=mock-secret-for-tests"])
class JwtUtilTest {

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @Test
    fun `test token generation`() {
        val token = jwtUtil.generateToken("testuser", "ROLE_USER")
        assertTrue(jwtUtil.isValidToken(token))
    }

    @Test
    fun `test token extraction`() {
        val token = jwtUtil.generateToken("testuser", "ROLE_USER")
        val username = jwtUtil.extractUsername(token)
        assertEquals("testuser", username)
    }
}