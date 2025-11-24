package com.semilladigital.geomap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

object MapUtils {

    // Asigna un emoji basado en las actividades.
    // Ahora recibe una lista simple de Strings (del modelo de Dominio)
    fun getEmojiForActivity(actividades: List<String>): String {
        val texto = actividades.joinToString(" ").lowercase()
        return when {
            "tomate" in texto -> "🍅"
            "maiz" in texto || "maíz" in texto -> "🌽"
            "frijol" in texto -> "🫘"
            "ganad" in texto || "borrego" in texto -> "🐑"
            "abeja" in texto || "miel" in texto -> "🐝"
            "caña" in texto -> "🎋"
            "trigo" in texto -> "🌾"
            else -> "🌱" // Default
        }
    }

    // Calcula el centro geométrico de un polígono para poner el marcador
    fun calculateCentroid(points: List<LatLng>): LatLng {
        var latSum = 0.0
        var lngSum = 0.0
        points.forEach {
            latSum += it.latitude
            lngSum += it.longitude
        }
        return LatLng(latSum / points.size, lngSum / points.size)
    }

    // Convierte un Texto/Emoji a un Icono de Mapa (BitmapDescriptor)
    fun textToBitmapDescriptor(text: String, context: Context): BitmapDescriptor? {
        val paint = Paint().apply {
            textSize = 60f
            textAlign = Paint.Align.CENTER
        }
        val width = paint.measureText(text).toInt() + 10
        val height = (paint.textSize).toInt() + 10

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(text, (width / 2).toFloat(), (height - 10).toFloat(), paint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // NOTA: El método 'parseCoordinates' ha sido ELIMINADO intencionalmente.
    // Esa lógica de convertir datos crudos ahora vive en el Mapper (GeomapDtos.kt -> toDomain),
    // cumpliendo con Clean Architecture.
}