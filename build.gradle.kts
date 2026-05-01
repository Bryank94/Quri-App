plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Esta es la forma correcta de declarar plugins en el root
    kotlin("kapt") version "2.0.0" apply false
}