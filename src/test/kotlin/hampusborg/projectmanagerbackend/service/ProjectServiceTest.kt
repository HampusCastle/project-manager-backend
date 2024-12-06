package hampusborg.projectmanagerbackend.service

import hampusborg.projectmanagerbackend.config.MongoTestConfig
import hampusborg.projectmanagerbackend.model.Project
import hampusborg.projectmanagerbackend.dto.ProjectDto
import hampusborg.projectmanagerbackend.repository.ProjectRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.util.*
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestConfig::class)
@TestPropertySource(properties = ["JWT_SECRET=mock-secret-for-tests"])
class ProjectServiceTest @Autowired constructor(
    private val projectService: ProjectService,
    @MockBean private val projectRepository: ProjectRepository
) {

    @Test
    fun `test create project`() {
        val project = Project(id = "1", name = "New Project", description = "Test project")
        val projectDto = ProjectDto(id = "1", name = "New Project", description = "Test project")

        Mockito.`when`(projectRepository.save(any()))
            .thenReturn(project)

        val result = projectService.createProject(projectDto)

        assertEquals("New Project", result.name)
        assertEquals("Test project", result.description)

        Mockito.verify(projectRepository).save(any())
    }

    @Test
    fun `test get all projects`() {
        val projects = listOf(
            Project(id = "1", name = "Project 1", description = "First project"),
            Project(id = "2", name = "Project 2", description = "Second project")
        )

        Mockito.`when`(projectRepository.findAll())
            .thenReturn(projects)

        val result = projectService.getAllProjects()

        assertEquals(2, result.size)
        assertEquals("Project 1", result[0].name)
        assertEquals("Project 2", result[1].name)

        Mockito.verify(projectRepository).findAll()
    }

    @Test
    fun `test get project by id`() {
        val project = Project(id = "1", name = "Project 1", description = "First project")
        val projectId = "1"

        Mockito.`when`(projectRepository.findById(eq(projectId)))
            .thenReturn(Optional.of(project))

        val result = projectService.getProjectById(projectId)

        assertEquals("Project 1", result.name)
        assertEquals("First project", result.description)

        Mockito.verify(projectRepository).findById(eq(projectId))
    }

    @Test
    fun `test delete project`() {
        val projectId = "1"
        val project = Project(id = projectId, name = "Project 1", description = "First project")

        Mockito.`when`(projectRepository.findById(eq(projectId)))
            .thenReturn(Optional.of(project))

        Mockito.doNothing().`when`(projectRepository).delete(eq(project))

        projectService.deleteProject(projectId)

        Mockito.verify(projectRepository).delete(eq(project))
    }
}