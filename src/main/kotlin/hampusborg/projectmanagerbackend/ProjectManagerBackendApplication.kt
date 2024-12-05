package hampusborg.projectmanagerbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProjectManagerBackendApplication

fun main(args: Array<String>) {
    runApplication<ProjectManagerBackendApplication>(*args)
}
