package hampusborg.projectmanagerbackend.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserRequest(
    @field:NotBlank(message = "Username cannot be empty")
    val username: String,

    @field:NotBlank(message = "Password cannot be empty")
    val password: String,

    @field:NotBlank(message = "Email cannot be empty")
    @field:Email(message = "Invalid email format")
    val email: String
)