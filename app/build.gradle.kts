import com.android.SdkConstants
import com.google.common.base.Charsets
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.konyaco.keeptally"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.konyaco.keeptally"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // For Room
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles.apply {
                add(getDefaultProguardFile("proguard-android-optimize.txt"))
                add(file("proguard-rules.pro"))
            }
            buildConfigField("String", "BASE_URL", """"https://api.keeptally.konyaco.com"""")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles.apply {
                add(getDefaultProguardFile("proguard-android-optimize.txt"))
                add(file("proguard-rules.pro"))
            }
            applicationIdSuffix = ".debug"
//            val baseUrl = gradleLocalProperties(rootDir).getProperty("baseUrl")

            val properties = Properties()
            val localProperties = File(rootDir, SdkConstants.FN_LOCAL_PROPERTIES)

            if (localProperties.isFile) {
                InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
                    properties.load(reader)
                }
            }
            val baseUrl = properties.getProperty("baseUrl")
            buildConfigField("String", "BASE_URL", """"$baseUrl"""")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    kotlin {
        jvmToolchain(17)
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    testImplementation(libs.junit.jupiter)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.hilt.navigation.compose)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    testImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.compiler)
    kaptAndroidTest(libs.hilt.compiler)

    implementation(libs.hilt.navigation.compose)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    val composeBom = platform("androidx.compose:compose-bom:2024.04.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3-window-size-class")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.navigation.compose)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.androidx.datastore.preferences)
    testImplementation(libs.junit)

    implementation(libs.argon2kt)
    // kotlinx-datetime
    implementation(libs.kotlinx.datetime)

    // Profile Installer
    implementation(libs.androidx.profileinstaller)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}