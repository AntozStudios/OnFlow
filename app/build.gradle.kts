import com.android.build.gradle.internal.packaging.defaultExcludes

plugins {
    id("com.android.application")
}

android {
    namespace = "com.antozstudios.myapplication"
    compileSdk = 34


packaging{

    resources.excludes.add("META-INF/INDEX.LIST")
    resources.excludes.add("META-INF/DEPENDENCIES")
}


    defaultConfig {
        applicationId = "com.antozstudios.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"


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

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // deine anderen Abhängigkeiten hier...





    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.compose.foundation:foundation-android:1.6.3")
    implementation("com.google.ar.sceneform:filament-android:1.17.1")

    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.cloud:google-cloud-texttospeech:2.37.0")



}



