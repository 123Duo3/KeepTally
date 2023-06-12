buildscript {
    extra["compose_version"] = "1.5.0-beta02"
}

plugins {
    id("com.android.application") version "8.1.0-beta05" apply false
    id("com.android.library") version "8.1.0-beta05" apply false
    kotlin("android") version "1.8.20" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
}

tasks.withType<Delete> {
    delete(rootProject.buildDir)
}