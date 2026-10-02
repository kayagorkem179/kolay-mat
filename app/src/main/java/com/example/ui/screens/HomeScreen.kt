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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamType
import com.example.data.model.UserProfile
import com.example.ui.theme.MathAccent
import com.example.ui.theme.MathPrimary
import com.example.ui.theme.MathSecondary
import com.example.ui.theme.MathSuccess
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    unsolvedCount: Int,
    onStartExam: (ExamType) -> Unit,
    onOpenScratchpad: () -> Unit,
    onOpenScanner: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenVideos: () -> Unit,
    onOpenPlanner: () -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenClassicYazili: () -> Unit,
    onOpenPastExams: () -> Unit = {},
    onOpenBookProgression: () -> Unit = {},
    onOpenTeacherPortal: () -> Unit = {},
    onOpenParentPortal: () -> Unit = {},
    onOpenHelpSupport: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. HERO BANNER: Welcome, MEB Sync & Security Shield Status
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Slate900
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // MEB / ÖSYM Müfredat Senkronizasyonu Rozeti
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0x3310B981)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MathSuccess,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "2026-2027 MEB & ÖSYM Müfredat Senkronize",
                                        fontSize = 11.sp,
                                        color = MathSuccess,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Security Shield
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0x3338BDF8),
                                modifier = Modifier.clickable { onOpenSecurity() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Kalkan Aktif",
                                        fontSize = 11.sp,
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Merhaba, ${userProfile.name} 👋",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hedef: ${userProfile.gradeOrTarget} • Bugün ${userProfile.studyGoalDailyMinutes} dk hedeflendi",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatMiniBadge(
                                label = "Günlük Seri",
                                value = "🔥 ${userProfile.currentStreakDays} Gün",
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniBadge(
                                label = "Çözülen Soru",
                                value = "✅ ${userProfile.totalQuestionsSolved}",
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniBadge(
                                label = "Hata Havuzu",
                                value = "📌 $unsolvedCount Soru",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenLibrary() }
                            )
                        }
                    }
                }
            }
        }

        // 2. QUICK ACTION TOOLBAR (Karalama Tahtası, Kitap Tarama, Soru Kütüphanesi, AI Plan)
        item {
            Text(
                text = "Hızlı Araçlar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickToolButton(
                    title = "Karalama\nTahtası",
                    icon = Icons.Default.Create,
                    tint = MathAccent,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenScratchpad
                )
                QuickToolButton(
                    title = "Kitap & Soru\nTarayıcı",
                    icon = Icons.Default.QrCodeScanner,
                    tint = MathSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenScanner
                )
                QuickToolButton(
                    title = "Hata & Çözüm\nKütüphanesi",
                    icon = Icons.Default.MenuBook,
                    tint = Color(0xFFA855F7),
                    badgeCount = unsolvedCount,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenLibrary
                )
                QuickToolButton(
                    title = "Yapay Zeka\nPlanlayıcı",
                    icon = Icons.Default.CalendarMonth,
                    tint = MathSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenPlanner
                )
            }
        }

        // 3. FEATURED: 20-QUESTION TIMED PRACTICE EXAM
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("featured_trial_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GÜNÜN 20 SORULUK DENEMESİ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "TYT Matematik Tam Deneme Sınavı",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "20 Soru • 40 Dakika • Süreli Geri Sayım • Sesli Anlatım & AI Analiz",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onStartExam(ExamType.TYT) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("start_tyt_exam_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Süreli Denemeyi Başlat")
                        }
                    }
                }
            }
        }

        // 4. MEB OKUL YAZILISI KLASİK SORULAR KARTI
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenClassicYazili() }
                    .testTag("classic_yazili_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate800)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MEB Okul Yazılıları Klasik Sınav Modu",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Açık uçlu, adımlı MEB puanlama rubriğine uygun klasik sorular.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Başla",
                        tint = Color(0xFF38BDF8)
                    )
                }
            }
        }

        // ÖSYM & MEB ÇIKMIŞ SORULAR KARTI
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPastExams() }
                    .testTag("past_exams_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.HistoryEdu, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("ÖSYM & MEB Çıkmış Sınav Soruları", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("2021-2024 TYT, AYT, LGS ve KPSS gerçek sınav soruları ve çözümleri.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF38BDF8))
                }
            }
        }

        // AKTİF YAYINLARI & GELİŞİME GÖRE ZORLAŞAN SEVİYE KARTI
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenBookProgression() }
                    .testTag("book_progression_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MathAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = MathAccent, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Gelişime Göre Zorlaşan Seviye Motoru", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Aktif Matematik (Seviye 1) bittikçe yeni kitap tara, seviyen artsın.", fontSize = 12.sp, color = Color.Gray)
                    }
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = MathAccent)
                }
            }
        }

        // ÖĞRETMEN & VELİ PORTAL SEÇİM ŞERİDİ
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Öğretmen Portalı Butonu
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenTeacherPortal() }
                        .testTag("teacher_portal_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Öğretmen Portalı", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("PDF Ödev & Öğrenci Takibi", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                // Veli Portalı Butonu
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenParentPortal() }
                        .testTag("parent_portal_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0EA5E9).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.FamilyRestroom, contentDescription = null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Veli Portalı", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Sadece Öğrenci Durumu", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        // GELİŞTİRİCİ DESTEK BİLGİSİ (SADECE KAYITLI ÖĞRENCİYE ÖZEL)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenHelpSupport() }
                    .testTag("help_support_banner"),
                shape = RoundedCornerShape(14.dp),
                color = Slate800
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Geliştirici & Öğretmen Destek Hattı", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Görkem Kaya • Sadece Kayıtlı Öğrencilere Özel İletişim", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                    Text("İncele", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 5. ALL EXAM CATEGORIES (LGS, TYT, AYT, KPSS, DGS, ALES)
        item {
            Text(
                text = "Tüm Sınavlar ve 20 Soruluk Denemeler",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Müfredata uygun en az 20 soru içeren tam test ve denemeler",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        items(ExamType.values().filter { it != ExamType.OKUL_YAZILISI }) { exam ->
            ExamTypeRowItem(
                examType = exam,
                onStart = { onStartExam(exam) }
            )
        }

        // 6. VIDEO LESSONS & TEACHER TIPS SHORTCUT
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenVideos() }
                    .testTag("videos_banner_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFBBF24).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = MathAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Konu Anlatım Videoları & Formül Notları",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Öğretmen destekli, püf noktalar ve sesli ders özetleri.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatMiniBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF94A3B8))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun QuickToolButton(
    title: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("quick_tool_${title.replace("\n", "_").lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                    }
                    if (badgeCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$badgeCount", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ExamTypeRowItem(
    examType: ExamType,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStart() }
            .testTag("exam_item_${examType.name}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = examType.shortName,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = examType.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = examType.badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "${examType.defaultQuestionCount} Soru • ${examType.durationMinutes} Dk • ${examType.description}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            IconButton(onClick = onStart) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Başlat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
