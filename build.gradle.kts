import java.io.ByteArrayOutputStream

plugins {
    `java-gradle-plugin`
    groovy
    // https://plugins.gradle.org/plugin/com.gradle.plugin-publish
    id("com.gradle.plugin-publish") version "0.12.0"
}

group = "de.clashsoft"
version = getVersionFromGit()
description = "Integrate Angular frontends into your Gradle build."

fun getVersionFromGit(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags")
        standardOutput = stdout
    }
    return stdout.toString().trim().substring(1) // strip v prefix
}

configure<com.gradle.publish.PluginBundleExtension> {
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
}

sourceSets {
    create("functionalTest") {
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])
configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

tasks.register<Test>("functionalTest") {
    testLogging.showStandardStreams = true
    testClassesDirs = sourceSets["functionalTest"].output.classesDirs
    classpath = sourceSets["functionalTest"].runtimeClasspath
}

tasks.named("check") {
    dependsOn("functionalTest")
}
