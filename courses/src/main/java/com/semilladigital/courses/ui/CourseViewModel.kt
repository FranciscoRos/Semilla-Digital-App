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
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(CourseState())
    val state: StateFlow<CourseState> = _state.asStateFlow()

    private var masterCourseList: List<Course> = emptyList()
    private val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        loadCourses()
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

    private fun loadCourses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = getCoursesUseCase()
            result.fold(
                onSuccess = { courses ->
                    masterCourseList = courses
                    val temas = courses.mapNotNull { it.tema }.distinct().toMutableList()
                    temas.add(0, "Todos")

                    // Lógica de Recomendación basada en actividades guardadas
                    val userActivities = sessionStorage.getActividades()

                    val recommended = courses.filter { course ->
                        course.tema != null && userActivities.any { activity ->
                            activity.equals(course.tema, ignoreCase = true)
                        }
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            courses = courses,
                            recommendedCourses = recommended,
                            availableTemas = temas,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            courses = emptyList(),
                            recommendedCourses = emptyList(),
                            error = error.message ?: "Error desconocido"
                        )
                    }
                }
            )
        }
    }
}