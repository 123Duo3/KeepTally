buildscript {
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    kotlin("android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.49" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
}

tasks.withType<Delete> {
    delete(rootProject.buildDir)
}