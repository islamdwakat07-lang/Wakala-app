package com.wakala.generator

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.text.style.UnderlineSpan
import java.io.File
import java.io.FileOutputStream

private const val IQRAR_PAGE_WIDTH = 595
private const val IQRAR_PAGE_HEIGHT = 842
private const val IQRAR_MARGIN = 42f

private fun buildIqrarSpannable(segments: List<TextSegment>): SpannableStringBuilder {
    val builder = SpannableStringBuilder()
    val underlineRanges = mutableListOf<Pair<Int, Int>>()
    for (segment in segments) {
        val start = builder.length
        builder.append(segment.text)
        if (segment.underlined) underlineRanges += start to builder.length
    }
    underlineRanges.forEach { (start, end) ->
        builder.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return builder
}

fun generateIqrarTanzimPdf(context: Context, data: IqrarTanzimData): File {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(IQRAR_PAGE_WIDTH, IQRAR_PAGE_HEIGHT, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    val titlePaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 15f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        color = 0xFF111111.toInt()
    }
    val subPaint = TextPaint(titlePaint).apply {
        textSize = 11f
        typeface = Typeface.DEFAULT
    }
    val linePaint = Paint().apply {
        color = 0xFF111111.toInt()
        strokeWidth = 1.2f
    }
    val bodyPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 12.5f
        typeface = Typeface.DEFAULT
        color = 0xFF111111.toInt()
    }
    val bottomPaint = TextPaint(bodyPaint).apply { textSize = 12f }

    val centerX = IQRAR_PAGE_WIDTH / 2f
    var y = IQRAR_MARGIN + 8f

    val headerRightX = IQRAR_PAGE_WIDTH - IQRAR_MARGIN
    val headerLeftX = IQRAR_MARGIN
    val arabicHeaderPaint = TextPaint(subPaint).apply { textAlign = Paint.Align.RIGHT }
    val hebrewHeaderPaint = TextPaint(subPaint).apply { textAlign = Paint.Align.LEFT }
    val arabicTitleHeaderPaint = TextPaint(titlePaint).apply { textSize = 12.5f; textAlign = Paint.Align.RIGHT }
    val hebrewTitleHeaderPaint = TextPaint(titlePaint).apply { textSize = 12.5f; textAlign = Paint.Align.LEFT }

    canvas.drawText("يهودا والسامرة", headerRightX, y, arabicHeaderPaint)
    canvas.drawText("יהודה ושומרון", headerLeftX, y, hebrewHeaderPaint)
    y += 18f
    canvas.drawText("دائرة التنظيم المركزية", headerRightX, y, arabicTitleHeaderPaint)
    canvas.drawText("לשכת התכנון המרכזי", headerLeftX, y, hebrewTitleHeaderPaint)
    y += 18f
    canvas.drawText("رام الله - ص.ب 731 تلفون: 953372 - 952324", headerRightX, y, arabicHeaderPaint)
    canvas.drawText("רמאללה ת.ד. 731 טל: 953372 - 952324", headerLeftX, y, hebrewHeaderPaint)
    y += 14f
    canvas.drawLine(IQRAR_MARGIN, y, IQRAR_PAGE_WIDTH - IQRAR_MARGIN, y, linePaint)
    y += 28f

    canvas.drawText("اقرار", centerX, y, titlePaint)
    y += 28f

    val spannableBody = buildIqrarSpannable(buildIqrarTanzimSegments(data))
    val bodyWidth = (IQRAR_PAGE_WIDTH - 2 * IQRAR_MARGIN).toInt()

    val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        StaticLayout.Builder
            .obtain(spannableBody, 0, spannableBody.length, bodyPaint, bodyWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setLineSpacing(0f, 1.45f)
            .setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD)
            .build()
    } else {
        @Suppress("DEPRECATION")
        StaticLayout(
            spannableBody, bodyPaint, bodyWidth,
            Layout.Alignment.ALIGN_NORMAL, 1.45f, 0f, false
        )
    }

    canvas.save()
    canvas.translate(IQRAR_MARGIN, y)
    staticLayout.draw(canvas)
    canvas.restore()
    y += staticLayout.height + 40f

    val rightX = IQRAR_PAGE_WIDTH - IQRAR_MARGIN
    val leftX = IQRAR_MARGIN
    val rightPaint = TextPaint(bottomPaint).apply { textAlign = Paint.Align.RIGHT }
    val leftPaint = TextPaint(bottomPaint).apply { textAlign = Paint.Align.LEFT }

    canvas.drawText("مقدم الطلب: ......................", rightX, y, rightPaint)
    val dateLabel = "التاريخ: " + data.dateText.ifBlank { "......../......../............" }
    canvas.drawText(dateLabel, leftX, y, leftPaint)

    document.finishPage(page)

    val outputDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
    val outputFile = File(outputDir, "اقرار-للتنظيم.pdf")
    FileOutputStream(outputFile).use { document.writeTo(it) }
    document.close()

    return outputFile
}
