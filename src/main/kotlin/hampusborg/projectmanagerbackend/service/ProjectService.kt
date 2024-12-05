package hampusborg.projectmanagerbackend.service

import hampusborg.projectmanagerbackend.dto.ProjectDto
import hampusborg.projectmanagerbackend.model.Project
import hampusborg.projectmanagerbackend.repository.ProjectRepository
import hampusborg.projectmanagerbackend.exception.ProjectNotFoundException
import org.springframework.stereotype.Service

@Service
class ProjectService(private val projectRepository: ProjectRepository) {

    fun createProject(projectDto: ProjectDto): ProjectDto {
        val project = Project(name = projectDto.name, description = projectDto.description)
        return mapToDto(projectRepository.save(project))
    }

    fun getAllProjects(): List<ProjectDto> = projectRepository.findAll().map { mapToDto(it) }

    fun getProjectById(projectId: String): ProjectDto {
        val project = findProjectById(projectId)
        return mapToDto(project)
    }

    fun deleteProject(id: String) {
        val project = findProjectById(id)
        projectRepository.delete(project)
    }

    fun updateProject(id: String, projectDto: ProjectDto): ProjectDto {
        val project = findProjectById(id).apply {
            name = projectDto.name
            description = projectDto.description
        }
        return mapToDto(projectRepository.save(project))
    }

    private fun findProjectById(id: String): Project {
        return projectRepository.findById(id).orElseThrow { ProjectNotFoundException("Project not found with ID: $id") }
    }

    private fun mapToDto(project: Project): ProjectDto {
        return ProjectDto(project.id, project.name, project.description)
    }
}