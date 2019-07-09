import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("java")
  id("idea")
  id("io.spring.dependency-management") version "1.0.6.RELEASE"
  id("org.springframework.boot") version "2.1.1.RELEASE"
  id("io.franzbecker.gradle-lombok") version "1.14"
}

tasks.getByName<BootJar>("bootJar") {
  archiveName = "contact-service.jar"
  version = "0.1.0"
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
  mavenCentral()
}

dependencyManagement {
  dependencies {
    dependency("org.awaitility:awaitility:3.1.5")
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

lombok {
  version = "1.18.4"
  sha256 = ""
}
