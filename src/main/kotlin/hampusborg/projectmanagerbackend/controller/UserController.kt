package hampusborg.projectmanagerbackend.controller

import hampusborg.projectmanagerbackend.dto.*
import hampusborg.projectmanagerbackend.model.User
import hampusborg.projectmanagerbackend.service.UserService
import hampusborg.projectmanagerbackend.util.JwtUtil
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService, private val jwtUtil: JwtUtil) {

    private fun handleValidationErrors(bindingResult: BindingResult): ResponseEntity<Any>? {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.joinToString(", ") { it.defaultMessage ?: "Invalid data" }
            return ResponseEntity.badRequest().body(ErrorDto(errors))
        }
        return null
    }

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody userRequest: UserRequest, bindingResult: BindingResult): ResponseEntity<out Any>? {
        val validationError = handleValidationErrors(bindingResult)
        if (validationError != null) {
            return validationError
        }

        userService.registerUser(userRequest)
        return ResponseEntity.ok("User registered successfully")
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest, bindingResult: BindingResult): ResponseEntity<Any> {
        val validationError = handleValidationErrors(bindingResult)
        if (validationError != null) {
            return validationError
        }

        val jwt = userService.loginUser(loginRequest)
        val role = userService.getUserProfile(loginRequest.username).role.name
        return ResponseEntity.ok(JwtResponse(jwt, role))
    }

    @GetMapping("/profile")
    fun getProfile(@RequestHeader("Authorization") token: String): ResponseEntity<User> {
        val username = jwtUtil.extractUsername(token.removePrefix("Bearer "))
        val user = userService.getUserProfile(username)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/password")
    fun updatePassword(
        @Valid @RequestBody passwordUpdateRequest: PasswordUpdateRequest,
        bindingResult: BindingResult,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<out Any>? {
        val validationError = handleValidationErrors(bindingResult)
        if (validationError != null) {
            return validationError
        }

        val username = jwtUtil.extractUsername(token.removePrefix("Bearer "))
        val user = userService.getUserByUsername(username)

        if (!userService.checkPassword(user, passwordUpdateRequest.oldPassword)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Old password is incorrect")
        }

        userService.updatePassword(user, passwordUpdateRequest.newPassword)
        return ResponseEntity.ok("Password updated successfully")
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDto>> = ResponseEntity.ok(userService.getAllUsers())

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: String): ResponseEntity<String> {
        userService.deleteUser(id)
        return ResponseEntity.ok("User deleted successfully")
    }
}