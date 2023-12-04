import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    alias(libs.plugins.kotlin)
    alias(libs.plugins.spotless)
}

repositories {
    mavenCentral()
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
}

application {
    applicationName = "dp-behov-soknad-pdf"
    mainClass.set("no.nav.dagpenger.innsending.AppKt")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<Jar>().configureEach {
    dependsOn("test")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(mapOf("Main-Class" to application.mainClass.get()))
    }

    from(
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        },
    )
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(libs.rapids.and.rivers)
    implementation(libs.konfig)
    implementation(libs.kotlin.logging)
    implementation("no.nav.dagpenger:oauth2-klient:2023.11.27-13.50.d478414fd10d")
    implementation("no.nav.dagpenger:pdl-klient:2023.11.27-13.50.d478414fd10d")
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.jackson.datatype.jsr310)
    implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10")
    implementation("com.openhtmltopdf:openhtmltopdf-slf4j:1.0.10")
    implementation("com.openhtmltopdf:openhtmltopdf-svg-support:1.0.10")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.10.1")
    implementation("org.jsoup:jsoup:1.17.1")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.9.1")
    implementation("org.apache.commons:commons-text:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.7.3")
    implementation("no.nav.pam.geography:pam-geography:2.20")

    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.8.0")
    testImplementation(libs.ktor.client.mock)
    testImplementation("org.verapdf:validation-model:1.24.1")
    testImplementation("de.redsix:pdfcompare:1.1.61")

    // FOr E2E
    testImplementation("io.kubernetes:client-java:19.0.0")
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        ktlint()
    }

    kotlinGradle {
        ktlint()
    }
}

tasks.named("compileKotlin") {
    dependsOn("spotlessCheck")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}
