package com.wakala.generator

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgreementSaleScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current

    var party1Name by remember { mutableStateOf("") }
    var party1Id by remember { mutableStateOf("") }
    var party1Residence by remember { mutableStateOf("") }

    var party2Name by remember { mutableStateOf("") }
    var party2Id by remember { mutableStateOf("") }
    var party2Residence by remember { mutableStateOf("") }

    var ownershipDescription by remember { mutableStateOf("") }
    var qitaaNumbers by remember { mutableStateOf("") }
    var hawzNumber by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var qada by remember { mutableStateOf("") }

    var party2WishDescription by remember { mutableStateOf("") }
    var party1SellDescription by remember { mutableStateOf("") }

    var priceText by remember { mutableStateOf("") }
    var transferOffice by remember { mutableStateOf("") }
    var penaltyAmount by remember { mutableStateOf("") }
    var courtName by remember { mutableStateOf("") }
    var pagesCount by remember { mutableStateOf("") }
    var copiesCount by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }

    val extraClauses = remember { mutableStateListOf<String>() }

    var voiceTarget by remember { mutableStateOf<((String) -> Unit)?>(null) }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!text.isNullOrBlank()) voiceTarget?.invoke(text)
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            tryLaunchSpeechRecognizer(context, speechLauncher)
        } else {
            Toast.makeText(context, "يلزم إذن المايكروفون للإدخال الصوتي", Toast.LENGTH_SHORT).show()
        }
    }

    fun startVoice(setter: (String) -> Unit) {
        voiceTarget = setter
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) {
            tryLaunchSpeechRecognizer(context, speechLauncher)
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun currentData() = AgreementSaleData(
        party1Name = party1Name, party1Id = party1Id, party1Residence = party1Residence,
        party2Name = party2Name, party2Id = party2Id, party2Residence = party2Residence,
        ownershipDescription = ownershipDescription.ifBlank { "حصص ارثية مشاعية في قطعتي" },
        qitaaNumbers = qitaaNumbers.ifBlank { "52 و58" },
        hawzNumber = hawzNumber.ifBlank { "12" },
        locationName = locationName.ifBlank { "جليجل بلاطه" },
        qada = qada.ifBlank { "نابلس" },
        party2WishDescription = party2WishDescription.ifBlank {
            "كامل حصص الفريق الأول الارثية المشاعية و/او الاصلية المشاعية بالغا مابلغت في قطعتي"
        },
        party1SellDescription = party1SellDescription.ifBlank {
            "كامل حصصه الارثية المشاعية و/او الاصلية المشاعية بالغا مابلغت في قطعتي"
        },
        priceText = priceText.ifBlank { "ثمانية الاف واربعمائة وثمانون (8480) دينار اردني" },
        transferOffice = transferOffice.ifBlank { "دائرة تسجيل أراضي حورون و/او كاتب عدل نابلس و/او كاتب عدل القدس" },
        penaltyAmount = penaltyAmount.ifBlank { "10000" },
        courtName = courtName.ifBlank { "نابلس" },
        pagesCount = pagesCount.ifBlank { "صفحتين" },
        copiesCount = copiesCount.ifBlank { "ثلاثة (3)" },
        dateText = dateText,
        extraClauses = extraClauses.toList()
    )

    val calendar = Calendar.getInstance()
    val datePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, day -> dateText = "$day/${month + 1}/$year" },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اتفاقية بيع") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("الفريق الأول", style = MaterialTheme.typography.titleMedium)
            VoiceTextField("اسم الفريق الأول", party1Name, { party1Name = it }, { startVoice { v -> party1Name = v } }, "مثال: أحمد محمد يوسف الأحمد")
            VoiceTextField("رقم هوية الفريق الأول", party1Id, { party1Id = it }, { startVoice { v -> party1Id = v } }, "مثال: 900112233")
            VoiceTextField("مكان سكن الفريق الأول", party1Residence, { party1Residence = it }, { startVoice { v -> party1Residence = v } }, "مثال: نابلس")

            Spacer(Modifier.height(16.dp))
            Text("الفريق الثاني", style = MaterialTheme.typography.titleMedium)
            VoiceTextField("اسم الفريق الثاني", party2Name, { party2Name = it }, { startVoice { v -> party2Name = v } }, "مثال: خالد سالم عبدالله الخطيب")
            VoiceTextField("رقم هوية الفريق الثاني", party2Id, { party2Id = it }, { startVoice { v -> party2Id = v } }, "مثال: 900445566")
            VoiceTextField("مكان سكن الفريق الثاني", party2Residence, { party2Residence = it }, { startVoice { v -> party2Residence = v } }, "مثال: نابلس")

            Spacer(Modifier.height(16.dp))
            Text("بيانات الأرض", style = MaterialTheme.typography.titleMedium)
            VoiceTextField("وصف ملكية الفريق الأول", ownershipDescription, { ownershipDescription = it }, { startVoice { v -> ownershipDescription = v } }, "مثال: حصص ارثية مشاعية في قطعتي")
            Row {
                Box(Modifier.weight(1f)) {
                    VoiceTextField("رقم القطعة", qitaaNumbers, { qitaaNumbers = it }, { startVoice { v -> qitaaNumbers = v } }, "مثال: 52 و58")
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    VoiceTextField("رقم الحوض", hawzNumber, { hawzNumber = it }, { startVoice { v -> hawzNumber = v } }, "مثال: 12")
                }
            }
            VoiceTextField("اسم المنطقة", locationName, { locationName = it }, { startVoice { v -> locationName = v } }, "مثال: جليجل بلاطه")
            VoiceTextField("القضاء", qada, { qada = it }, { startVoice { v -> qada = v } }, "مثال: نابلس")
            VoiceTextField("رغبة الفريق الثاني بالشراء", party2WishDescription, { party2WishDescription = it }, { startVoice { v -> party2WishDescription = v } }, "مثال: كامل حصص الفريق الأول الارثية المشاعية...")
            VoiceTextField("رغبة الفريق الأول بالبيع", party1SellDescription, { party1SellDescription = it }, { startVoice { v -> party1SellDescription = v } }, "مثال: كامل حصصه الارثية المشاعية...")

            Spacer(Modifier.height(16.dp))
            Text("شروط البيع", style = MaterialTheme.typography.titleMedium)
            VoiceTextField("الثمن", priceText, { priceText = it }, { startVoice { v -> priceText = v } }, "مثال: ثمانية الاف واربعمائة وثمانون (8480) دينار اردني")
            VoiceTextField("جهة التسجيل", transferOffice, { transferOffice = it }, { startVoice { v -> transferOffice = v } }, "مثال: دائرة تسجيل أراضي حورون")
            VoiceTextField("مبلغ الغرامة", penaltyAmount, { penaltyAmount = it }, { startVoice { v -> penaltyAmount = v } }, "مثال: 10000")
            VoiceTextField("المحكمة المختصة", courtName, { courtName = it }, { startVoice { v -> courtName = v } }, "مثال: نابلس")
            Row {
                Box(Modifier.weight(1f)) {
                    VoiceTextField("عدد الصفحات", pagesCount, { pagesCount = it }, { startVoice { v -> pagesCount = v } }, "مثال: صفحتين")
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    VoiceTextField("عدد النسخ", copiesCount, { copiesCount = it }, { startVoice { v -> copiesCount = v } }, "مثال: ثلاثة (3)")
                }
            }

            OutlinedTextField(
                value = dateText,
                onValueChange = {},
                readOnly = true,
                label = { Text("التاريخ") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.Add, contentDescription = "اختيار التاريخ")
                    }
                }
            )

            Spacer(Modifier.height(16.dp))
            Text("بنود إضافية", style = MaterialTheme.typography.titleMedium)
            extraClauses.forEachIndexed { index, clause ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        VoiceTextField(
                            clauseNumberWord(index + 12),
                            clause,
                            { newVal -> extraClauses[index] = newVal },
                            { startVoice { v -> extraClauses[index] = v } }
                        )
                    }
                    IconButton(onClick = { extraClauses.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف البند")
                    }
                }
            }
            OutlinedButton(
                onClick = { extraClauses.add("") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("إضافة بند")
            }

            Spacer(Modifier.height(16.dp))
            Text("المعاينة", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                val segments = buildAgreementSaleSegments(currentData())
                val annotated = buildAnnotatedString {
                    for (seg in segments) {
                        if (seg.underlined) {
                            withStyle(SpanStyle(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
                                append(seg.text)
                            }
                        } else {
                            append(seg.text)
                        }
                    }
                }
                Text(annotated, modifier = Modifier.padding(12.dp))
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        try {
                            val file = generateAgreementSalePdf(context, currentData())
                            sharePdf(context, file)
                        } catch (e: Exception) {
                            Toast.makeText(context, "خطأ أثناء إنشاء الملف: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("مشاركة PDF") }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        try {
                            val file = generateAgreementSalePdf(context, currentData())
                            saveToDownloads(context, file)
                        } catch (e: Exception) {
                            Toast.makeText(context, "خطأ أثناء إنشاء الملف: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("حفظ في التنزيلات") }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
