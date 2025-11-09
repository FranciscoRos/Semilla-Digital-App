package com.semilladigital.courses.data.remote

import com.semilladigital.courses.data.remote.dto.CourseDto
import com.semilladigital.courses.data.remote.dto.DataWrapperDto
import retrofit2.http.GET

interface CourseApiService {

    // Tu ruta es: Route::get('/cursos', [CursosController::class, 'index']);
    @GET("cursos")
    suspend fun getCourses(): DataWrapperDto<List<CourseDto>>
}