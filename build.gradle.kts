buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // 👇 Agregamos el plugin de Google Services en el classpath (forma clásica, segura)
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    // Estos vienen de tu libs.versions.toml
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
