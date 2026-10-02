package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamType
import com.example.data.model.MathQuestion
import com.example.data.remote.GeminiService
import com.example.ui.theme.MathAccent
import com.example.ui.theme.MathPrimary
import com.example.ui.theme.MathSuccess
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.TtsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    ttsManager: TtsManager,
    onSaveToLibrary: (MathQuestion) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var scannedText by remember {
        mutableStateOf("Bir kenar uzunluğu 12 cm olan eşkenar üçgenin içine çizilebilecek en büyük dairenin alanı kaç cm² dir? (π = 3 alınız)")
    }
    var aiSolutionResult by remember { mutableStateOf<String?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var isSavedToLibrary by remember { mutableStateOf(false) }

    val sampleBookQuestions = listOf(
        "LGS Soru Bankası Sayfa 64: Boyutları 180 m ve 240 m olan tarla kare parsellere bölünecektir. En az kaç parsel elde edilir?",
        "TYT Matematik Sayfa 112: Bir manav elindeki elmaların önce %30'unu, sonra kalanın %40'ını satıyor. Geriye 84 kg kaldığına göre başlangıçta kaç kg elma vardı?",
        "AYT Matematik Sayfa 205: f(x) = 2x³ - 6x + 7 fonksiyonunun x = 1 noktasındaki teğetinin eğimi kaçtır?"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Kitap & Materyal Tarayıcı", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Optik Soru Algılama & Yapay Zeka Çözücü", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("scanner_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("scanner_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Scanner Viewport Mock
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("scanner_viewport_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scanner targeting frame
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        // Instruction text
                        Text(
                            text = "Matematik kitabındaki soruyu kameraya veya çerçeveye hizalayın",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 10.dp)
                        )
                    }
                }
            }

            // Quick Book Samples
            item {
                Text(
                    text = "Veya Kitaptan Hazır Bir Sayfa Seç:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    sampleBookQuestions.forEachIndexed { idx, sample ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scannedText = sample
                                    aiSolutionResult = null
                                    isSavedToLibrary = false
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(sample, fontSize = 11.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // Scanned Text Field
            item {
                Text(
                    text = "Taranan Soru Metni & Formülü:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = scannedText,
                    onValueChange = { scannedText = it },
                    modifier = Modifier.fillMaxWidth().testTag("scanned_text_field"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
            }

            // Solve Button
            item {
                Button(
                    onClick = {
                        isAnalyzing = true
                        isSavedToLibrary = false
                        coroutineScope.launch {
                            aiSolutionResult = GeminiService.solveMathQuestion(scannedText)
                            isAnalyzing = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("ai_solve_scanned_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isAnalyzing && scannedText.isNotBlank()
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yapay Zeka Soruyu Çözüyor...")
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFBBF24))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yapay Zeka ile Adım Adım Çöz")
                    }
                }
            }

            // AI Solution Output
            aiSolutionResult?.let { solution ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("ai_solution_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate900)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Yapay Zeka Matematik Çözümü", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Row {
                                    // TTS
                                    IconButton(
                                        onClick = { ttsManager.speak(solution) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Seslendir", tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                    }

                                    // Save to Unsolved Library
                                    IconButton(
                                        onClick = {
                                            val newQ = MathQuestion(
                                                id = (System.currentTimeMillis() % 10000).toInt(),
                                                examType = ExamType.TYT,
                                                topic = "Taranan Kitap Sorusu",
                                                questionText = scannedText,
                                                explanation = solution
                                            )
                                            onSaveToLibrary(newQ)
                                            isSavedToLibrary = true
                                        },
                                        modifier = Modifier.size(32.dp).testTag("save_scanned_to_library_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isSavedToLibrary) Icons.Default.Check else Icons.Default.BookmarkAdd,
                                            contentDescription = "Kütüphaneye Ekle",
                                            tint = if (isSavedToLibrary) MathSuccess else MathAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = solution,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }
    }
}
