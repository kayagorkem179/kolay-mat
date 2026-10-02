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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeacherHomework
import com.example.ui.theme.MathAccent
import com.example.ui.theme.MathPrimary
import com.example.ui.theme.MathSuccess
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherPortalScreen(
    homeworks: List<TeacherHomework>,
    onAddHomework: (String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var showUploadDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newSubject by remember { mutableStateOf("YKS Matematik") }
    var newFileName by remember { mutableStateOf("Odev_Foyu_Matematik_2026.pdf") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Öğretmen Yönetim & Takip Portalı", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("PDF Ödev / Föy Yükleme & Öğrenci Kontrolü", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("teacher_back_button")) {
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
                .testTag("teacher_portal_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Student Inspection Card (Görkem Kaya)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("student_inspection_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Öğrenci: Görkem Kaya", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("12-A / YKS Hazırlık Grubu", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                }
                            }
                            Surface(shape = RoundedCornerShape(8.dp), color = MathSuccess.copy(alpha = 0.2f)) {
                                Text("Aktif Çalışıyor", color = MathSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid for Teacher
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF1E293B), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Bu Hafta", fontSize = 11.sp, color = Color.Gray)
                                    Text("210 Soru", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF1E293B), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TYT Net", fontSize = 11.sp, color = Color.Gray)
                                    Text("26.5 Net", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MathAccent)
                                }
                            }
                            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF1E293B), modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Ödev Durumu", fontSize = 11.sp, color = Color.Gray)
                                    Text("%85 Tamam", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MathSuccess)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Öğretmen Gözlemi: Görkem temel seviyeyi (Aktif Matematik) başarıyla tamamladı. Problemlerdeki işlem hızı %20 arttı.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // PDF Upload Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Öğrencilere Verilen PDF Ödev ve Föyler", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { showUploadDialog = !showUploadDialog },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("upload_pdf_toggle_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF Ödev Yükle", fontSize = 11.sp)
                    }
                }
            }

            // Inline Upload Form
            if (showUploadDialog) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("upload_pdf_form_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Yeni PDF Ödev / Konu Anlatımı Yükleme", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newTitle,
                                onValueChange = { newTitle = it },
                                label = { Text("Ödev Başlığı (Örn: Trigonometri Föyü)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newFileName,
                                onValueChange = { newFileName = it },
                                label = { Text("PDF Dosya Adı") },
                                leadingIcon = { Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFEF4444)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (newTitle.isNotBlank()) {
                                        onAddHomework(newTitle, "Ahmet Hoca", newSubject, newFileName)
                                        newTitle = ""
                                        showUploadDialog = false
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("submit_pdf_upload_button")
                            ) {
                                Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sisteme Yükle ve Öğrencilere Gönder")
                            }
                        }
                    }
                }
            }

            // List of Homeworks
            items(homeworks) { hw ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(hw.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${hw.fileName} • ${hw.targetGroup}", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Teslim: ${hw.deadline} • ${hw.totalQuestions} Soru", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (hw.isSubmitted) MathSuccess.copy(alpha = 0.15f) else MathAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (hw.isSubmitted) "Teslim Edildi" else "Bekliyor",
                                color = if (hw.isSubmitted) MathSuccess else MathAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
