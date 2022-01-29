buildscript {
    extra["compose_version"] = "1.1.0-rc03"
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
    }
}

plugins {
    id("com.android.application") version "7.1.0-rc01" apply false
    id("com.android.library") version "7.1.0-rc01" apply false
    kotlin("android") version "1.6.10" apply false
}

tasks.withType<Delete> {
    delete(rootProject.buildDir)
}