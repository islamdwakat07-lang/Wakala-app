package com.wakala.generator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.UnderlineSpan
import java.io.File
import java.io.FileOutputStream

private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MARGIN = 48f
private const val FOOTER_HEIGHT = 40f

private data class PageRange(val startLine: Int, val endLine: Int)

private fun buildSaleSpannable(segments: List<TextSegment>): SpannableStringBuilder {
    val sb = SpannableStringBuilder()
    for (seg in segments) {
        val start = sb.length
        sb.append(seg.text)
        if (seg.underlined) {
            sb.setSpan(UnderlineSpan(), start, sb.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return sb
}

private fun drawSignatureRow(canvas: Canvas, y: Float, paint: Paint) {
    val colWidth = (PAGE_WIDTH - 2 * MARGIN) / 4
    val labels = listOf("الفريق الأول", "الفريق الثاني", "شاهد", "شاهد")
    paint.textAlign = Paint.Align.CENTER
    labels.forEachIndexed { i, label ->
        val centerX = MARGIN + colWidth * i + colWidth / 2
        canvas.drawText(label, centerX, y, paint)
        canvas.drawText("......................", centerX, y + 26f, paint)
    }
    paint.textAlign = Paint.Align.RIGHT
}

fun generateAgreementSalePdf(context: Context, data: AgreementSaleData): File {
    val segments = buildAgreementSaleSegments(data)
    val spannable = buildSaleSpannable(segments)

    val textPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 13f
        color = android.graphics.Color.BLACK
    }

    val contentWidth = (PAGE_WIDTH - 2 * MARGIN).toInt()

    val layout = StaticLayout.Builder
        .obtain(spannable, 0, spannable.length, textPaint, contentWidth)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setLineSpacing(0f, 1.45f)
        .setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD)
        .setIncludePad(false)
        .build()

    val availableHeight = PAGE_HEIGHT - 2 * MARGIN - FOOTER_HEIGHT
    val signatureBlockHeight = 90f

    val pageRanges = mutableListOf<PageRange>()
    var lineStart = 0
    val totalLines = layout.lineCount
    while (lineStart < totalLines) {
        val topOfPage = layout.getLineTop(lineStart)
        var lineEnd = lineStart
        while (lineEnd < totalLines &&
            (layout.getLineBottom(lineEnd) - topOfPage) <= availableHeight
        ) {
            lineEnd++
        }
        if (lineEnd == lineStart) lineEnd = lineStart + 1
        pageRanges.add(PageRange(lineStart, lineEnd - 1))
        lineStart = lineEnd
    }

    val lastRange = pageRanges.last()
    val lastPageTextHeight =
        layout.getLineBottom(lastRange.endLine) - layout.getLineTop(lastRange.startLine)
    val signatureFitsOnLastPage = (lastPageTextHeight + signatureBlockHeight) <= availableHeight

    val pdfDocument = PdfDocument()
    val paint = Paint().apply {
        isAntiAlias = true
        textSize = 11f
        textAlign = Paint.Align.RIGHT
        color = android.graphics.Color.BLACK
    }

    val totalPages = if (signatureFitsOnLastPage) pageRanges.size else pageRanges.size + 1

    pageRanges.forEachIndexed { index, range ->
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, index + 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        canvas.save()
        canvas.clipRect(MARGIN, MARGIN, PAGE_WIDTH - MARGIN, MARGIN + availableHeight)
        val offsetY = MARGIN - layout.getLineTop(range.startLine)
        canvas.translate(MARGIN, offsetY)
        layout.draw(canvas)
        canvas.restore()

        val isLastTextPage = index == pageRanges.size - 1
        if (isLastTextPage && signatureFitsOnLastPage) {
            val sigY = MARGIN + (layout.getLineBottom(range.endLine) - layout.getLineTop(range.startLine)) + 50f
            drawSignatureRow(canvas, sigY, paint)
        }

        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("${index + 1} / $totalPages", PAGE_WIDTH / 2f, PAGE_HEIGHT - MARGIN / 2, paint)
        paint.textAlign = Paint.Align.RIGHT

        pdfDocument.finishPage(page)
    }

    if (!signatureFitsOnLastPage) {
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, totalPages).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        drawSignatureRow(canvas, MARGIN + 60f, paint)

        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("$totalPages / $totalPages", PAGE_WIDTH / 2f, PAGE_HEIGHT - MARGIN / 2, paint)
        paint.textAlign = Paint.Align.RIGHT

        pdfDocument.finishPage(page)
    }

    val pdfDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
    val file = File(pdfDir, "اتفاقية-بيع.pdf")
    FileOutputStream(file).use { pdfDocument.writeTo(it) }
    pdfDocument.close()
    return file
}
