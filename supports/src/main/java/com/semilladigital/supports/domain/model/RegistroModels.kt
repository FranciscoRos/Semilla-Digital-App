package com.semilladigital.supports.domain.model

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
    val Usuario: UsuarioDetalle
)

data class HistorialApoyoItem(
    val idApoyo: String,
    val nombre_programa: String
)

data class UsuarioDetalle(
    val idUsuario: String,
    val Nombre: String,
    val Parcela: List<ParcelaDetalle> = emptyList()
)

data class ParcelaDetalle(
    val idParcela: String,
    val nombre: String,
    val area: Double,
    val usos: List<UsoParcela> = emptyList()
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