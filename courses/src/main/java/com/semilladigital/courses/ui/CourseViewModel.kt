package com.semilladigital.courses.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.app.core.data.storage.SessionStorage
import com.semilladigital.courses.domain.model.Course
import com.semilladigital.courses.domain.use_case.GetCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val sessionStorage: SessionStorage // Inyectamos para leer intereses
) : ViewModel() {

    private val _state = MutableStateFlow(CourseState())
    val state: StateFlow<CourseState> = _state.asStateFlow()

    private var masterCourseList: List<Course> = emptyList()
    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        loadData()
    }

    fun onEvent(event: CourseEvent) {
        when (event) {
            is CourseEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                applyFilters()
            }
            is CourseEvent.OnFilterTemaChanged -> {
                _state.update { it.copy(selectedTema = event.tema) }
                applyFilters()
            }
            is CourseEvent.OnFilterModalidadChanged -> {
                _state.update { it.copy(selectedModalidad = event.modalidad) }
                applyFilters()
            }
            is CourseEvent.OnShowDetails -> {
                _state.update { it.copy(selectedCourse = event.course) }
            }
            is CourseEvent.OnHideDetails -> {
                _state.update { it.copy(selectedCourse = null) }
            }
            is CourseEvent.OnShowFilterDialog -> {
                _state.update { it.copy(isFilterDialogVisible = true) }
            }
            is CourseEvent.OnHideFilterDialog -> {
                _state.update { it.copy(isFilterDialogVisible = false) }
            }
            is CourseEvent.OnDateFilterChanged -> {
                _state.update { it.copy(selectedDateFilter = event.filter) }
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        val currentState = _state.value
        val today = LocalDate.now()

        val filteredList = masterCourseList.filter { course ->
            val searchMatch = (course.titulo.contains(currentState.searchQuery, ignoreCase = true) ||
                    course.descripcion.contains(currentState.searchQuery, ignoreCase = true))

            val temaMatch = (currentState.selectedTema == "Todos" ||
                    course.tema.equals(currentState.selectedTema, ignoreCase = true))

            val modalidadMatch = (currentState.selectedModalidad == "Todas" ||
                    course.modalidad.equals(currentState.selectedModalidad, ignoreCase = true))

            val courseDate = parseCourseDate(course.fechaCurso)
            val dateMatch = when (currentState.selectedDateFilter) {
                "Próximos" -> courseDate == null || courseDate.isAfter(today) || courseDate.isEqual(today)
                "Pasados" -> courseDate != null && courseDate.isBefore(today)
                else -> true
            }

            searchMatch && temaMatch && modalidadMatch && dateMatch
        }

        _state.update { it.copy(courses = filteredList) }
    }

    private fun parseCourseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, apiDateFormatter)
        } catch (e: DateTimeParseException) {
            Log.e("CourseViewModel", "Error al parsear fecha: $dateString", e)
            null
        }
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = getCoursesUseCase() // Obtiene TODOS los cursos del backend

            result.fold(
                onSuccess = { courses ->
                    masterCourseList = courses
                    val temas = courses.mapNotNull { it.tema }.distinct().toMutableList()
                    temas.add(0, "Todos")

                    // --- FILTRADO LOCAL "PARA TI" ---
                    // 1. Leemos la lista plana de intereses que guardó DashboardViewModel
                    val interesesUsuario = sessionStorage.getIntereses()

                    // 2. Filtramos localmente
                    val recomendados = if (interesesUsuario.isNotEmpty()) {
                        courses.filter { course ->
                            interesesUsuario.any { palabra ->
                                // Buscamos si la palabra clave (ej. "Tomate") está en título, descripción o tema
                                course.titulo.contains(palabra, ignoreCase = true) ||
                                        course.descripcion.contains(palabra, ignoreCase = true) ||
                                        (course.tema != null && course.tema.contains(palabra, ignoreCase = true))
                            }
                        }
                    } else {
                        // Si no hay intereses, mostramos los 5 más recientes como fallback
                        courses.take(5)
                    }

                    _state.update {
                        it.copy(
                            courses = courses,
                            recommendedCourses = recomendados,
                            availableTemas = temas,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }
}