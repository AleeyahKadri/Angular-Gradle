import java.io.ByteArrayOutputStream
import org.gradle.api.tasks.testing.Test

plugins {
    `java-gradle-plugin`
    groovy
    // https://plugins.gradle.org/plugin/com.gradle.plugin-publish
    id("com.gradle.plugin-publish") version "0.12.0"
}

group = "de.clashsoft"

val gitDescribeOutput = ByteArrayOutputStream()
exec {
    commandLine("git", "describe", "--tags")
    workingDir = rootDir
    standardOutput = gitDescribeOutput
}
version = gitDescribeOutput.toString().trim().removePrefix("v")

description = "Integrate Angular frontends into your Gradle build."

pluginBundle {
    website = "https://github.com/Clashsoft/Angular-Gradle"
    vcsUrl = "https://github.com/Clashsoft/Angular-Gradle"
    description = project.description
    tags = listOf("angular", "frontend", "node")

    plugins {
        create("angularGradle") {
            displayName = "Angular Gradle"
        }
    }

    mavenCoordinates {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    testImplementation("junit:junit:4.12")
}

gradlePlugin {
    plugins {
        create("angularGradle") {
            id = "de.clashsoft.angular-gradle"
            implementationClass = "de.clashsoft.gradle.angular.AngularGradlePlugin"
        }
    }
    testSourceSets(sourceSets.create("functionalTest"))
}

val functionalTestSourceSet = sourceSets["functionalTest"]

configurations[functionalTestSourceSet.implementationConfigurationName]
    .extendsFrom(configurations["testImplementation"])

tasks.register<Test>("functionalTest") {
    testLogging.showStandardStreams = true
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

tasks.named("check") {
    dependsOn("functionalTest")
}
