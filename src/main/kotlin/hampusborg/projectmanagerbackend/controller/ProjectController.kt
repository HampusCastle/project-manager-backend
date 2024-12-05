package hampusborg.projectmanagerbackend.controller

import hampusborg.projectmanagerbackend.dto.*
import hampusborg.projectmanagerbackend.service.ProjectService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects")
class ProjectController(private val projectService: ProjectService) {

    private fun handleValidationErrors(bindingResult: BindingResult): ResponseEntity<Any> {
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.fieldErrors.joinToString(", ") { it.defaultMessage ?: "Invalid data" }
            return ResponseEntity.badRequest().body(ErrorDto(errors))
        }
        return ResponseEntity.ok().build()
    }

    @PostMapping
    fun createProject(@Valid @RequestBody projectDto: ProjectDto, bindingResult: BindingResult): ResponseEntity<Any> {
        val validationError = handleValidationErrors(bindingResult)
        if (validationError.statusCode != HttpStatus.OK) {
            return validationError
        }
        return ResponseEntity.ok(projectService.createProject(projectDto))
    }

    @GetMapping
    fun getAllProjects(): ResponseEntity<List<ProjectDto>> = ResponseEntity.ok(projectService.getAllProjects())

    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: String): ResponseEntity<ProjectDto> =
        ResponseEntity.ok(projectService.getProjectById(id))

    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: String): ResponseEntity<String> {
        projectService.deleteProject(id)
        return ResponseEntity.ok("Project deleted successfully")
    }

    @PutMapping("/{id}")
    fun updateProject(
        @PathVariable id: String,
        @Valid @RequestBody projectDto: ProjectDto,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        val validationError = handleValidationErrors(bindingResult)
        if (validationError.statusCode != HttpStatus.OK) {
            return validationError
        }
        return ResponseEntity.ok(projectService.updateProject(id, projectDto))
    }
}