package com.semilladigital.courses.domain.use_case

import com.semilladigital.courses.domain.model.Course
import com.semilladigital.courses.domain.repository.CourseRepository
import javax.inject.Inject

// Esta clase tiene una sola tarea: obtener los cursos
class GetCoursesUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    // 'invoke' nos permite llamar a la clase como si fuera una función
    suspend operator fun invoke(): Result<List<Course>> {
        return repository.getCourses()
    }
}