buildscript {
    extra["compose_version"] = "1.3.0"
}

plugins {
    id("com.android.application") version "8.0.0-alpha05" apply false
    id("com.android.library") version "8.0.0-alpha05" apply false
    kotlin("android") version "1.7.20" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
}

tasks.withType<Delete> {
    delete(rootProject.buildDir)
}