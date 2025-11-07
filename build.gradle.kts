// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    //ext.objectboxVersion = "5.0.1" // For Groovy build scripts
    val objectboxVersion by extra("5.0.1") // For KTS build scripts

    repositories {
        mavenCentral()
    }

    dependencies {
        // GEORGINA: had to change this to 8.9.1 because whenever I created SecondActivity,
        // the Empty Activity View template introduces newer, incompatible dependencies
        // Android Gradle Plugin 8.0 or later supported
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
    }
}