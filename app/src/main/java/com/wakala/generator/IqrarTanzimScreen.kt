package com.wakala.generator

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.util.Calendar

private fun buildIqrarAnnotatedPreview(segments: List<TextSegment>): AnnotatedString = buildAnnotatedString {
    for (segment in segments) {
        if (segment.underlined) {
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append(segment.text) }
        } else {
            append(segment.text)
        }
    }
}

private fun currentIqrarData(
    applicantName: String, idNumber: String, qitaa: String, hawz: String,
    village: String, dateText: String
) = IqrarTanzimData(applicantName, idNumber, qitaa, hawz, village, dateText)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IqrarTanzimScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current

    var applicantName by rememberSaveable { mutableStateOf("") }
    var idNumber by rememberSaveable { mutableStateOf("") }
    var qitaa by rememberSaveable { mutableStateOf("") }
    var hawz by rememberSaveable { mutableStateOf("") }
    var village by rememberSaveable { mutableStateOf("") }
    var dateText by rememberSaveable { mutableStateOf("") }

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

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = generateIqrarTanzimPdf(context, currentIqrarData(applicantName, idNumber, qitaa, hawz, village, dateText))
            val saved = saveToDownloads(context, file)
            Toast.makeText(context, if (saved) "تم الحفظ في مجلد التنزيلات" else "تعذر الحفظ", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "يلزم إذن التخزين للحفظ على هذا الإصدار من أندرويد", Toast.LENGTH_SHORT).show()
        }
    }

    val data = currentIqrarData(applicantName, idNumber, qitaa, hawz, village, dateText)
    val segments = remember(data) { buildIqrarTanzimSegments(data) }
    val previewText = remember(segments) { buildIqrarAnnotatedPreview(segments) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إقرار للتنظيم") },
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            VoiceTextField(
                label = "اسم مقدّم الإقرار",
                value = applicantName,
                onValueChange = { applicantName = it },
                onVoiceClick = { startVoice { v -> applicantName = v } }
            )
            Spacer(Modifier.height(8.dp))
            VoiceTextField(
                label = "رقم الهوية",
                value = idNumber,
                onValueChange = { idNumber = it },
                onVoiceClick = { startVoice { v -> idNumber = v } }
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                VoiceTextField(
                    label = "رقم القطعة",
                    value = qitaa,
                    onValueChange = { qitaa = it },
                    onVoiceClick = { startVoice { v -> qitaa = v } },
                    modifier = Modifier.weight(1f)
                )
                VoiceTextField(
                    label = "رقم الحوض",
                    value = hawz,
                    onValueChange = { hawz = it },
                    onVoiceClick = { startVoice { v -> hawz = v } },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            VoiceTextField(
                label = "اسم القرية",
                value = village,
                onValueChange = { village = it },
                onVoiceClick = { startVoice { v -> village = v } }
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = dateText,
                onValueChange = {},
                readOnly = true,
                label = { Text("التاريخ") },
                trailingIcon = {
                    IconButton(onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                dateText = String.format("%04d/%02d/%02d", year, month + 1, day)
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "اختيار التاريخ")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "إقرار",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Text(
                        "دائرة التنظيم المركزية",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(previewText, textAlign = TextAlign.Justify, color = Color.Black)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val file = generateIqrarTanzimPdf(context, data)
                        sharePdf(context, file)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("مشاركة PDF")
                }
                OutlinedButton(
                    onClick = {
                        val needsRuntimePermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED
                        if (needsRuntimePermission) {
                            storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            val file = generateIqrarTanzimPdf(context, data)
                            val saved = saveToDownloads(context, file)
                            Toast.makeText(
                                context,
                                if (saved) "تم الحفظ في مجلد التنزيلات" else "تعذر الحفظ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("حفظ PDF")
                }
            }

            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = {
                    applicantName = ""; idNumber = ""; qitaa = ""; hawz = ""
                    village = ""; dateText = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تفريغ الحقول")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
