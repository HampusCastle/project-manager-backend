package hampusborg.projectmanagerbackend.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Username cannot be empty")
    val username: String,

    @field:NotBlank(message = "Password cannot be empty")
    val password: String
)