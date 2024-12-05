package hampusborg.projectmanagerbackend.initializer

import hampusborg.projectmanagerbackend.model.Role
import hampusborg.projectmanagerbackend.model.User
import hampusborg.projectmanagerbackend.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        userRepository.deleteAll()

        val users = listOf(
            createUser("admin", "admin@projectmanager.com", "admin123", Role.ROLE_ADMIN),
            createUser("user", "user@projectmanager.com", "user123", Role.ROLE_USER)
        )

        userRepository.saveAll(users)
        println("Admin and regular users created")
    }

    private fun createUser(username: String, email: String, password: String, role: Role): User {
        return User(
            username = username,
            email = email,
            password = passwordEncoder.encode(password),
            role = role
        )
    }
}