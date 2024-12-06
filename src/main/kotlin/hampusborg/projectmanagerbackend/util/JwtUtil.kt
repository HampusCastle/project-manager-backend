package hampusborg.projectmanagerbackend.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtUtil {

    @Value("\${spring.security.jwt.secret}")
    private lateinit var jwtSecret: String

    private val secretKey: Key by lazy {
        if (jwtSecret.length >= 32) {
            Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        } else {
            Keys.secretKeyFor(SignatureAlgorithm.HS256)
        }
    }
    fun generateToken(username: String, role: String): String {
        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun isValidToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun extractUsername(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }
}