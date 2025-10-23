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
        // Android Gradle Plugin 8.0 or later supported
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
    }
}