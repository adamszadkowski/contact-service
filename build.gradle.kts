import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50"
    id("nebula.integtest") version "6.0.3"
    id("idea")
    id("org.springframework.boot") version "2.2.0.RELEASE"
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.2.0.RELEASE"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.0"))
    constraints {
        implementation("org.awaitility:awaitility:3.1.6")
        implementation("io.strikt:strikt-core:0.21.1")
        implementation("io.mockk:mockk:1.9")
    }
}

configurations.forEach {
    it.exclude(group = "org.assertj")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.strikt:strikt-core")
    testImplementation("io.mockk:mockk")
    testImplementation("org.awaitility:awaitility")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
    getByName<BootJar>("bootJar") {
        archiveFileName.set("contact-service.jar")
    }
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
