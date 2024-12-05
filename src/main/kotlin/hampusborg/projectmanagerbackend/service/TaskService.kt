package hampusborg.projectmanagerbackend.service

import hampusborg.projectmanagerbackend.dto.TaskDto
import hampusborg.projectmanagerbackend.model.Task
import hampusborg.projectmanagerbackend.repository.TaskRepository
import hampusborg.projectmanagerbackend.exception.TaskNotFoundException
import org.springframework.stereotype.Service

@Service
class TaskService(private val taskRepository: TaskRepository) {

    fun getTaskById(projectId: String, taskId: String): Task? {
        return taskRepository.findByProjectIdAndId(projectId, taskId)
    }
    fun getTasksByProject(projectId: String): List<TaskDto> {
        return taskRepository.findByProjectId(projectId).map { mapToDto(it) }
    }

    fun createTask(projectId: String, taskDto: TaskDto): TaskDto {
        validateTaskDto(taskDto)
        val task = Task(title = taskDto.title!!, description = taskDto.description!!, projectId = projectId, status = taskDto.status ?: "TODO")
        return mapToDto(taskRepository.save(task))
    }

    fun updateTaskStatus(projectId: String, taskId: String, taskDto: TaskDto): TaskDto {
        val task = findTaskByProjectAndId(projectId, taskId).apply {
            status = taskDto.status ?: status
            title = taskDto.title ?: title
            description = taskDto.description ?: description
        }
        taskRepository.save(task)
        return mapToDto(task)
    }

    fun deleteTask(projectId: String, taskId: String) {
        val task = findTaskByProjectAndId(projectId, taskId)
        taskRepository.delete(task)
    }

    private fun findTaskByProjectAndId(projectId: String, taskId: String): Task {
        return taskRepository.findByProjectIdAndId(projectId, taskId)
            ?: throw TaskNotFoundException("Task not found for projectId: $projectId and taskId: $taskId")
    }

    private fun mapToDto(task: Task): TaskDto {
        return TaskDto(task.id, task.title, task.description, task.status)
    }

    private fun validateTaskDto(taskDto: TaskDto) {
        if (taskDto.title.isNullOrBlank() || taskDto.description.isNullOrBlank()) {
            throw IllegalArgumentException("Title and description are required")
        }
    }
}