package com.example.pharmarestock.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.pharmarestock.database.CartItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generatePdf(context: Context, cartItems: List<CartItem>, userName: String, branchName: String): File? {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // 1. Title
        titlePaint.textSize = 24f
        titlePaint.isFakeBoldText = true
        titlePaint.color = Color.BLACK
        canvas.drawText("Pharmacy Restock Order", 50f, 50f, titlePaint)

        // 2. Header Info (Date, Name, Branch)
        paint.textSize = 14f
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        var currentY = 80f // Starting vertical position for header text

        canvas.drawText("Date: $currentDate", 50f, currentY, paint)
        currentY += 20f

        if (userName.isNotBlank()) {
            canvas.drawText("Prepared by: $userName", 50f, currentY, paint)
            currentY += 20f
        }

        if (branchName.isNotBlank()) {
            canvas.drawText("Branch/Store: $branchName", 50f, currentY, paint)
            currentY += 20f
        }

        currentY += 20f // Extra space before the table

        // 3. Table Headers
        paint.isFakeBoldText = true
        paint.textSize = 16f
        canvas.drawText("Medicine Name", 50f, currentY, paint)
        canvas.drawText("Quantity", 400f, currentY, paint)

        currentY += 10f
        canvas.drawLine(50f, currentY, 545f, currentY, paint)
        currentY += 25f

        // 4. List Items
        paint.isFakeBoldText = false
        for (item in cartItems) {
            canvas.drawText(item.medicineName, 50f, currentY, paint)
            canvas.drawText(item.quantity.toString(), 400f, currentY, paint)
            currentY += 30f
        }

        pdfDocument.finishPage(page)

        val directory = File(context.cacheDir, "pdfs")
        if (!directory.exists()) directory.mkdir()

        val file = File(directory, "RestockOrder.pdf")

        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}