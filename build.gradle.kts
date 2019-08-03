import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  kotlin("jvm") version "1.3.41"
  kotlin("plugin.spring") version "1.3.41"
  id("idea")
  id("io.spring.dependency-management") version "1.0.8.RELEASE"
  id("org.springframework.boot") version "2.1.6.RELEASE"
}

sourceSets {
  create("intTest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
  }
}

val intTestImplementation by configurations.getting {
  extendsFrom(configurations.implementation.get())
  extendsFrom(configurations.testImplementation.get())
}

val intTestRuntimeOnly by configurations.getting {
  extendsFrom(configurations.runtimeOnly.get())
  extendsFrom(configurations.testRuntimeOnly.get())
}

tasks.getByName<BootJar>("bootJar") {
  archiveFileName.set("contact-service.jar")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
  mavenCentral()
}

dependencyManagement {
  dependencies {
    dependency("org.awaitility:awaitility:3.1.6")
    dependency("org.assertj:assertj-core:3.12.2")
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.boot:spring-boot-starter-mustache")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.assertj:assertj-core")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")
  testImplementation("org.awaitility:awaitility")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
  useJUnitPlatform()
}

val integrationTest = task<Test>("integrationTest") {
  description = "Runs integration tests."
  group = "verification"

  testClassesDirs = sourceSets["intTest"].output.classesDirs
  classpath = sourceSets["intTest"].runtimeClasspath

  useJUnitPlatform()
  shouldRunAfter("test")
}

tasks.check { dependsOn(integrationTest) }
