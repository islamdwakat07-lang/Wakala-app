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

// A4 page size in points at 72 dpi - the unit PdfDocument expects.
private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MARGIN = 42f

/** Turns the shared segment list into an Android Spannable with underline spans applied. */
private fun buildSpannable(segments: List<TextSegment>): SpannableStringBuilder {
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

/**
 * Renders the وكالة on a single A4 page and writes it to a PDF file in the app's cache
 * directory, returning that file. No network access is used anywhere in this function.
 */
fun generateWakalaPdf(context: Context, data: WakalaData): File {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
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
        textSize = 13f
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

    val centerX = PAGE_WIDTH / 2f
    var y = MARGIN + 8f

    // Letterhead
    canvas.drawText("وكالة خصوصية", centerX, y, titlePaint)
    y += 20f
    canvas.drawText("مكتب المحامي", centerX, y, subPaint)
    y += 18f
    canvas.drawText("عبدالله دويكات", centerX, y, subPaint)
    y += 18f
    canvas.drawText("0594333138", centerX, y, subPaint)
    y += 14f
    canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
    y += 28f

    // Body - justified, right-to-left, with underlines matching the paper template
    val spannableBody = buildSpannable(buildWakalaSegments(data))
    val bodyWidth = (PAGE_WIDTH - 2 * MARGIN).toInt()

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
    canvas.translate(MARGIN, y)
    staticLayout.draw(canvas)
    canvas.restore()
    y += staticLayout.height + 25f

    // Bottom section: date + الموكل on the right, lawyer's certification on the left,
    // with empty space below each for the real (handwritten) signature and stamp.
    val bottomY = y
    val rightX = PAGE_WIDTH - MARGIN
    val leftX = MARGIN

    val rightPaint = TextPaint(bottomPaint).apply { textAlign = Paint.Align.RIGHT }
    val leftPaint = TextPaint(bottomPaint).apply { textAlign = Paint.Align.LEFT }

    val dateLabel = "تحريراً في هذا اليوم " + data.dateText.ifBlank { "......../......../............" }
    canvas.drawText(dateLabel, rightX, bottomY, rightPaint)
    canvas.drawText(if (data.hasSecond) "الموكلان" else "الموكل", rightX, bottomY + 22f, rightPaint)

    canvas.drawText("أصادق على صحة التوقيع والتوكيل", leftX, bottomY, leftPaint)
    canvas.drawText("المحامي عبدالله دويكات", leftX, bottomY + 22f, leftPaint)

    document.finishPage(page)

    val outputDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
    val outputFile = File(outputDir, "وكالة-خصوصية.pdf")
    FileOutputStream(outputFile).use { document.writeTo(it) }
    document.close()

    return outputFile
}
