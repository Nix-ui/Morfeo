import org.jetbrains.kotlin.konan.properties.Properties
import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.sentry)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ucb.morfeo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ucb.morfeo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

fun getLocalProperty(key: String): String {
    val localProperties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")

    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { input ->
            localProperties.load(input)
        }
    } else {
        error("Error: El archivo 'local.properties' no se encuentra en la raíz del proyecto. Asegúrese de crearlo.")
    }
    return localProperties.getProperty(key) ?: error("Error: La clave '$key' no se encuentra en 'local.properties'. Por favor, añádala con su valor.")
}
val localeMapping = mapOf(
    "en-US" to "values",
    "es-ES" to "values-es",
    "es-BO" to "values-es-rBO"
)
tasks.register("downloadLocoStrings") {
    group = "localization"
    description = "Downloads strings.xml files from Localise.biz via API"

    val locoApiKey = getLocalProperty("LOCO_API_KEY")

    val resDir = file("src/main/res")

    doLast {
        localeMapping.forEach { (apiCode, resFolder) ->
            downloadFile(
                apiKey = locoApiKey,
                apiCode = apiCode,
                resFolder = resFolder,
                resDir = resDir
            )
        }
    }
}
fun downloadFile(apiKey: String, apiCode: String, resFolder: String, resDir: File) {
    println("-> Descargando [$apiCode] en $resFolder")

    val outputFile = file("$resDir/$resFolder/strings.xml")

    outputFile.parentFile.mkdirs()

    val exportUrl = "https://localise.biz/api/export/locale/$apiCode.xml?key=$apiKey&format=android"

    try {
        URL(exportUrl).openStream().use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        println("✅ Descarga de $apiCode exitosa en $resFolder/strings.xml.")
    } catch (e: Exception) {
        println("❌ ERROR al descargar $apiCode. Verifique que la clave '$apiCode' tenga contenido en Localise.biz y que la API Key sea correcta: ${e.message}")
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization) // Agregado
    implementation(libs.okhttp) // Agregado
    implementation(libs.kotlinx.serialization)

    // Firebase
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)
    implementation(libs.firebase.auth)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // UI
    implementation(libs.androidx.compose.material.extended)

    // Room
    implementation(libs.bundles.local)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)
    testImplementation(libs.room.testing)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.workmanager)

    // Utils
    implementation(libs.jwt.decode)
    implementation(libs.datastore)
    implementation(libs.androidx.work.runtime.ktx)
    // Vico - Charts
    implementation(libs.bundles.vico)
}
sentry {
    org.set("universidad-catolica-bolivi-tm")
    projectName.set("morfeo")
    includeSourceContext.set(true)
}