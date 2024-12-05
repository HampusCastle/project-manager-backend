package hampusborg.projectmanagerbackend.service

import hampusborg.projectmanagerbackend.request.LoginRequest
import hampusborg.projectmanagerbackend.dto.UserDto
import hampusborg.projectmanagerbackend.request.UserRequest
import hampusborg.projectmanagerbackend.model.User
import hampusborg.projectmanagerbackend.repository.UserRepository
import hampusborg.projectmanagerbackend.util.JwtUtil
import hampusborg.projectmanagerbackend.exception.UserNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository, private val jwtUtil: JwtUtil, private val passwordEncoder: PasswordEncoder) {

    fun registerUser(userRequest: UserRequest) {
        val hashedPassword = passwordEncoder.encode(userRequest.password)
        val user = User(username = userRequest.username, password = hashedPassword, email = userRequest.email)
        userRepository.save(user)
    }

    fun loginUser(loginRequest: LoginRequest): String {
        val user = userRepository.findByUsername(loginRequest.username)
        if (user != null && passwordEncoder.matches(loginRequest.password, user.password)) {
            return jwtUtil.generateToken(user.username, user.role.name)
        }
        throw Exception("Invalid credentials")
    }

    fun getUserProfile(username: String): User {
        return userRepository.findByUsername(username) ?: throw UserNotFoundException("User not found")
    }

    fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username) ?: throw UserNotFoundException("User not found")
    }

    fun checkPassword(user: User, oldPassword: String): Boolean {
        return passwordEncoder.matches(oldPassword, user.password)
    }

    fun updatePassword(user: User, newPassword: String) {
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
    }

    fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { UserDto(it) }
    }

    fun deleteUser(id: String) {
        userRepository.deleteById(id)
    }
}