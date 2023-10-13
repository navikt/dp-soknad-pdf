import com.diffplug.spotless.LineEnding
import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.9.10"
    id("com.diffplug.spotless") version "6.21.0"
}

apply {
    plugin("com.diffplug.spotless")
}

repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
    maven("https://jitpack.io")
}

application {
    applicationName = "dp-behov-soknad-pdf"
    mainClass.set("no.nav.dagpenger.innsending.AppKt")
}

java {
    targetCompatibility = VERSION_17
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

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = VERSION_17.toString()
}

dependencies {
    val ktorVersion = "2.3.4"
    implementation(kotlin("stdlib"))

    implementation("com.github.navikt:rapids-and-rivers:2023093008351696055717.ffdec6aede3d")

    implementation("com.natpryce:konfig:1.6.10.0")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("com.github.navikt.dp-biblioteker:pdl-klient:2023.04.27-09.33.fcf0798bf943")
    implementation("com.github.navikt.dp-biblioteker:oauth2-klient:2023.04.27-09.33.fcf0798bf943")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10")
    implementation("com.openhtmltopdf:openhtmltopdf-slf4j:1.0.10")
    implementation("com.openhtmltopdf:openhtmltopdf-svg-support:1.0.10")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.9.1")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.7.3")
    implementation("com.github.navikt:pam-geography:2.18")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.7.2")
    testImplementation("io.ktor:ktor-client-${"mock"}:2.0.2")
    testImplementation("org.verapdf:validation-model:1.24.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // FOr E2E
    testImplementation("io.kubernetes:client-java:18.0.1")
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        ktlint()
    }

    kotlinGradle {
        ktlint()
    }
    // Workaround for <https://github.com/diffplug/spotless/issues/1644>
    // using idea found at
    // <https://github.com/diffplug/spotless/issues/1527#issuecomment-1409142798>.
    lineEndings = LineEnding.PLATFORM_NATIVE // or any other except GIT_ATTRIBUTES
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
