package com.semilladigital.supports.domain.model

data class Requerimiento(
    val nombre: String? = null,
    val valor: String? = null,
    val Direccion: DireccionRequerimiento? = null,
    val Requisito: String? = null,
    val type: String? = null,
    val config: ApoyoConfig? = null,
    val fieldName: String? = null,
    val validation: RequerimientoValidation? = null
)

data class RequerimientoValidation(
    val operator: String? = null,
    val value: String? = null
)

data class DireccionRequerimiento(
    val calle: String? = null
)

data class ApoyoConfig(
    val areas: List<String>? = null,
    val actividades: List<String>? = null,
    val hectareas: Double? = null
)

data class BeneficiadoDetalle(
    val Usuario: UsuarioInfo? = null,
    val parcela: ParcelaInfo? = null,
    val fechaRegistro: String? = null,
    val agendacionCita: AgendacionCita? = null
)

data class UsuarioInfo(
    val idUsuario: String? = null,
    val Nombre: String? = null,
    val Apellido1: String? = null,
    val Apellido2: String? = null,
    val Curp: String? = null,
    val Correo: String? = null,
    val Telefono: String? = null,
    val Ine: String? = null,
    val Rfc: String? = null
)

data class ParcelaInfo(
    val idParcela: String? = null,
    val ciudad: String? = null,
    val municipio: String? = null,
    val localidad: String? = null,
    val direccionAdicional: String? = null,
    val area: Double? = null,
    val nombre: String? = null,
    val fechaRegistro: String? = null
)

data class AgendacionCita(
    val Administrador: String? = null,
    val FechaCita: String? = null,
    val HoraCita: String? = null,
    val PropositoCita: String? = null
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
    val Requerimientos: List<Requerimiento> = emptyList(),

    val Beneficiados: List<BeneficiadoDetalle> = emptyList(),

    val Creado: String,
    val Actualizado: String
)

data class ApoyosResponse(
    val data: List<Apoyo>
)