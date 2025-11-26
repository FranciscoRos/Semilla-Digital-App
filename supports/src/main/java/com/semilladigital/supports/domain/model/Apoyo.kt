package com.semilladigital.supports.domain.model

data class Requerimiento(
    val nombre: String? = null,
    val valor: String? = null,
    val Direccion: DireccionRequerimiento? = null,
    val Requisito: String? = null,
    val type: String? = null,
    val config: ApoyoConfig? = null
)

data class DireccionRequerimiento(
    val calle: String? = null
)

data class ApoyoConfig(
    val areas: List<String>? = null,
    val actividades: List<String>? = null,
    val hectareas: Double? = null
)

data class Apoyo(
    val id: String,
    val nombre_programa: String,
    val descripcion: String,
    val objetivo: String,
    val tipo_objetivo: String,
    val institucion_encargada: String,
    val institucion_acronimo: String,
    val direccion: String,
    val horarios_atencion: String,
    val telefono_contacto: String,
    val correo_contacto: String,
    val redes_sociales: String,
    val latitud_institucion: Double? = null,
    val longitud_institucion: Double? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val numero_beneficiados_actual: Int,
    val Requerimientos: List<Requerimiento>,
    val Beneficiados: List<String>,
    val Creado: String,
    val Actualizado: String
)

data class ApoyosResponse(
    val data: List<Apoyo>
)