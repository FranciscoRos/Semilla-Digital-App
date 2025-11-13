package com.semilladigital.courses.ui

import com.semilladigital.courses.domain.model.Course
// import java.time.LocalDate // Ya no lo necesitamos aquí

data class CourseState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(), // La lista *filtrada*
    val error: String? = null,
    val searchQuery: String = "",

    val selectedCourse: Course? = null, // El curso que se muestra en el modal

    val availableTemas: List<String> = emptyList(),
    val selectedTema: String = "Todos", // El filtro de tema actual

    val availableModalidades: List<String> = listOf("Todas", "En Línea", "Presencial"),
    val selectedModalidad: String = "Todas", // El filtro de modalidad actual

    val isFilterDialogVisible: Boolean = false,


    val availableDateFilters: List<String> = listOf("Todos", "Próximos", "Pasados"),
    val selectedDateFilter: String = "Todos" // Default a "Todos"
)