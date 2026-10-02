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
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookLevelProgress
import com.example.data.model.ExamType
import com.example.ui.theme.MathAccent
import com.example.ui.theme.MathPrimary
import com.example.ui.theme.MathSuccess
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookProgressionScreen(
    bookProgress: BookLevelProgress,
    onAdvanceLevel: () -> Unit,
    onScanNewBook: () -> Unit,
    onStartAdaptiveTest: (Int) -> Unit,
    onBack: () -> Unit
) {
    val levelTiers = listOf(
        Triple(1, "Aktif Öğrenme Yayınları: 0'dan Matematik", "Temel İşlem Yeteneği, Sayılar & Parantez Kuralları (Kolay)"),
        Triple(2, "Bilgi Sarmal / 345 TYT Matematik", "ÖSYM Sınav Formatında Çok Aşamalı Problemler (Orta)"),
        Triple(3, "Apotemi & Orijinal İleri Matematik", "Yeni Nesil Akıl Yürütme ve Derece Soruları (İleri/Zor)")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gelişime Göre Zorlaşan Seviye Motoru", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Aktif Yayınları & Materyal İlerleme Sistemi", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("book_progression_back_button")) {
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
                .testTag("book_progression_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Current Active Book Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("active_book_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = RoundedCornerShape(8.dp), color = MathAccent.copy(alpha = 0.2f)) {
                                Text(
                                    text = "SEVİYE ${bookProgress.currentLevel} AKTİF",
                                    color = MathAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text("Öğrenci Adaptasyon Modu", color = Color(0xFF38BDF8), fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = bookProgress.bookName,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Öğrencinin çözdüğü soruların doğruluk oranı yükseldikçe sistem soruları otomatik zorlaştırır.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val progressFraction = bookProgress.completedPages.toFloat() / bookProgress.totalPages.toFloat()
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = MathAccent,
                            trackColor = Color(0xFF334155)
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tamamlanan: ${bookProgress.completedPages}/${bookProgress.totalPages} Sayfa", color = Color.Gray, fontSize = 11.sp)
                            Text("%${(progressFraction * 100).toInt()}", color = MathAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onStartAdaptiveTest(bookProgress.currentLevel) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f).testTag("start_adaptive_level_test_button")
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Seviye Sınavı Çöz", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = onScanNewBook,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("scan_new_book_button")
                            ) {
                                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Yeni Kitap Tara", fontSize = 11.sp, color = Color(0xFF38BDF8))
                            }
                        }
                    }
                }
            }

            // Progression Tiers Timeline
            item {
                Text(
                    text = "Gelişime Göre Kitap & Zorluk Kademeleri",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(levelTiers.size) { idx ->
                val (lvl, title, desc) = levelTiers[idx]
                val isCurrent = lvl == bookProgress.currentLevel
                val isCompleted = lvl < bookProgress.currentLevel

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = when {
                                isCompleted -> MathSuccess
                                isCurrent -> MaterialTheme.colorScheme.primary
                                else -> Color(0xFFE2E8F0)
                            },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when {
                                        isCompleted -> Icons.Default.CheckCircle
                                        isCurrent -> Icons.Default.TrendingUp
                                        else -> Icons.Default.Lock
                                    },
                                    contentDescription = null,
                                    tint = if (isCompleted || isCurrent) Color.White else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Kademe $lvl: $title",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else Color.Gray
                            )
                        }

                        if (isCompleted || isCurrent) {
                            IconButton(onClick = { onStartAdaptiveTest(lvl) }) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Başla", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // Next Recommended Book Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate800)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MathAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bir Sonraki Seviye Kitap Önerisi:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = bookProgress.nextRecommendedBook,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MathAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aktif Yayınları seviyesi tamamlandığında elinizdeki bu yeni kitabı taratarak kütüphanenizi genişletip soruların zorluğunu 2. seviyeye yükseltebilirsiniz.",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onAdvanceLevel,
                            colors = ButtonDefaults.buttonColors(containerColor = MathSuccess),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("advance_level_button")
                        ) {
                            Text("Seviye Atla & Yeni Kitap Senkronize Et", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
