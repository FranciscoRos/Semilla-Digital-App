package com.semilladigital.courses.domain.repository

import com.semilladigital.courses.domain.model.Course
import kotlinx.coroutines.flow.StateFlow

interface CourseRepository {
    val courses: StateFlow<List<Course>>
    suspend fun refreshCourses(): Result<Unit>
    suspend fun getCourses(forceRefresh: Boolean): Result<List<Course>>
}