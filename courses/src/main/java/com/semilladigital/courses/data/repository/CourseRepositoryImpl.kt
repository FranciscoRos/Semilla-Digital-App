package com.semilladigital.courses.data.repository

import com.semilladigital.courses.data.remote.CourseApiService
import com.semilladigital.courses.data.remote.dto.CourseDto
import com.semilladigital.courses.domain.model.Course
import com.semilladigital.courses.domain.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val apiService: CourseApiService
) : CourseRepository {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    override val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private var lastFetchTime: Long = 0
    private val CACHE_TIMEOUT = 5 * 60 * 1000 // 5 minutos

    override suspend fun refreshCourses(): Result<Unit> {
        return try {
            val response = apiService.getCourses()
            // Asegúrate que tu response.data sea la lista.
            // Si usas DataWrapper, sería response.data.map { ... }
            val domainCourses = response.data.map { it.toDomain() }
            _courses.value = domainCourses
            lastFetchTime = System.currentTimeMillis()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCourses(forceRefresh: Boolean): Result<List<Course>> {
        val isCacheValid = (System.currentTimeMillis() - lastFetchTime) < CACHE_TIMEOUT

        if (!forceRefresh && _courses.value.isNotEmpty() && isCacheValid) {
            return Result.success(_courses.value)
        }
        return try {
            refreshCourses()
            Result.success(_courses.value)
        } catch (e: Exception) {
            if (_courses.value.isNotEmpty()) {
                Result.success(_courses.value)
            } else {
                Result.failure(e)
            }
        }
    }

    private fun CourseDto.toDomain(): Course {
        return Course(
            id = id,
            titulo = titulo,
            descripcion = descripcion,
            detalles = detalles,
            tema = tema,
            modalidad = modalidad,
            fechaCurso = fechaCurso,
            direccion = direccion,
            lat = lat,
            longitud = longitud,
            url = url
        )
    }
}