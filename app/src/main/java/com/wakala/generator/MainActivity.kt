package com.wakala.generator

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CompositionLocalProvider
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WakalaTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        WakalaApp()
                    }
                }
            }
        }
    }
}

@Composable
fun WakalaTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = Color(0xFF8A1F2D),
        secondary = Color(0xFF8A1F2D)
    )
    MaterialTheme(colorScheme = colors, content = content)
}

private fun buildAnnotatedPreview(segments: List<TextSegment>): AnnotatedString = buildAnnotatedString {
    for (segment in segments) {
        if (segment.underlined) {
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append(segment.text) }
        } else {
            append(segment.text)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakalaApp() {
    val context = LocalContext.current

    var name1 by rememberSaveable { mutableStateOf("") }
    var id1 by rememberSaveable { mutableStateOf("") }
    var hasSecond by rememberSaveable { mutableStateOf(false) }
    var name2 by rememberSaveable { mutableStateOf("") }
    var id2 by rememberSaveable { mutableStateOf("") }
    var hawz by rememberSaveable { mutableStateOf("") }
    var hawzLocation by rememberSaveable { mutableStateOf("") }
    var qitaa by rememberSaveable { mutableStateOf("") }
    var village by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("حورون و/أو قدوم و/أو سالم و/أو بيت ايل") }
    var qada by rememberSaveable { mutableStateOf("") }
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
            val file = generateWakalaPdf(context, currentData(name1, id1, hasSecond, name2, id2, hawz, hawzLocation, qitaa, village, location, qada, dateText))
            val saved = saveToDownloads(context, file)
            Toast.makeText(context, if (saved) "تم الحفظ في مجلد التنزيلات" else "تعذر الحفظ", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "يلزم إذن التخزين للحفظ على هذا الإصدار من أندرويد", Toast.LENGTH_SHORT).show()
        }
    }

    val data = currentData(name1, id1, hasSecond, name2, id2, hawz, hawzLocation, qitaa, village, location, qada, dateText)
    val segments = remember(data) { buildWakalaSegments(data) }
    val previewText = remember(segments) { buildAnnotatedPreview(segments) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("مولّد وكالة خصوصية") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            VoiceTextField(
                label = "اسم الموكل الرباعي (الأول)",
                value = name1,
                onValueChange = { name1 = it },
                onVoiceClick = { startVoice { v -> name1 = v } },
                placeholder = "مثال: مصطفى علي مصطفى سلامة"
            )
            Spacer(Modifier.height(8.dp))
            VoiceTextField(
                label = "رقم هوية الموكل الأول",
                value = id1,
                onValueChange = { id1 = it },
                onVoiceClick = { startVoice { v -> id1 = v } },
                placeholder = "مثال: 850455890"
            )

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = hasSecond, onCheckedChange = { hasSecond = it })
                Text("إضافة موكل ثانٍ إلى نفس الوكالة")
            }

            if (hasSecond) {
                Spacer(Modifier.height(8.dp))
                VoiceTextField(
                    label = "اسم الموكل الثاني الرباعي",
                    value = name2,
                    onValueChange = { name2 = it },
                    onVoiceClick = { startVoice { v -> name2 = v } }
                )
                Spacer(Modifier.height(8.dp))
                VoiceTextField(
                    label = "رقم هوية الموكل الثاني",
                    value = id2,
                    onValueChange = { id2 = it },
                    onVoiceClick = { startVoice { v -> id2 = v } }
                )
            }

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                VoiceTextField(
                    label = "رقم الحوض",
                    value = hawz,
                    onValueChange = { hawz = it },
                    onVoiceClick = { startVoice { v -> hawz = v } },
                    modifier = Modifier.weight(1f)
                )
                VoiceTextField(
                    label = "رقم القطعة",
                    value = qitaa,
                    onValueChange = { qitaa = it },
                    onVoiceClick = { startVoice { v -> qitaa = v } },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            VoiceTextField(
                label = "اسم الموقع",
                value = hawzLocation,
                onValueChange = { hawzLocation = it },
                onVoiceClick = { startVoice { v -> hawzLocation = v } }
            )
            Spacer(Modifier.height(8.dp))
            VoiceTextField(
                label = "اسم البلد (أراضي ...)",
                value = village,
                onValueChange = { village = it },
                onVoiceClick = { startVoice { v -> village = v } },
                placeholder = "مثال: بيت دجن"
            )
            Spacer(Modifier.height(8.dp))
            VoiceTextField(
                label = "اسم الدائرة",
                value = location,
                onValueChange = { location = it },
                onVoiceClick = { startVoice { v -> location = v } }
            )
            Spacer(Modifier.height(8.dp))
            VoiceTextField(
                label = "اسم القضاء",
                value = qada,
                onValueChange = { qada = it },
                onVoiceClick = { startVoice { v -> qada = v } },
                placeholder = "مثال: نابلس"
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
                        "وكالة خصوصية",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Text(
                        "مكتب المحامي عبدالله دويكات - 0594333138",
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
                        val file = generateWakalaPdf(context, data)
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
                            val file = generateWakalaPdf(context, data)
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
                    name1 = ""; id1 = ""; hasSecond = false; name2 = ""; id2 = ""
                    hawz = ""; hawzLocation = ""; qitaa = ""; village = ""
                    location = "حورون و/أو قدوم و/أو سالم و/أو بيت ايل"
                    qada = ""; dateText = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تفريغ الحقول")
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "التطبيق يعمل بالكامل دون إنترنت لتوليد الوكالة وحفظها ومشاركتها. الإدخال الصوتي قد " +
                    "يحتاج إنترنت حسب دعم جهازك للتعرف على الصوت دون اتصال.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun currentData(
    name1: String, id1: String, hasSecond: Boolean, name2: String, id2: String,
    hawz: String, hawzLocation: String, qitaa: String, village: String, location: String, qada: String, dateText: String
) = WakalaData(name1, id1, hasSecond, name2, id2, hawz, hawzLocation, qitaa, village, location, qada, dateText)

@Composable
fun VoiceTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onVoiceClick: () -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
        trailingIcon = {
            IconButton(onClick = onVoiceClick) {
                Icon(Icons.Default.Mic, contentDescription = "إدخال صوتي")
            }
        },
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}
