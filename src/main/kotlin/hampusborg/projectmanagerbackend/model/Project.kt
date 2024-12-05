package hampusborg.projectmanagerbackend.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "projects")
data class Project(
    val id: String? = null,
    var name: String,
    var description: String
)