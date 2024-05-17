plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.appcrud"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appcrud"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding{
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.fragment:fragment-ktx:1.7.1")
    implementation ("com.itextpdf:itext7-core:7.2.4")

    //Dependencia PDF
    implementation ("com.dmitryborodin:pdfview-android:1.1.0")

    //RETROFIT
    //com.squareup.retrofit2:converter-gson
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    //CORRUTINA
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}