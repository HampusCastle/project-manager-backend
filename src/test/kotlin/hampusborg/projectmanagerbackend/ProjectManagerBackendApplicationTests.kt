package hampusborg.projectmanagerbackend

import hampusborg.projectmanagerbackend.config.MongoTestConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ActiveProfiles("test")
@Import(MongoTestConfig::class)
@TestPropertySource(properties = ["JWT_SECRET=mock-secret-for-tests"])
class ProjectManagerBackendApplicationTests {

    @Test
    fun contextLoads() {
    }

}
