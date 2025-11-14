plugins {
    alias(libs.plugins.android.application)
    id("io.objectbox")
}

android {
    namespace = "com.example.imageupload"
    // GEORGINA: had to change this to 36 because of the same newer dependencies
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.imageupload"
        minSdk = 24
        //GEORGINA: had to change this to 36 because of the same newer dependencies
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation(libs.activity)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("io.objectbox:objectbox-android:5.0.1")
    annotationProcessor("io.objectbox:objectbox-processor:5.0.1")

}