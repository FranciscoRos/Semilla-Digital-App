package com.semilladigital.courses.ui

import com.semilladigital.courses.domain.model.Course


sealed class CourseEvent {
    data class OnSearchQueryChanged(val query: String) : CourseEvent()
    data class OnShowDetails(val course: Course) : CourseEvent()
    object OnHideDetails : CourseEvent()
    data class OnFilterTemaChanged(val tema: String) : CourseEvent()
    data class OnFilterModalidadChanged(val modalidad: String) : CourseEvent()

    object OnShowFilterDialog : CourseEvent()
    object OnHideFilterDialog : CourseEvent()

    // --- CAMBIOS AQUÍ ---
    // 1. Quitamos los eventos del DatePicker
    // object OnShowDatePicker : CourseEvent()
    // object OnHideDatePicker : CourseEvent()
    // data class OnDateSelected(val dateMillis: Long?) : CourseEvent()

    // 2. Añadimos el nuevo evento para los chips de fecha
    data class OnDateFilterChanged(val filter: String) : CourseEvent()
}