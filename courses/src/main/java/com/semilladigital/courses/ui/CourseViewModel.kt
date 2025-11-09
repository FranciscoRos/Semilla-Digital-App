package com.semilladigital.courses.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.semilladigital.courses.domain.use_case.GetCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase
) : ViewModel() {

    // El _state es privado y mutable (solo el ViewModel lo edita)
    private val _state = MutableStateFlow(CourseState())
    // El state es público e inmutable (la UI solo lo lee)
    val state: StateFlow<CourseState> = _state.asStateFlow()

    // 'init' se llama cuando el ViewModel es creado por primera vez
    init {
        loadCourses()
    }

    // Función para cargar los cursos desde la API
    private fun loadCourses() {
        viewModelScope.launch {
            // 1. Poner la UI en estado de "cargando"
            _state.update { it.copy(isLoading = true) }

            // 2. Llamar al Caso de Uso (que llama al Repo, que llama a la API)
            val result = getCoursesUseCase()

            // 3. Manejar el resultado
            result.fold(
                onSuccess = { courses ->
                    // Éxito: actualizar el estado con los cursos
                    _state.update {
                        it.copy(
                            isLoading = false,
                            courses = courses,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    // Error: actualizar el estado con el mensaje de error
                    _state.update {
                        it.copy(
                            isLoading = false,
                            courses = emptyList(),
                            error = error.message ?: "Error desconocido"
                        )
                    }
                }
            )
        }
    }

    // Aquí añadiremos más funciones luego (ej. onSearchChanged)
}