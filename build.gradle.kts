plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "org.operacon"
version = "0.3-DEBUG"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("org.quartz-scheduler:quartz:2.2.1")
    implementation("org.quartz-scheduler:quartz-jobs:2.2.1")
}