package hampusborg.projectmanagerbackend.controller

import hampusborg.projectmanagerbackend.config.MongoTestConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(MongoTestConfig::class)
@TestPropertySource(properties = ["JWT_SECRET=mock-secret-for-tests"])
class TaskControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `test create task`() {
        val projectId = "1"
        val taskRequest = """{"title": "Task 1", "description": "Description for Task 1", "status": "OPEN"}"""

        mockMvc.post("/projects/$projectId/tasks") {
            content = taskRequest
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status().isOk
                jsonPath("$.title").value("Task 1")
                jsonPath("$.description").value("Description for Task 1")
            }
    }

    @Test
    fun `test get task by id`() {
        val projectId = "1"
        val taskId = "1"

        mockMvc.get("/projects/$projectId/tasks/$taskId")
            .andExpect {
                status().isOk
                jsonPath("$.title").value("Task 1")
                jsonPath("$.description").value("Description for Task 1")
            }
    }

    @Test
    fun `test update task status`() {
        val projectId = "1"
        val taskId = "1"
        val taskRequest = """{"title": "Updated Task", "description": "Updated description", "status": "IN_PROGRESS"}"""

        mockMvc.put("/projects/$projectId/tasks/$taskId") {
            content = taskRequest
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status().isOk
                jsonPath("$.status").value("IN_PROGRESS")
            }
    }

    @Test
    fun `test delete task`() {
        val projectId = "1"
        val taskId = "1"

        mockMvc.delete("/projects/$projectId/tasks/$taskId")
            .andExpect {
                status().isOk
                content().string("Task deleted successfully")
            }
    }
}