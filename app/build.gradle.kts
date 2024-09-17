plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.roland.android.odiyo"
    compileSdk = 34

    signingConfigs {
        create("release") {
            keyAlias = "nnamdo"
            keyPassword = "rolinsnnamodi9570"
            storeFile = file("C:\\Users\\Martins\\keystores\\app-keystore.jks")
            storePassword = "rolinsnnamodi9570"
        }
    }

    defaultConfig {
        applicationId = "com.roland.android.odiyo"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // old dependencies
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.compiler)
    implementation(libs.navigation.anim)
    implementation(libs.systembar)

    // android
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.compose.navigation)

    // coil
    implementation(libs.coil.compose)

    // di
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.core)

    // glance
    implementation(libs.glance)
    implementation(libs.glance.appwidget)

    // media3
    implementation(libs.media3.player)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)

    // material design
    implementation(libs.material.icons)
    implementation(libs.material3)

    // other modules
    implementation(project(path = ":domain"))
    implementation(project(path = ":data-repository"))
    implementation(project(path = ":data-local"))
    implementation(project(path = ":data-system"))

    // paging
    implementation(libs.paging.compose)

    // palette
    implementation(libs.palette)

    // persistence
    implementation(libs.datastore)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // splash
    implementation(libs.splashscreen)

    // unit testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}