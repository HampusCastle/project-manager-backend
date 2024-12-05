package hampusborg.projectmanagerbackend.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PasswordUpdateRequest(
    @field:NotBlank(message = "Old password cannot be empty")
    val oldPassword: String,

    @field:NotBlank(message = "New password cannot be empty")
    @field:Size(min = 6, message = "New password must be at least 6 characters long")
    val newPassword: String
)