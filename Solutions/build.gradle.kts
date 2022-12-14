plugins {
    kotlin("jvm") version "1.7.21"
    application
}

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        sourceSets {
            main {
                java.srcDirs("Solutions", "Helpers")
            }
        }
}