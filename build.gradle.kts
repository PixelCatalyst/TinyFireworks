group = "com.pixcat"
version = "1.0-SNAPSHOT"

val applicationMainClass = "MainKt"

plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runtime)
    alias(libs.plugins.versions)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging)

    runtimeOnly(libs.log4j.slf4j2)
    runtimeOnly(libs.log4j.core)
    runtimeOnly(libs.jackson.databind)
    runtimeOnly(libs.jackson.json)
    implementation(kotlin("stdlib-jdk8"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

project.setProperty("mainClassName", applicationMainClass)

application {
    if (hasProperty("openrndr.application")) {
        mainClass.set("${property("openrndr.application")}")
    }
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = applicationMainClass
            attributes["Implementation-Version"] = project.version
        }
        minimize {
            exclude(dependency("org.openrndr:openrndr-gl3:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
            exclude(dependency("org.slf4j:slf4j-simple:.*"))
            exclude(dependency("org.apache.logging.log4j:log4j-slf4j2-impl:.*"))
            exclude(dependency("com.fasterxml.jackson.core:jackson-databind:.*"))
            exclude(dependency("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:.*"))
        }
    }
}

tasks {
    dependencyUpdates {
        gradleReleaseChannel = "current"

        val nonStableKeywords = listOf("alpha", "beta", "rc")

        fun isNonStable(
            version: String
        ) = nonStableKeywords.any {
            version.lowercase().contains(it)
        }

        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }
}

class Openrndr {
    val openrndrVersion = libs.versions.openrndr.get()

    val currArch = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform("current").architecture.name
    val currOs = org.gradle.internal.os.OperatingSystem.current()
    val os = if (project.hasProperty("targetPlatform")) {
        val supportedPlatforms = setOf("windows", "macos", "linux-x64", "linux-arm64")
        val platform: String = project.property("targetPlatform") as String
        if (platform !in supportedPlatforms) {
            throw IllegalArgumentException("target platform not supported: $platform")
        } else {
            platform
        }
    } else when {
        currOs.isWindows -> "windows"
        currOs.isMacOsX -> when (currArch) {
            "aarch64", "arm-v8" -> "macos-arm64"
            else -> "macos"
        }

        currOs.isLinux -> when (currArch) {
            "x86-64" -> "linux-x64"
            "aarch64" -> "linux-arm64"
            else -> throw IllegalArgumentException("architecture not supported: $currArch")
        }

        else -> throw IllegalArgumentException("os not supported: ${currOs.name}")
    }

    private fun openrndr(module: String) = "org.openrndr:openrndr-$module:$openrndrVersion"
    private fun openrndrNatives(module: String) = "org.openrndr:openrndr-$module-natives-$os:$openrndrVersion"

    init {
        dependencies {
            runtimeOnly(openrndr("gl3"))
            runtimeOnly(openrndrNatives("gl3"))
            implementation(openrndr("openal"))
            runtimeOnly(openrndrNatives("openal"))
            implementation(openrndr("application"))
            implementation(openrndr("svg"))
            implementation(openrndr("animatable"))
            implementation(openrndr("extensions"))
            implementation(openrndr("filter"))
            implementation(openrndr("dialogs"))
        }
    }
}

val openrndr = Openrndr()

if (properties["openrndr.tasks"] == "true") {
    task("create executable jar for $applicationMainClass") {
        group = "OPENRNDR"
        dependsOn("shadowJar")
    }

    task("run $applicationMainClass") {
        group = "OPENRNDR"
        dependsOn("run")
    }
}
