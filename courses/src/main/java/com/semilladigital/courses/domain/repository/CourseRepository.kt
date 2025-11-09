package com.semilladigital.courses.domain.repository

import com.semilladigital.courses.domain.model.Course // Crearemos este modelo en un segundo

// Esta es la interfaz que el ViewModel usará.
// Define "qué" se puede hacer.
interface CourseRepository {

    // Usaremos un 'Result' para manejar éxito o error
    suspend fun getCourses(): Result<List<Course>>
}