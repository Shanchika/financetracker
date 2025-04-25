package com.example.cashleaf.data

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.cashleaf.model.Transaction
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {

    fun generatePdf(transactions: List<Transaction>, fileName: String): String {
        val pdfDocument = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isAntiAlias = true
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Header
        canvas.drawText("Transaction Report", 20f, 50f, paint)
        paint.textSize = 12f
        var yOffset = 70f

        transactions.forEach { transaction ->
            canvas.drawText("ID: ${transaction.id}", 20f, yOffset, paint)
            yOffset += 20f
            canvas.drawText("Title: ${transaction.title}", 20f, yOffset, paint)
            yOffset += 20f
            canvas.drawText("Amount: ${transaction.amount}", 20f, yOffset, paint)
            yOffset += 20f
            canvas.drawText("Category: ${transaction.category}", 20f, yOffset, paint)
            yOffset += 20f
            canvas.drawText("Date: ${dateFormat.format(transaction.date)}", 20f, yOffset, paint)
            yOffset += 20f
            canvas.drawText("Type: ${if (transaction.isExpense) "Expense" else "Income"}", 20f, yOffset, paint)
            yOffset += 40f
        }

        pdfDocument.finishPage(page)

        // 📁 Save to Downloads folder
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) downloadsDir.mkdirs()

        val pdfFile = File(downloadsDir, "$fileName.pdf")
        try {
            FileOutputStream(pdfFile).use { out ->
                pdfDocument.writeTo(out)
                Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }

        return pdfFile.absolutePath
    }
}
