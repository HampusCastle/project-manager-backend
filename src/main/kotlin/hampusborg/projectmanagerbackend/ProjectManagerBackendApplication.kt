package hampusborg.projectmanagerbackend

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProjectManagerBackendApplication

fun main(args: Array<String>) {
    val dotenv = Dotenv.load()

    System.setProperty("DATABASE_URL", dotenv["DATABASE_URL"] ?: "")
    System.setProperty("JWT_SECRET", dotenv["JWT_SECRET"] ?: "")

    runApplication<ProjectManagerBackendApplication>(*args)
}