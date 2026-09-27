package com.wakala.generator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgreementSaleScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current

    var party1Name by remember { mutableStateOf("أحمد محمد يوسف الأحمد") }
    var party1Id by remember { mutableStateOf("900112233") }
    var party1Residence by remember { mutableStateOf("نابلس") }

    var party2Name by remember { mutableStateOf("خالد سالم عبدالله الخطيب") }
    var party2Id by remember { mutableStateOf("900445566") }
    var party2Residence by remember { mutableStateOf("نابلس") }

    var ownershipDescription by remember { mutableStateOf("حصص ارثية مشاعية في قطعتي") }
    var qitaaNumbers by remember { mutableStateOf("52 و58") }
    var hawzNumber by remember { mutableStateOf("12") }
    var locationName by remember { mutableStateOf("جليجل بلاطه") }
    var qada by remember { mutableStateOf("نابلس") }

    var party2WishDescription by remember {
        mutableStateOf("كامل حصص الفريق الأول الارثية المشاعية و/او الاصلية المشاعية بالغا مابلغت في قطعتي")
    }
    var party1SellDescription by remember {
        mutableStateOf("كامل حصصه الارثية المشاعية و/او الاصلية المشاعية بالغا مابلغت في قطعتي")
    }

    var priceText by remember { mutableStateOf("ثمانية الاف واربعمائة وثمانون (8480) دينار اردني") }
    var transferOffice by remember { mutableStateOf("دائرة تسجيل أراضي حورون و/او كاتب عدل نابلس و/او كاتب عدل القدس") }
    var penaltyAmount by remember { mutableStateOf("10000") }
    var courtName by remember { mutableStateOf("نابلس") }
    var pagesCount by remember { mutableStateOf("صفحتين") }
    var copiesCount by remember { mutableStateOf("ثلاثة (3)") }
    var dateText by remember { mutableStateOf("") }

    val extraClauses = remember { mutableStateListOf<String>() }

    fun currentData() = AgreementSaleData(
        party1Name = party1Name, party1Id = party1Id, party1Residence = party1Residence,
        party2Name = party2Name, party2Id = party2Id, party2Residence = party2Residence,
        ownershipDescription = ownershipDescription,
        qitaaNumbers = qitaaNumbers, hawzNumber = hawzNumber,
        locationName = locationName, qada = qada,
        party2WishDescription = party2WishDescription,
        party1SellDescription = party1SellDescription,
        priceText = priceText, transferOffice = transferOffice,
        penaltyAmount = penaltyAmount, courtName = courtName,
        pagesCount = pagesCount, copiesCount = copiesCount,
        dateText = dateText, extraClauses = extraClauses.toList()
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
            VoiceTextField("اسم الفريق الأول", party1Name) { party1Name = it }
            VoiceTextField("رقم هوية الفريق الأول", party1Id) { party1Id = it }
            VoiceTextField("مكان سكن الفريق الأول", party1Residence) { party1Residence = it }

            Spacer(Modifier.height(16.dp))
            Text("الفريق الثاني", style = MaterialTheme.typography.titleMedium)
            VoiceTextField("اسم الفريق الثاني", party2Name) { party2Name = it }
            VoiceTextField("رقم هوية الفريق الثاني", party2Id) { party2Id = it }
            VoiceTextField("مكان سكن الفريق الثاني", party2Residence) { party2Residence = it }

            Spacer(Modifier.height(16.dp))
            Text("بيانات الأرض", style = MaterialTheme.typography.titleMedium)
            VoiceTextField("وصف ملكية الفريق الأول (يملك ويتصرف في...)", ownershipDescription) { ownershipDescription = it }
            Row {
                Box(Modifier.weight(1f)) {
                    VoiceTextField("رقم القطعة", qitaaNumbers) { qitaaNumbers = it }
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    VoiceTextField("رقم الحوض", hawzNumber) { hawzNumber = it }
                }
            }
            VoiceTextField("اسم المنطقة", locationName) { locationName = it }
            VoiceTextField("القضاء", qada) { qada = it }
            VoiceTextField("رغبة الفريق الثاني بالشراء (كامل حصص...)", party2WishDescription) { party2WishDescription = it }
            VoiceTextField("رغبة الفريق الأول بالبيع (كامل حصصه...)", party1SellDescription) { party1SellDescription = it }

            Spacer(Modifier.height(16.dp))
            Text("شروط البيع", style = MaterialTheme.typography.titleMedium)
            VoiceTextField("الثمن", priceText) { priceText = it }
            VoiceTextField("جهة التسجيل", transferOffice) { transferOffice = it }
            VoiceTextField("مبلغ الغرامة", penaltyAmount) { penaltyAmount = it }
            VoiceTextField("المحكمة المختصة", courtName) { courtName = it }
            Row {
                Box(Modifier.weight(1f)) {
                    VoiceTextField("عدد الصفحات", pagesCount) { pagesCount = it }
                }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    VoiceTextField("عدد النسخ", copiesCount) { copiesCount = it }
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
                            "${clauseNumberWord(index + 12)}",
                            clause
                        ) { newVal -> extraClauses[index] = newVal }
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
                        val file = generateAgreementSalePdf(context, currentData())
                        sharePdf(context, file)
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("مشاركة PDF") }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        val file = generateAgreementSalePdf(context, currentData())
                        saveToDownloads(context, file, "اتفاقية-بيع.pdf")
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("حفظ في التنزيلات") }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
