import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

repositories {
    mavenCentral()
}


tasks {
    wrapper {
        gradleVersion = "7.5.1"
    }
}