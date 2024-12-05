package hampusborg.projectmanagerbackend.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "tasks")
data class Task(
    val id: String? = null,
    var title: String,
    var description: String,
    val projectId: String,
    var status: String = "TODO"
)