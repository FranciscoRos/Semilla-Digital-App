package com.semilladigital.geomap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Color
import android.graphics.RectF
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
            "MERCADO_LOCAL" -> "🛒"
            "FINANCIAMIENTO" -> "💰"
            "EDUCACION" -> "📚"
            else -> "📍"
        }
    }

    fun getHueForUbicacionType(tipo: String): Float {
        return when (tipo.uppercase()) {
            "CENTRO_ACOPIO" -> BitmapDescriptorFactory.HUE_GREEN
            "SEDE_GOBIERNO" -> BitmapDescriptorFactory.HUE_AZURE
            "PROVEEDOR_INSUMOS" -> BitmapDescriptorFactory.HUE_ORANGE
            "FINANCIAMIENTO" -> BitmapDescriptorFactory.HUE_YELLOW
            "EDUCACION" -> BitmapDescriptorFactory.HUE_MAGENTA
            else -> BitmapDescriptorFactory.HUE_RED
        }
    }

    fun getPinColorHexForUbicacionType(tipo: String): Int {
        return when (tipo.uppercase()) {
            "CENTRO_ACOPIO" -> Color.parseColor("#4CAF50")
            "SEDE_GOBIERNO" -> Color.parseColor("#2196F3")
            "PROVEEDOR_INSUMOS" -> Color.parseColor("#FF9800")
            "FINANCIAMIENTO" -> Color.parseColor("#FFC107")
            "EDUCACION" -> Color.parseColor("#9C27B0")
            else -> Color.parseColor("#F44336")
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

    fun createColoredPinIconWithText(emoji: String, colorHex: Int): BitmapDescriptor? {
        val pinWidth = 80
        val pinHeight = 110
        val circleRadius = 35f
        val bottomPoint = 100f
        val circleY = 40f

        val bitmap = Bitmap.createBitmap(pinWidth, pinHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val pinPath = Path()
        pinPath.addCircle((pinWidth / 2).toFloat(), circleY, circleRadius, Path.Direction.CW)
        pinPath.lineTo((pinWidth / 2 - 10).toFloat(), circleY + circleRadius)
        pinPath.lineTo((pinWidth / 2).toFloat(), bottomPoint)
        pinPath.lineTo((pinWidth / 2 + 10).toFloat(), circleY + circleRadius)
        pinPath.close()

        val backgroundPaint = Paint().apply {
            color = colorHex
            isAntiAlias = true
        }

        val shadowPaint = Paint().apply {
            color = Color.BLACK
            alpha = 50
            isAntiAlias = true
        }
        canvas.drawOval(RectF(15f, bottomPoint - 15f, pinWidth - 15f, bottomPoint + 5f), shadowPaint)

        canvas.drawPath(pinPath, backgroundPaint)

        val textPaint = Paint().apply {
            textSize = 50f
            textAlign = Paint.Align.CENTER
            color = Color.BLACK
            isAntiAlias = true
        }

        val textY = circleY + (textPaint.textSize / 2) - textPaint.fontMetrics.descent * 0.7f
        canvas.drawText(emoji, (pinWidth / 2).toFloat(), textY, textPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}