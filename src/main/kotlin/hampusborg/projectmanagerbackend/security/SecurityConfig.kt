package hampusborg.projectmanagerbackend.security

import hampusborg.projectmanagerbackend.util.JwtAuthenticationFilter
import hampusborg.projectmanagerbackend.util.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtUtil: JwtUtil) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .cors { cors ->
                cors.configurationSource {
                    CorsConfiguration().apply {
                        allowedOrigins = listOf("http://localhost:3000")
                        allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        allowedHeaders = listOf("Authorization", "Content-Type", "*")
                        allowCredentials = true
                    }
                }
            }
            .authorizeHttpRequests { authRequest ->
                authRequest
                    .requestMatchers("/users/register", "/users/login").permitAll()
                    .requestMatchers("/projects/**", "/tasks/**", "/users/**").authenticated()
                    .requestMatchers("/users/{id}/delete").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}