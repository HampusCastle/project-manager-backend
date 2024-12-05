package hampusborg.projectmanagerbackend.response

import jakarta.validation.constraints.NotEmpty

data class JwtResponse(
    @field:NotEmpty(message = "Token cannot be empty")
    val token: String,

    @field:NotEmpty(message = "Role cannot be empty")
    val role: String
)