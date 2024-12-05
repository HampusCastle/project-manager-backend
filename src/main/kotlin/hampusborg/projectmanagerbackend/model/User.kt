package hampusborg.projectmanagerbackend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(
    @Id var id: String? = null,
    @Indexed(unique = true) val username: String,
    var password: String,
    var email: String,
    val role: Role = Role.ROLE_USER
)

enum class Role {
    ROLE_USER,
    ROLE_ADMIN
}