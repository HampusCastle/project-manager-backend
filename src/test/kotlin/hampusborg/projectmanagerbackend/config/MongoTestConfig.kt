package hampusborg.projectmanagerbackend.config

import jakarta.annotation.PreDestroy
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.testcontainers.containers.MongoDBContainer
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestConfiguration
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class]) // Stänger av automatisk MongoDB-konfiguration
class MongoTestConfig {

    private val mongoDBContainer = MongoDBContainer("mongo:latest").apply {
        start()
        Thread.sleep(5000)
    }

    @Bean
    fun mongoTemplate(): MongoTemplate {
        val connectionString = "mongodb://${mongoDBContainer.host}:${mongoDBContainer.firstMappedPort}/test"
        println("MongoDB Connection String: $connectionString")
        val mongoDatabaseFactory = SimpleMongoClientDatabaseFactory(connectionString)
        return MongoTemplate(mongoDatabaseFactory)
    }

    @PreDestroy
    fun stopMongo() {
        mongoDBContainer.stop()
    }

    @Test
    fun `test MongoDB connection`() {
        val mongoTemplate = mongoTemplate()
        val databaseName = mongoTemplate.db.name
        assertNotNull(mongoTemplate)
        assertTrue(databaseName == "test", "Expected database name to be 'test', but was '$databaseName'.")
    }
}