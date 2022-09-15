buildscript {
    extra["compose_version"] = "1.2.0-rc02"
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
    }
}

plugins {
    id("com.android.application") version "7.4.0-alpha10" apply false
    id("com.android.library") version "7.4.0-alpha10" apply false
    kotlin("android") version "1.6.21" apply false
}

tasks.withType<Delete> {
    delete(rootProject.buildDir)
}