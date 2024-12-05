package hampusborg.projectmanagerbackend.repository

import hampusborg.projectmanagerbackend.model.Project
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : MongoRepository<Project, String>