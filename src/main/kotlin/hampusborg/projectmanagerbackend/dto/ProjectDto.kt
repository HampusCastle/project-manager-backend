package hampusborg.projectmanagerbackend.dto

import jakarta.validation.constraints.NotBlank

data class ProjectDto(
    val id: String?,

    @field:NotBlank(message = "Project name cannot be empty")
    val name: String,

    @field:NotBlank(message = "Project description cannot be empty")
    val description: String
)