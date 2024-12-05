package hampusborg.projectmanagerbackend.repository

import hampusborg.projectmanagerbackend.model.Task
import org.springframework.data.mongodb.repository.MongoRepository

interface TaskRepository : MongoRepository<Task, String> {

    fun findByProjectId(projectId: String): List<Task>

    fun findByProjectIdAndId(projectId: String, taskId: String): Task?
}