package hampusborg.projectmanagerbackend.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.http.MediaType

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `test create project with valid data`() {
        val projectRequest = """{"name": "New Project", "description": "Test project"}"""

        mockMvc.post("/projects") {
            content = projectRequest
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status().isOk
                jsonPath("$.name").value("New Project")
                jsonPath("$.description").value("Test project")
            }
    }

    @Test
    fun `test update project`() {
        val projectRequest = """{"name": "Updated Project", "description": "Updated description"}"""
        val projectId = "1"

        mockMvc.put("/projects/$projectId") {
            content = projectRequest
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status().isOk
                jsonPath("$.name").value("Updated Project")
                jsonPath("$.description").value("Updated description")
            }
    }

    @Test
    fun `test get all projects`() {
        mockMvc.get("/projects")
            .andExpect {
                status().isOk
                jsonPath("$").isArray
                jsonPath("$[0].name").value("Existing Project")
            }
    }

    @Test
    fun `test get project by id`() {
        val projectId = "1"
        mockMvc.get("/projects/$projectId")
            .andExpect {
                status().isOk
                jsonPath("$.name").value("Existing Project")
            }
    }

    @Test
    fun `test delete project`() {
        val projectId = "1"

        mockMvc.delete("/projects/$projectId")
            .andExpect {
                status().isOk
                content().string("Project deleted successfully")
            }
    }
}