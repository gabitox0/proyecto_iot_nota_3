plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // PASO 1: AÑADIR EL PLUGIN DE GOOGLE SERVICES
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proyecto_iot_nota_3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.proyecto_iot_nota_3"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // AÑADIDO: Habilitar View Binding
    buildFeatures {
        viewBinding = true
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
}

dependencies {
    // Dependencias por defecto
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ----- DEPENDENCIAS DE FIREBASE Y GOOGLE -----
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // Firebase Authentication (Auth)
    implementation("com.google.firebase:firebase-auth-ktx")

    // Google Play Services Auth (Necesario para Google Sign-In)
    implementation("com.google.android.gms:play-services-auth")

    // Firebase Firestore (Base de datos de noticias)
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Coroutines para el manejo asíncrono
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Dependencias de prueba
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// TAREA PARA OBTENER EL SHA-1 DESDE GRADLE
tasks.register("printSha1") {
    // Asegurarse de que esta tarea se ejecute después de la tarea de firma de Android
    dependsOn("signingReport")
    doLast {
        // La tarea signingReport imprime automáticamente la información de SHA-1 a la consola.
        println("Ejecutando la tarea signingReport. Busca el valor 'SHA1:' en la ventana Run/Build.")
    }
}