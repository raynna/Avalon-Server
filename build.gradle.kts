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
    implementation("log4j:log4j:1.2.16")
    //its that simple, then you can delete the local jar ohh nice the rebuild herE?
//god i need to get rid of those libs locally i think ye use maven implemtation declarations instead, you dont want to host local libs ye, never used gradle in my life or maven or yes, i did for some minecrat modding
    // Local JARs (Try to avoid this if at all possible by using Maven)
    implementation(files("lib/FileStore.jar"))
    implementation(files("lib/c3p0-0.9.5.5.jar"))
    implementation(files("lib/collections-generic-4.01.jar"))
    implementation(files("lib/commons-codec-1.9.jar"))
    implementation(files("lib/commons-codec-1.10.jar"))
    implementation(files("lib/demorpg.jar"))
    implementation(files("lib/everythingrs-api.jar"))
    implementation(files("lib/ip2c.jar"))
    implementation(files("lib/javacord-2.0.17-shaded.jar"))
    implementation(files("lib/jode-1.1.2-pre1.jar"))
    implementation(files("lib/lzma-4.63-jio-0.94.jar"))
    implementation(files("lib/mail.jar"))
    implementation(files("lib/mchange-commons-java-0.2.19.jar"))
    implementation(files("lib/metrik.jar"))
    implementation(files("lib/mysql.jar"))
    implementation(files("lib/mysql-5.0.5-bin.jar"))
    implementation(files("lib/mysql-connector-5.1.22.jar"))
    implementation(files("lib/mysql-connector-java-5.1.18-bin.jar"))
    implementation(files("lib/RS-Cache-Library.jar"))
    implementation(files("lib/RuneTopListV2.1.jar"))
    //implementation(files("lib/xpp3-1.13.4.C.jar"))
    implementation(files("lib/xstream-1.4.1.jar"))

    // Optional: Kotlin reflection if you want to use annotations
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.24")
}

application {
    mainClass.set("com.rs.Launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ORACLE)
    }
}
