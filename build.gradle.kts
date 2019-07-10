import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("java")
  id("idea")
  id("io.spring.dependency-management") version "1.0.8.RELEASE"
  id("org.springframework.boot") version "2.1.6.RELEASE"
  id("io.franzbecker.gradle-lombok") version "3.1.0"
}

tasks.getByName<BootJar>("bootJar") {
  archiveFileName.set("contact-service.jar")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

repositories {
  mavenCentral()
}

dependencyManagement {
  dependencies {
    dependency("org.awaitility:awaitility:3.1.6")
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-mustache")

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
