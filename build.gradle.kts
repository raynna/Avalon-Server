plugins {
    kotlin("jvm") version "1.9.24"
    application
    java
}

group = "com.rs"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:1.9.24")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:1.9.24")
    implementation("org.jetbrains.kotlin:kotlin-script-util:1.8.22")

    // Add any other dependencies here
    implementation("io.netty:netty:3.9.9.Final")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.slf4j:slf4j-simple:2.0.12")

    // Local JARs (Try to avoid this if at all possible by using Maven)
    implementation(files("lib/FileStore.jar"))
    implementation(files("lib/everythingrs-api.jar"))
    implementation(files("lib/ip2c.jar"))

    // Optional: Kotlin reflection if you want to use annotations
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.24")

    // https://mvnrepository.com/artifact/com.displee/rs-cache-library
    implementation("com.displee:rs-cache-library:7.3.0")
}

application {
    mainClass.set("com.rs.Launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

kotlin {
    jvmToolchain(21)
}
