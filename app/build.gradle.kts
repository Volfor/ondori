plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.devtools.kps)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.volfor.ondori"
    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }

    defaultConfig {
        applicationId = "com.volfor.ondori"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.compose.material3)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.material3)
    // Android Studio Preview support
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    // Window size utils
//    implementation("androidx.compose.material3.adaptive:adaptive")
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.service)
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
//    implementation("androidx.compose.runtime:runtime-livedata")
//    implementation("androidx.compose.runtime:runtime-rxjava2")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui.googlefonts)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.android.hilt)
    ksp(libs.android.hilt.compiler)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    val firebaseBom = platform(libs.google.firebase.bom)
    implementation(firebaseBom)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // UI Tests
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}