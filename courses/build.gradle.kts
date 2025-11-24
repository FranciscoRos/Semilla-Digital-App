plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.semilladigital.courses"
    compileSdk = 36

    buildFeatures {
        compose = true
    }

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    //Para acceder a los objetos de ui
    implementation(project(":core-ui"))
    implementation(project(":core-data"))
    implementation(project(":chatbot"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt para inyección de dependencias
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //para consumo de api
    // 1. Para que Hilt pueda encontrar lo que definiste en :core-data
    implementation(project(":core-data"))
    implementation(project(":core-ui"))
    // 2. Para resolver el error de @SerializedName (Gson)
    implementation(libs.retrofit.converter.gson)

    // 3. Para resolver el error de 'Retrofit' (Retrofit)
    implementation(libs.retrofit.core)


    implementation(project(":core-ui"))

    // 2. Librerías de Hilt y Lifecycle para ViewModels
    implementation(libs.androidx.hilt.navigation.compose) // Para hiltViewModel()
    implementation(libs.androidx.lifecycle.viewmodel.compose) // Para el ViewModel
    implementation(libs.androidx.lifecycle.runtime.compose) // Para collectAsStateWithLifecycle

    // 3. Librerías de Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.material.icons.extended)
    //Para usar librerias nuevas en sdk viejo
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
}