import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

// La firma stabile è esplicita: build e test CI ordinari non leggono credenziali.
val signRelease = providers.gradleProperty("signRelease").orNull == "true"
val releaseKeyProperties = Properties()
if (signRelease) {
    val propertiesFile = rootProject.file("keystore.properties")
    check(propertiesFile.isFile) {
        "Firma richiesta: crea keystore.properties dal modello keystore.properties.example."
    }
    propertiesFile.reader(Charsets.UTF_8).use(releaseKeyProperties::load)
    listOf("storeFile", "storePassword", "keyAlias", "keyPassword").forEach { name ->
        check(!releaseKeyProperties.getProperty(name).isNullOrBlank()) {
            "Firma richiesta: manca il campo $name in keystore.properties."
        }
    }
    check(rootProject.file(releaseKeyProperties.getProperty("storeFile")).isFile) {
        "Firma richiesta: il file keystore configurato non esiste."
    }
}

android {
    namespace = "it.hendorsoftware.medishelf"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "it.hendorsoftware.medishelf"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.0-alpha03"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (signRelease) {
            create("privateRelease") {
                storeFile = rootProject.file(releaseKeyProperties.getProperty("storeFile"))
                storePassword = releaseKeyProperties.getProperty("storePassword")
                keyAlias = releaseKeyProperties.getProperty("keyAlias")
                keyPassword = releaseKeyProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            if (signRelease) {
                signingConfig = signingConfigs.getByName("privateRelease")
            }
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
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
