buildscript {
    extra["compose_version"] = "1.3.1"
}

plugins {
    id("com.android.application") version "8.1.0-beta04" apply false
    id("com.android.library") version "8.1.0-beta04" apply false
    kotlin("android") version "1.8.21" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
}

tasks.withType<Delete> {
    delete(rootProject.buildDir)
}