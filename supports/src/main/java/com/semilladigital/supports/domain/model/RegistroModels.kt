package com.semilladigital.supports.domain.model

import com.google.gson.annotations.SerializedName

data class RegistroResponse(
    val data: RegistroData
)

data class RegistrosListResponse(
    val data: List<RegistroData>
)

data class RegistroData(
    val id: String,
    val Estado: String,
    val HistorialApoyo: List<HistorialApoyoItem> = emptyList(),
    @SerializedName("Usuario") val Usuario: UsuarioDetalle,
    @SerializedName("CamposExtra") val CamposExtra: Map<String, Any>? = emptyMap()
)

data class HistorialApoyoItem(
    val idApoyo: String,
    val nombre_programa: String
)

data class UsuarioDetalle(
    val idUsuario: String,
    @SerializedName("Nombre") val Nombre: String,
    @SerializedName("Apellido1") val Apellido1: String? = null,
    @SerializedName("Apellido2") val Apellido2: String? = null,
    @SerializedName("Correo") val Correo: String? = null,
    @SerializedName("Telefono") val Telefono: String? = null,
    @SerializedName("Curp") val Curp: String? = null,
    @SerializedName("Rfc") val Rfc: String? = null,
    @SerializedName("Ine") val Ine: String? = null,
    @SerializedName("FechaNacimiento") val FechaNacimiento: String? = null,
    @SerializedName("Domicilio") val Domicilio: DomicilioDetalle? = null,
    @SerializedName("Parcela") val Parcela: List<ParcelaDetalle> = emptyList()
)

data class DomicilioDetalle(
    @SerializedName("Calle") val Calle: String? = null,
    @SerializedName("Colonia") val Colonia: String? = null,
    @SerializedName("Municipio") val Municipio: String? = null,
    @SerializedName("Ciudad") val Ciudad: String? = null,
    @SerializedName("Estado") val Estado: String? = null,
    @SerializedName("CodigoPostal") val CodigoPostal: String? = null,
    @SerializedName("Referencia") val Referencia: String? = null
)

data class ParcelaDetalle(
    val idParcela: String? = null,
    val nombre: String? = null,
    val area: Double? = null,
    val ciudad: String? = null,
    val municipio: String? = null,
    val localidad: String? = null,
    val direccionAdicional: String? = null,
    // CAMBIO CRÍTICO: Debe ser una lista de objetos Coordenada
    val coordenadas: List<Coordenada>? = null,
    val usos: List<UsoParcela> = emptyList()
)

data class Coordenada(
    val lat: Double,
    val lng: Double
)

data class UsoParcela(
    val area: String,
    val actividadesEspecificas: List<String> = emptyList()
)

enum class EstatusApoyo {
    DISPONIBLE,
    YA_INSCRITO,
    NO_CUMPLE_REQUISITOS
}

data class ApoyoUiItem(
    val apoyo: Apoyo,
    val estatus: EstatusApoyo,
    val motivoNoEligible: String? = null
)