package hampusborg.projectmanagerbackend.util

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
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