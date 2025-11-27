package com.semilladigital.courses.domain.use_case

import com.semilladigital.courses.domain.model.Course
import com.semilladigital.courses.domain.repository.CourseRepository
import javax.inject.Inject

class GetCoursesUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<List<Course>> {
        return repository.getCourses(forceRefresh)
    }
}