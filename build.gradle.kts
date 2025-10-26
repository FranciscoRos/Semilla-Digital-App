/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Esto va en C:\Users\franc\AndroidStudioProjects\Semilla-Digital\build.gradle.kts
// En C:\Users\franc\AndroidStudioProjects\Semilla-Digital\build.gradle.kts (EL DE LA RAÍZ)

plugins {
    // El que necesita el módulo :app
    alias(libs.plugins.android.application) apply false

    // El que necesita el módulo :courses (y otros de librería)
    alias(libs.plugins.android.library) apply false

    // El que necesita el módulo :test-app (¡ESTE ES EL NUEVO!)
    alias(libs.plugins.android.test) apply false // <--- AÑADE ESTA LÍNEA

    // Plugins de Kotlin
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false

    // Hilt (Inyección de dependencias)
    alias(libs.plugins.hilt.gradle) apply false

    // KSP (Procesador de anotaciones)
    alias(libs.plugins.ksp) apply false

    // Compose
    alias(libs.plugins.compose.compiler) apply false
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Do not add plugins here. They are declared in libs.versions.toml and applied in module-level build files.
