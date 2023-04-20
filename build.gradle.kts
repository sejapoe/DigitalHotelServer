import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    kotlin("multiplatform") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("io.ktor.plugin") version "2.2.4"
    id("com.google.devtools.ksp") version "1.8.20-1.0.11"
    application
}

group = "ru.sejapoe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven("https://jitpack.io")
}
kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")

            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("com.github.sejapoe:ktor-ksp-routing:1.0.3")
                implementation("com.google.firebase:firebase-admin:7.1.1")
                implementation("ch.qos.logback:logback-classic:1.0.0")
                implementation("io.jsonwebtoken:jjwt-api:0.11.2")
                implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
                implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")
                implementation("org.postgresql:postgresql:42.3.1")
                implementation("org.jetbrains.exposed:exposed-core:0.41.1")
                implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
                implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
                implementation("io.ktor:ktor-server-content-negotiation:2.0.2")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.2")
                implementation("io.ktor:ktor-network-tls-certificates:2.0.2")
                implementation("io.ktor:ktor-server-netty:2.0.2")
                implementation("io.ktor:ktor-server-html-builder-jvm:2.0.2")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.9.3-pre.346")
            }
        }
        val jsTest by getting
    }
    jvmToolchain(11)
}

application {
    mainClass.set("ru.sejapoe.application.ServerKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

ktor {
    archivesName.set("server.jar")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}
dependencies {
    implementation("io.ktor:ktor-server-call-logging-jvm:2.2.4")
    add("kspJvm", "com.github.sejapoe:ktor-ksp-routing:1.0.3")
}
