package com.semilladigital.geomap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

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
            "trigo" in texto -> "🌾"
            else -> "🌱"
        }
    }

    fun getEmojiForUbicacionType(tipo: String): String {
        return when (tipo.uppercase()) {
            "CENTRO_ACOPIO" -> "📦"
            "SEDE_GOBIERNO" -> "🏛️"
            "PROVEEDOR_INSUMOS" -> "🛒"
            "FINANCIAMIENTO" -> "💰"
            "EDUCACION" -> "📚"
            else -> "📍"
        }
    }

    fun getColorForUbicacionType(tipo: String): Long {
        return when (tipo.uppercase()) {
            "CENTRO_ACOPIO" -> 0xFF8BC34A // Verde Lima
            "SEDE_GOBIERNO" -> 0xFF2196F3 // Azul
            "PROVEEDOR_INSUMOS" -> 0xFFFF9800 // Naranja
            "FINANCIAMIENTO" -> 0xFFFFC107 // Ámbar (Amarillo)
            "EDUCACION" -> 0xFF9C27B0 // Púrpura
            else -> 0xFF795548 // Marrón (Default)
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

    fun createColoredTextIcon(text: String, colorHex: Long): BitmapDescriptor? {
        val emojiSize = 72f
        val padding = 12f
        val cornerRadius = 24f

        val textPaint = Paint().apply {
            textSize = emojiSize
            textAlign = Paint.Align.CENTER
            color = android.graphics.Color.BLACK
        }

        val textWidth = textPaint.measureText(text)
        val iconWidth = (textWidth + 2 * padding).toInt()
        val iconHeight = (emojiSize + 2 * padding).toInt()

        val bitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint().apply {
            color = colorHex.toInt()
            style = Paint.Style.FILL
        }

        val rect = RectF(0f, 0f, iconWidth.toFloat(), iconHeight.toFloat())
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)

        val textY = (iconHeight / 2) + (textPaint.textSize / 2) - textPaint.fontMetrics.descent
        canvas.drawText(text, (iconWidth / 2).toFloat(), textY, textPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}