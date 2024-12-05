package hampusborg.projectmanagerbackend.dto

import hampusborg.projectmanagerbackend.model.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class UserDto(
    val id: String?,

    @field:NotEmpty(message = "Username cannot be empty")
    val username: String,

    @field:NotEmpty(message = "Email cannot be empty")
    @field:Email(message = "Email should be valid")
    val email: String
) {
    constructor(user: User) : this(
        id = user.id,
        username = user.username,
        email = user.email
    )
}