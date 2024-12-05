package hampusborg.projectmanagerbackend.controller

import hampusborg.projectmanagerbackend.dto.*
import hampusborg.projectmanagerbackend.service.TaskService
import hampusborg.projectmanagerbackend.repository.TaskRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects/{projectId}/tasks")
class TaskController(
    private val taskService: TaskService,
    private val taskRepository: TaskRepository
) {

    private fun handleValidationErrors(bindingResult: BindingResult): ResponseEntity<Any>? {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.joinToString(", ") { it.defaultMessage ?: "Invalid data" }
            return ResponseEntity.badRequest().body(ErrorDto(errors))
        }
        return null
    }

    @GetMapping("/{taskId}")
    fun getTaskById(@PathVariable projectId: String, @PathVariable taskId: String): ResponseEntity<TaskDto> {
        val task = taskRepository.findByProjectIdAndId(projectId, taskId)
        return if (task != null) {
            val taskDto = TaskDto(
                id = task.id,
                title = task.title,
                description = task.description,
                status = task.status
            )
            ResponseEntity.ok(taskDto)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

    @GetMapping
    fun getTasksByProject(@PathVariable projectId: String): ResponseEntity<List<TaskDto>> =
        ResponseEntity.ok(taskService.getTasksByProject(projectId))

    @PostMapping
    fun createTask(
        @PathVariable projectId: String,
        @Valid @RequestBody taskDto: TaskDto,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        handleValidationErrors(bindingResult)?.let { return it }
        return ResponseEntity.ok(taskService.createTask(projectId, taskDto))
    }

    @PutMapping("/{taskId}")
    fun updateTaskStatus(
        @PathVariable projectId: String,
        @PathVariable taskId: String,
        @Valid @RequestBody taskDto: TaskDto,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        handleValidationErrors(bindingResult)?.let { return it }
        return ResponseEntity.ok(taskService.updateTaskStatus(projectId, taskId, taskDto))
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(@PathVariable projectId: String, @PathVariable taskId: String): ResponseEntity<String> {
        println("Attempting to delete task with projectId: $projectId and taskId: $taskId")
        val task = taskService.getTaskById(projectId, taskId)
        return if (task != null) {
            taskService.deleteTask(projectId, taskId)
            ResponseEntity.ok("Task deleted successfully")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found for projectId: $projectId and taskId: $taskId")
        }
    }
}