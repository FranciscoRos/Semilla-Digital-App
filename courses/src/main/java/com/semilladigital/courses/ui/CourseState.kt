package com.semilladigital.courses.ui

import com.semilladigital.courses.domain.model.Course

data class CourseState(
    val isLoading: Boolean = false,

    val courses: List<Course> = emptyList(),
    val recommendedCourses: List<Course> = emptyList(),

    val error: String? = null,
    val searchQuery: String = "",

    val selectedCourse: Course? = null,

    val availableTemas: List<String> = emptyList(),
    val selectedTema: String = "Todos",

    val availableModalidades: List<String> = listOf("Todas", "En Línea", "Presencial"),
    val selectedModalidad: String = "Todas",

    val isFilterDialogVisible: Boolean = false,

    val availableDateFilters: List<String> = listOf("Todos", "Próximos", "Pasados"),
    val selectedDateFilter: String = "Todos"
)