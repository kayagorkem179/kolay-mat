package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import com.example.data.local.SavedQuestionEntity
import com.example.ui.components.ScratchpadCard
import com.example.ui.theme.MathAccent
import com.example.ui.theme.MathError
import com.example.ui.theme.MathPrimary
import com.example.ui.theme.MathSuccess
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.TtsManager
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionLibraryScreen(
    savedQuestions: List<SavedQuestionEntity>,
    ttsManager: TtsManager,
    onDeleteQuestion: (Int) -> Unit,
    onToggleSolved: (Int, Boolean) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tümü") } // Tümü, Çözülecekler, Çözülenler
    val openScratchpadQuestionId = remember { mutableStateMapOf<Int, Boolean>() }
    val openAiExplanationQuestionId = remember { mutableStateMapOf<Int, Boolean>() }

    val filteredList = savedQuestions.filter { q ->
        val matchesQuery = q.questionText.contains(searchQuery, ignoreCase = true) ||
                q.topic.contains(searchQuery, ignoreCase = true) ||
                q.examType.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Çözülecekler" -> !q.isSolved
            "Çözülenler" -> q.isSolved
            else -> true
        }

        matchesQuery && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Çözülemeyenler & Hata Kütüphanesi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${savedQuestions.size} Kayıtlı Soru • Çevrimdışı Depolama", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("library_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("question_library_screen")
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Konularda veya sorularda ara...", fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_field"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Tümü", "Çözülecekler", "Çözülenler").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    AssistChip(
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("filter_$filter")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (savedQuestions.isEmpty()) "Henüz Kayıtlı Soru Yok" else "Aramaya Uygun Soru Bulunamadı",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Testlerde yanlış yaptığınız veya yıldızladığınız sorular burada toplanır ve tekrar çözülebilir.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredList, key = { it.id }) { question ->
                        val optionsList = remember(question.optionsJson) {
                            try {
                                val jsonArr = JSONArray(question.optionsJson)
                                (0 until jsonArr.length()).map { jsonArr.getString(it) }
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }
                        val isScratchpadOpen = openScratchpadQuestionId[question.id] == true
                        val isAiOpen = openAiExplanationQuestionId[question.id] == true

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("saved_question_card_${question.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (question.isSolved) Slate800.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Card Header: Exam Badge + Topic + Action Icons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = question.examType,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = question.topic,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Solved Status Toggle Checkbox
                                        IconButton(
                                            onClick = { onToggleSolved(question.id, !question.isSolved) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (question.isSolved) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = "Çözüldü",
                                                tint = if (question.isSolved) MathSuccess else Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Delete from library
                                        IconButton(
                                            onClick = { onDeleteQuestion(question.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Sil",
                                                tint = MathError.copy(alpha = 0.8f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Question Text
                                Text(
                                    text = question.questionText,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                question.mathFormula?.let { formula ->
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(6.dp),
                                        color = Slate900
                                    ) {
                                        Text(
                                            text = formula,
                                            color = Color(0xFFFBBF24),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }

                                // If user chose a wrong option during test
                                if (question.userSelectedWrongIndex >= 0 && question.userSelectedWrongIndex < optionsList.size) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        color = MathError.copy(alpha = 0.1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Senin Seçimin: ${('A' + question.userSelectedWrongIndex)}) ${optionsList[question.userSelectedWrongIndex]} (Hatalı)",
                                                fontSize = 11.sp,
                                                color = MathError,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Action Buttons Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Scratchpad Button
                                    AssistChip(
                                        onClick = {
                                            openScratchpadQuestionId[question.id] = !isScratchpadOpen
                                        },
                                        label = { Text(if (isScratchpadOpen) "Tahtayı Kapat" else "Karalama Tahtası", fontSize = 11.sp) },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Create, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    )

                                    // AI Analysis Button
                                    AssistChip(
                                        onClick = {
                                            openAiExplanationQuestionId[question.id] = !isAiOpen
                                        },
                                        label = { Text(if (isAiOpen) "Analizi Kapat" else "AI Hatam Nerede?", fontSize = 11.sp) },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF38BDF8))
                                        }
                                    )

                                    // TTS Listen
                                    IconButton(
                                        onClick = {
                                            ttsManager.speak("Soru: ${question.questionText}. Doğru çözüm: ${question.explanation}")
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Dinle", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                // Embedded Scratchpad
                                AnimatedVisibility(visible = isScratchpadOpen) {
                                    Column {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        ScratchpadCard(
                                            title = "Soru ${question.id} İçin Karalama",
                                            initialHeightDp = 200,
                                            onClose = { openScratchpadQuestionId[question.id] = false }
                                        )
                                    }
                                }

                                // Embedded AI / Teacher Explanation
                                AnimatedVisibility(visible = isAiOpen) {
                                    Column {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            color = Slate900
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Öğretmen Çözüm Adımları:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = question.explanation,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFFCBD5E1),
                                                    lineHeight = 18.sp
                                                )
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
    }
}
