// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val agpVersion = "8.6.1"
    val kotlinVersion = "2.0.21"

    id("com.android.application") version agpVersion apply false
    id("com.android.library") version agpVersion apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion apply false
}