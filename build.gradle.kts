plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "hampusborg"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot starter dependencies
    implementation("org.springframework.boot:spring-boot-starter-security") // För säkerhet
    implementation("org.springframework.boot:spring-boot-starter-validation") // För validering
    implementation("org.springframework.boot:spring-boot-starter-web") // För REST API
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb") // För MongoDB

    // Jackson och Kotlin-specifika moduler
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // För JSON-serialisering med Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect") // För Kotlin reflection

    // JWT och Security
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-orgjson:0.11.5")

    // Lombok (För att minska boilerplate-kod)
    annotationProcessor("org.projectlombok:lombok")

    // Development Only (För devtools)
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")  // Grundläggande test beroende för Spring Boot
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0") // JUnit 5 API (nyare version)
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0") // JUnit 5 Engine (nyare version)
    testImplementation("org.springframework.security:spring-security-test")  // För säkerhetstestning
    testImplementation("org.mockito:mockito-core:3.9.0")  // För mockning
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")  // För Kotlin-stöd i Mockito
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5") // För Kotlin-JUnit5 integration
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") // För att köra tester med JUnit Platform
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}