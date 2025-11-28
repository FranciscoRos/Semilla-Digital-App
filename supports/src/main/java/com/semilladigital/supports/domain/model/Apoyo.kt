package com.semilladigital.supports.domain.model

import com.google.gson.annotations.SerializedName

// Respuesta raíz del endpoint
data class ApoyosResponse(
    val data: List<Apoyo>
)

// Modelo principal
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
    val numero_beneficiados_actual: Int = 0,

    // Listas que pueden venir vacías
    val Requerimientos: List<Requerimiento> = emptyList(),
    val Beneficiados: List<BeneficiadoDetalle> = emptyList(),

    val Creado: String? = null,
    val Actualizado: String? = null
)

// Modelo flexible para Requerimientos (maneja la variedad de campos del JSON)
data class Requerimiento(
    val nombre: String? = null,
    val valor: String? = null,
    val Direccion: DireccionReq? = null,
    val Requisito: String? = null,
    val type: String? = null,
    val config: ConfigReq? = null,
    val fieldName: String? = null,
    val validation: ValidationReq? = null
)

data class DireccionReq(
    val calle: String? = null
)

data class ConfigReq(
    val areas: List<String>? = null,
    val actividades: List<String>? = null,
    val hectareas: Double? = null
)

data class ValidationReq(
    val operator: String? = null,
    val value: String? = null
)

// Modelos para Beneficiados y Parcelas
data class BeneficiadoDetalle(
    val Usuario: UsuarioBeneficiado? = null,
    val parcela: ParcelaBeneficiado? = null,
    val fechaRegistro: String? = null,
    val agendacionCita: CitaInfo? = null
)

data class UsuarioBeneficiado(
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

data class ParcelaBeneficiado(
    val idParcela: String? = null,
    val ciudad: String? = null,
    val municipio: String? = null,
    val localidad: String? = null,
    val direccionAdicional: String? = null,
    val coordenadas: List<Coordenada>? = null,
    val area: Double? = null,
    val nombre: String? = null,
    val usos: List<UsoParcelaBeneficiado>? = null,
    val fechaRegistro: String? = null
)

data class Coordenada(
    val lat: Double,
    val lng: Double
)

data class UsoParcelaBeneficiado(
    val area: String? = null,
    val actividadesEspecificas: List<String>? = null
)

data class CitaInfo(
    val Administrador: Any? = null, // Puede ser objeto o null
    val FechaCita: String? = null,
    val HoraCita: String? = null,
    val PropositoCita: String? = null
)