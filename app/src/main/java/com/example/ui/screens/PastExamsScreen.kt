package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.components.ScratchpadCard
import com.example.ui.theme.MathAccent
import com.example.ui.theme.MathPrimary
import com.example.ui.theme.MathSuccess
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.TtsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastExamsScreen(
    questions: List<MathQuestion>,
    ttsManager: TtsManager,
    onStartPastExamTrial: () -> Unit,
    onSaveToLibrary: (MathQuestion) -> Unit,
    onBack: () -> Unit
) {
    var selectedYear by remember { mutableStateOf("Tümü") } // Tümü, 2024, 2023
    val openScratchpads = remember { mutableStateMapOf<Int, Boolean>() }
    val openSolutions = remember { mutableStateMapOf<Int, Boolean>() }

    val filteredList = questions.filter { q ->
        if (selectedYear == "Tümü") true
        else q.pastExamYear?.toString() == selectedYear
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ÖSYM & MEB Çıkmış Sorular Arşivi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("2021 - 2024 Gerçek Sınav Soruları ve Çözümleri", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("past_exams_back_button")) {
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
                .testTag("past_exams_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Trial Launcher Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("past_exam_trial_banner"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.HistoryEdu, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("20 Soruluk Çıkmış Sorular Denemesi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("ÖSYM ve MEB resmi çıkmış sorularından karma sınav", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onStartPastExamTrial,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                            modifier = Modifier.fillMaxWidth().testTag("start_past_exam_trial_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Çıkmış Sorular Denemesini Başlat (20 Soru)", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Filter Chips (Yıllara göre filtre)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sınav Yılı:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    listOf("Tümü", "2024", "2023").forEach { year ->
                        val isSelected = selectedYear == year
                        AssistChip(
                            onClick = { selectedYear = year },
                            label = { Text(year, fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.testTag("filter_year_$year")
                        )
                    }
                }
            }

            // Question List
            items(filteredList) { question ->
                val isScratchpadOpen = openScratchpads[question.id] == true
                val isSolutionOpen = openSolutions[question.id] == true

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("past_question_card_${question.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Badge row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF38BDF8).copy(alpha = 0.15f)) {
                                    Text(
                                        text = question.pastExamLabel ?: "ÖSYM Çıkmış Soru",
                                        color = Color(0xFF0284C7),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(question.topic, fontSize = 11.sp, color = Color.Gray)
                            }

                            Row {
                                IconButton(onClick = { ttsManager.speak("Çıkmış Soru: ${question.questionText}") }, modifier = Modifier.size(30.dp)) {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Dinle", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { onSaveToLibrary(question) }, modifier = Modifier.size(30.dp)) {
                                    Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = "Kaydet", modifier = Modifier.size(18.dp), tint = MathAccent)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = question.questionText,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions: Scratchpad & Show Solution
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(
                                onClick = { openScratchpads[question.id] = !isScratchpadOpen },
                                label = { Text(if (isScratchpadOpen) "Tahtayı Kapat" else "Karalama Tahtası", fontSize = 11.sp) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Create, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            )

                            AssistChip(
                                onClick = { openSolutions[question.id] = !isSolutionOpen },
                                label = { Text(if (isSolutionOpen) "Çözümü Gizle" else "ÖSYM Çözümünü Gör", fontSize = 11.sp) },
                                leadingIcon = { Icon(imageVector = Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp), tint = MathSuccess) }
                            )
                        }

                        if (isScratchpadOpen) {
                            Spacer(modifier = Modifier.height(10.dp))
                            ScratchpadCard(
                                title = "Çıkmış Soru ${question.id} İçin Karalama",
                                initialHeightDp = 200,
                                onClose = { openScratchpads[question.id] = false }
                            )
                        }

                        if (isSolutionOpen) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Slate900
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("ÖSYM Resmi Çözüm Analizi:", color = MathSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(question.explanation, color = Color(0xFFCBD5E1), fontSize = 12.sp, lineHeight = 18.sp)
                                    if (question.teacherTip.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("💡 Püf Nokta: ${question.teacherTip}", color = Color(0xFFFBBF24), fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
