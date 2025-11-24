package com.semilladigital.geomap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.semilladigital.geomap.data.ParcelaDto

object MapUtils {

    fun getEmojiForActivity(actividades: List<String>): String {
        val texto = actividades.joinToString(" ").lowercase()
        return when {
            "tomate" in texto -> "🍅"
            "maiz" in texto || "maíz" in texto -> "🌽"
            "frijol" in texto -> "🫘"
            "ganad" in texto || "borrego" in texto -> "🐑"
            "abeja" in texto || "miel" in texto -> "🐝"
            "caña" in texto -> "🎋"
            else -> "🌱" // Default
        }
    }

    fun calculateCentroid(points: List<LatLng>): LatLng {
        var latSum = 0.0
        var lngSum = 0.0
        points.forEach {
            latSum += it.latitude
            lngSum += it.longitude
        }
        return LatLng(latSum / points.size, lngSum / points.size)
    }

    // Convierte el formato extraño del JSON a LatLng de Google
    fun parseCoordinates(raw: List<Map<String, Double>>): List<LatLng> {
        return raw.mapNotNull {
            val lat = it["lat"] ?: it["latitud"]
            val lng = it["lng"] ?: it["longitud"]
            if (lat != null && lng != null) LatLng(lat, lng) else null
        }
    }

    // Crea un icono de mapa a partir de un String (Emoji)
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
}