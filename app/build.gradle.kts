import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val envFile = rootProject.file(".env")
val envProperties = Properties()

println("🔍 Looking for .env file at: ${envFile.absolutePath}")
println("🔍 .env file exists: ${envFile.exists()}")

if (envFile.exists()) {
    try {
        envProperties.load(FileInputStream(envFile))
        println("✅ Successfully loaded .env file")

        // Debug: Print all loaded properties (remove in production!)
        println("📋 Loaded properties:")
        envProperties.forEach { key, value ->
            println("   $key = ${value.toString().take(10)}...")
        }

    } catch (e: Exception) {
        println("❌ Error loading .env file: ${e.message}")
    }
} else {
    println("❌ .env file not found!")
    println("📁 Expected location: ${envFile.absolutePath}")
    println("📁 Current working directory: ${System.getProperty("user.dir")}")
}

android {
    namespace = "com.frogans.fpandroidlocationservice"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.frogans.fpandroidlocationservice"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        // Debug: Check what values are being set
        val vivatechPositionsKey = envProperties.getProperty("VIVATECH_POSITIONS_KEY") ?: "KEY_NOT_FOUND"
        val serverUrl = envProperties.getProperty("SERVER_URL") ?: "KEY_NOT_FOUND"

        buildConfigField("String", "VIVATECH_POSITIONS_KEY", "\"$vivatechPositionsKey\"")
        buildConfigField("String", "SERVER_URL", "\"$serverUrl\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true  // Enable BuildConfig generation
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.location)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}