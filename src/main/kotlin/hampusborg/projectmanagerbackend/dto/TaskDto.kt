package hampusborg.projectmanagerbackend.dto

import jakarta.validation.constraints.NotBlank


data class TaskDto(
    val id: String?,

    @field:NotBlank(message = "Task title cannot be empty")
    val title: String?,

    @field:NotBlank(message = "Task description cannot be empty")
    val description: String?,

    @field:NotBlank(message = "Task status cannot be empty")
    val status: String?
)