package hampusborg.projectmanagerbackend.service

import hampusborg.projectmanagerbackend.config.MongoTestConfig
import hampusborg.projectmanagerbackend.dto.TaskDto
import hampusborg.projectmanagerbackend.exception.TaskNotFoundException
import hampusborg.projectmanagerbackend.model.Task
import hampusborg.projectmanagerbackend.repository.TaskRepository
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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestConfig::class)
@TestPropertySource(properties = ["JWT_SECRET=mock-secret-for-tests"])
class TaskServiceTest @Autowired constructor(
    private val taskService: TaskService,
    @MockBean private val taskRepository: TaskRepository
) {

    @Test
    fun `test getTaskById returns task successfully`() {
        val task = Task(id = "1", title = "Task 1", description = "Task description", projectId = "1", status = "TODO")
        val taskId = "1"
        val projectId = "1"

        Mockito.`when`(taskRepository.findByProjectIdAndId(eq(projectId), eq(taskId)))
            .thenReturn(task)

        val result = taskService.getTaskById(projectId, taskId)

        assertEquals("Task 1", result?.title)
        assertEquals("Task description", result?.description)
        assertEquals("TODO", result?.status)

        Mockito.verify(taskRepository).findByProjectIdAndId(eq(projectId), eq(taskId))
    }

    @Test
    fun `test getTasksByProject returns tasks successfully`() {
        val tasks = listOf(
            Task(id = "1", title = "Task 1", description = "Task description 1", projectId = "1", status = "TODO"),
            Task(id = "2", title = "Task 2", description = "Task description 2", projectId = "1", status = "IN_PROGRESS")
        )
        val projectId = "1"

        Mockito.`when`(taskRepository.findByProjectId(eq(projectId)))
            .thenReturn(tasks)

        val result = taskService.getTasksByProject(projectId)

        assertEquals(2, result.size)
        assertEquals("Task 1", result[0].title)
        assertEquals("Task 2", result[1].title)

        Mockito.verify(taskRepository).findByProjectId(eq(projectId))
    }

    @Test
    fun `test createTask throws IllegalArgumentException if title is missing`() {
        val taskDto = TaskDto(id = null, title = "", description = "Task description", status = "TODO")
        val projectId = "1"

        assertFailsWith<IllegalArgumentException> {
            taskService.createTask(projectId, taskDto)
        }
    }

    @Test
    fun `test createTask successfully creates task`() {
        val taskDto = TaskDto(id = null, title = "New Task", description = "Task description", status = "TODO")
        val projectId = "1"
        val task = Task(id = "1", title = "New Task", description = "Task description", projectId = projectId, status = "TODO")

        Mockito.`when`(taskRepository.save(any()))
            .thenReturn(task)

        val result = taskService.createTask(projectId, taskDto)

        assertEquals("New Task", result.title)
        assertEquals("Task description", result.description)
        assertEquals("TODO", result.status)

        Mockito.verify(taskRepository).save(any())
    }

    @Test
    fun `test updateTaskStatus successfully updates task`() {
        val taskDto = TaskDto(id = "1", title = "Updated Task", description = "Updated description", status = "IN_PROGRESS")
        val projectId = "1"
        val taskId = "1"
        val task = Task(id = taskId, title = "Old Task", description = "Old description", projectId = projectId, status = "TODO")

        Mockito.`when`(taskRepository.findByProjectIdAndId(eq(projectId), eq(taskId)))
            .thenReturn(task)

        Mockito.`when`(taskRepository.save(any()))
            .thenReturn(task)

        val result = taskService.updateTaskStatus(projectId, taskId, taskDto)

        assertEquals("Updated Task", result.title)
        assertEquals("Updated description", result.description)
        assertEquals("IN_PROGRESS", result.status)

        Mockito.verify(taskRepository).save(any())
    }

    @Test
    fun `test updateTaskStatus throws TaskNotFoundException if task not found`() {
        val projectId = "1"
        val taskId = "1"
        val taskDto = TaskDto(id = taskId, title = "Updated Task", description = "Updated description", status = "IN_PROGRESS")

        Mockito.`when`(taskRepository.findByProjectIdAndId(eq(projectId), eq(taskId)))
            .thenReturn(null)

        assertFailsWith<TaskNotFoundException> {
            taskService.updateTaskStatus(projectId, taskId, taskDto)
        }
    }

    @Test
    fun `test deleteTask successfully deletes task`() {
        val projectId = "1"
        val taskId = "1"
        val task = Task(id = taskId, title = "Task to delete", description = "Description", projectId = projectId, status = "TODO")

        Mockito.`when`(taskRepository.findByProjectIdAndId(eq(projectId), eq(taskId)))
            .thenReturn(task)

        Mockito.doNothing().`when`(taskRepository).delete(any())

        taskService.deleteTask(projectId, taskId)

        Mockito.verify(taskRepository).delete(any())
    }

    @Test
    fun `test deleteTask throws TaskNotFoundException if task not found`() {
        val projectId = "1"
        val taskId = "1"

        Mockito.`when`(taskRepository.findByProjectIdAndId(eq(projectId), eq(taskId)))
            .thenReturn(null)

        assertFailsWith<TaskNotFoundException> {
            taskService.deleteTask(projectId, taskId)
        }
    }
}