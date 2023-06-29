buildscript {
    extra["compose_version"] = "1.5.0-beta03"
}

plugins {
    id("com.android.application") version "8.1.0-rc01" apply false
    id("com.android.library") version "8.1.0-rc01" apply false
    kotlin("android") version "1.8.20" apply false
    id("com.google.dagger.hilt.android") version "2.46.1" apply false
}

tasks.withType<Delete> {
    delete(rootProject.buildDir)
}