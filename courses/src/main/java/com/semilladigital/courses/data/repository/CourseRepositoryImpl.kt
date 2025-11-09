package com.semilladigital.courses.data.repository

import com.semilladigital.courses.data.remote.CourseApiService
import com.semilladigital.courses.data.remote.dto.CourseDto
import com.semilladigital.courses.domain.model.Course
import com.semilladigital.courses.domain.repository.CourseRepository
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val apiService: CourseApiService
) : CourseRepository {

    override suspend fun getCourses(): Result<List<Course>> {
        return try {
            // 1. Llama a la API (ahora devuelve el wrapper)
            val response = apiService.getCourses()

            // 2. ACCEDEMOS AL OBJETO .DATA
            val courseDtoList = response.data

            // 3. Convierte el DTO al Modelo (como antes)
            val courseList = courseDtoList.map { it.toCourse() }

            // 4. Devuelve éxito
            Result.success(courseList)

        } catch (e: Exception) {
            // 5. Devuelve error
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

// La función de conversión
private fun CourseDto.toCourse(): Course {
    return Course(
        id = this.id,
        titulo = this.titulo,
        descripcion = this.descripcion,
        modalidad = this.modalidad,
        fechaCurso = this.fechaCurso,
        direccion = this.direccion, // Añadido
        url = this.url,
        lat = this.lat,
        longitud = this.longitud
    )
}