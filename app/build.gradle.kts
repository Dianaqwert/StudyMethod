import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //pluggings database
    id("kotlin-kapt")
}

// 1. Cargar properties
val localProperties = Properties() // Ya no necesitas 'java.util.'
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile)) // Ya no necesitas 'java.io.'
}

android {
    namespace = "com.example.proyecto_final"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proyecto_final"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        //nuevo
        buildConfigField("String", "OPENAI_URL", "\"https://api.openai.com/v1/chat/completions\"")
        buildConfigField("String", "OPENAI_API_KEY", "\"${localProperties.getProperty("OPENAI_API_KEY") ?: ""}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    //nuevo
    buildFeatures{
        buildConfig=true; //activa la generacion del alchivo builconfig
    }
}

dependencies {
    //data room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // Para Corrutinas
    kapt("androidx.room:room-compiler:$room_version")
    //librerias de openai
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20210307")
    implementation("io.noties.markwon:core:4.6.2")
    //ñibrerias por defecto
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}