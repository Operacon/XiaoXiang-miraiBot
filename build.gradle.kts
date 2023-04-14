plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.15.0-M1"
}

group = "org.operacon"
version = "0.4-DEBUG"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.quartz-scheduler:quartz:2.3.2")
    implementation("org.quartz-scheduler:quartz-jobs:2.3.2")
}