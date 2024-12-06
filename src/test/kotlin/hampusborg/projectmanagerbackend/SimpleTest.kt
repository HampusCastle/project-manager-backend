package hampusborg.projectmanagerbackend

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class SimpleTest {
    @Test
    fun simpleTest() {
        Assertions.assertTrue(true, "Test passed!")
    }
}