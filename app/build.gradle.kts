plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("dagger.hilt.android.plugin") // Hilt plugin
    id("kotlin-kapt") // Correcto para Kapt
    id("kotlin-android")
}

android {
    namespace = "com.example.speedrun_compose"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.speedrun_compose"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {


    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation (libs.androidx.hilt.navigation.compose)  // Necesario para hiltViewModel en Compose
    implementation (libs.androidx.lifecycle.viewmodel.compose) // Para ViewModel con Compose

    // Room
    implementation(libs.androidx.room.runtime)
    kapt("androidx.room:room-compiler:2.6.1") // Kapt para Room

    // Room con corrutinas
    implementation(libs.androidx.room.ktx.v252)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // OkHttp
    implementation(libs.okhttp)

    // Jetpack Compose
    implementation(libs.androidx.ui.v151)
    implementation(libs.androidx.material.v151)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)

    // Lifecycle y ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx.v261)

    // Coil para imágenes en Compose
    implementation(libs.coil.compose)

    // Material 3 e íconos extendidos
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material3)

    // Navigation para Compose
    implementation(libs.androidx.navigation.compose)

    // Otros componentes Jetpack Compose
    implementation(libs.androidx.foundation)
    implementation(libs.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
