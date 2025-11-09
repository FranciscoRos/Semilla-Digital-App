package com.semilladigital.courses.ui

import com.semilladigital.courses.domain.model.Course

// Define todos los elementos que la UI necesita para dibujarse
data class CourseState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val error: String? = null,
    val searchQuery: String = ""
)