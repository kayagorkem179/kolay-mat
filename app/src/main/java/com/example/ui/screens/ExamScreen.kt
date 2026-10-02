package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.local.ExamResultEntity
import com.example.data.model.ExamType
import com.example.data.model.MathQuestion
import com.example.data.model.QuestionComment
import com.example.data.remote.GeminiService
import com.example.ui.components.ScratchpadCard
import com.example.ui.theme.MathAccent
import com.example.ui.theme.MathError
import com.example.ui.theme.MathPrimary
import com.example.ui.theme.MathSuccess
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.TtsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    examType: ExamType,
    questions: List<MathQuestion>,
    ttsManager: TtsManager,
    commentsMap: Map<Int, List<QuestionComment>> = emptyMap(),
    onAddComment: (Int, String, String, String) -> Unit = { _, _, _, _ -> },
    onSaveQuestionToLibrary: (MathQuestion, Int) -> Unit,
    onExamCompleted: (ExamResultEntity) -> Unit,
    onExit: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } // questionId -> selectedIndex
    var remainingSeconds by remember { mutableIntStateOf(examType.durationMinutes * 60) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var isSubmitted by remember { mutableStateOf(false) }
    var showScratchpad by remember { mutableStateOf(false) }
    var showAiExplanationDialog by remember { mutableStateOf(false) }
    var aiAnalysisText by remember { mutableStateOf("") }
    var isAiLoading by remember { mutableStateOf(false) }
    val savedQuestionsMap = remember { mutableStateMapOf<Int, Boolean>() }
    var showConfirmFinishDialog by remember { mutableStateOf(false) }
    var showCommentsSection by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val currentQuestion = questions.getOrNull(currentIndex) ?: questions.first()

    // Countdown Timer Loop
    LaunchedEffect(isTimerRunning, isSubmitted) {
        while (isTimerRunning && !isSubmitted && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
        if (remainingSeconds <= 0 && !isSubmitted) {
            isSubmitted = true
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${examType.shortName} Denemesi (${questions.size} Soru)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Kazanım: ${currentQuestion.mebGainCode} • ${currentQuestion.topic}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            ttsManager.stop()
                            onExit()
                        },
                        modifier = Modifier.testTag("exit_exam_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    // Timer Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (remainingSeconds < 300) MathError.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Süre",
                                tint = if (remainingSeconds < 300) MathError else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedTime,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (remainingSeconds < 300) MathError else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Finish Exam Button
                    if (!isSubmitted) {
                        Button(
                            onClick = { showConfirmFinishDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MathSuccess),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("finish_exam_top_button")
                        ) {
                            Text("Bitir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("exam_screen_content")
        ) {
            // QUESTION NUMBER SELECTION RIBBON (1 to 20)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(questions) { idx, q ->
                    val isSelected = idx == currentIndex
                    val isAnswered = userAnswers.containsKey(q.id)
                    val isSaved = savedQuestionsMap[q.id] == true

                    val bgColor = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isSubmitted -> {
                            val selectedAns = userAnswers[q.id]
                            if (selectedAns == q.correctIndex) MathSuccess else if (selectedAns != null) MathError else Color(0xFF475569)
                        }
                        isAnswered -> Color(0xFF0EA5E9)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    val textColor = if (isSelected || isAnswered || isSubmitted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .clickable {
                                ttsManager.stop()
                                currentIndex = idx
                            }
                            .testTag("question_nav_$idx"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }
            }

            // MAIN SCROLLABLE QUESTION BODY
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    // Header Bar with Question Number & Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Soru ${currentIndex + 1} / ${questions.size} • ${currentQuestion.difficulty}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // TTS Speak
                            IconButton(
                                onClick = {
                                    val textToRead = "Soru ${currentIndex + 1}. ${currentQuestion.questionText}. " +
                                            currentQuestion.options.mapIndexed { i, opt ->
                                                "${('A' + i)} şıkkı: $opt"
                                            }.joinToString(". ")
                                    ttsManager.speak(textToRead)
                                },
                                modifier = Modifier.testTag("tts_speak_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Sesli Oku",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Scratchpad Toggle
                            IconButton(
                                onClick = { showScratchpad = !showScratchpad },
                                modifier = Modifier.testTag("toggle_scratchpad_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Karalama Tahtası",
                                    tint = if (showScratchpad) MathAccent else Color.Gray
                                )
                            }

                            // Save to Unsolved Library
                            val isSaved = savedQuestionsMap[currentQuestion.id] == true
                            IconButton(
                                onClick = {
                                    savedQuestionsMap[currentQuestion.id] = !isSaved
                                    onSaveQuestionToLibrary(currentQuestion, userAnswers[currentQuestion.id] ?: -1)
                                },
                                modifier = Modifier.testTag("save_to_library_button")
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Kütüphaneye Kaydet",
                                    tint = if (isSaved) MathAccent else Color.Gray
                                )
                            }
                        }
                    }
                }

                // Question Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("question_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = currentQuestion.questionText,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            currentQuestion.mathFormula?.let { formula ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Slate900
                                ) {
                                    Text(
                                        text = formula,
                                        color = Color(0xFFFBBF24),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // INLINE SCRATCHPAD CANVAS
                if (showScratchpad) {
                    item {
                        ScratchpadCard(
                            title = "Soru ${currentIndex + 1} Üzerinde Karalama",
                            onClose = { showScratchpad = false }
                        )
                    }
                }

                // OPTIONS (A, B, C, D, E)
                item {
                    Text(
                        text = "Seçenekler",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }

                itemsIndexed(currentQuestion.options) { optIndex, optionText ->
                    val optionLetter = ('A' + optIndex)
                    val isSelected = userAnswers[currentQuestion.id] == optIndex
                    val isCorrect = optIndex == currentQuestion.correctIndex

                    val borderColor = when {
                        isSubmitted && isCorrect -> MathSuccess
                        isSubmitted && isSelected && !isCorrect -> MathError
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> Color(0xFFCBD5E1)
                    }

                    val containerColor = when {
                        isSubmitted && isCorrect -> MathSuccess.copy(alpha = 0.15f)
                        isSubmitted && isSelected && !isCorrect -> MathError.copy(alpha = 0.15f)
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable {
                                if (!isSubmitted) {
                                    userAnswers[currentQuestion.id] = optIndex
                                }
                            }
                            .testTag("option_${optionLetter}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$optionLetter",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optionText,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSubmitted) {
                                if (isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Doğru",
                                        tint = MathSuccess
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hatalı",
                                        tint = MathError
                                    )
                                }
                            }
                        }
                    }
                }

                // AI PEDAGOGICAL ANALYSIS & WHY WRONG INSIGHT (Available once submitted or per user request)
                item {
                    val userSelected = userAnswers[currentQuestion.id]
                    if (isSubmitted || userSelected != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_helper_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Slate900)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Yapay Zeka Öğretmen Rehberi",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF38BDF8)
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            isAiLoading = true
                                            showAiExplanationDialog = true
                                            coroutineScope.launch {
                                                val selText = userSelected?.let { "Seçenek ${('A' + it)}: ${currentQuestion.options[it]}" } ?: "Boş Bırakıldı"
                                                val corText = "Seçenek ${('A' + currentQuestion.correctIndex)}: ${currentQuestion.options[currentQuestion.correctIndex]}"
                                                aiAnalysisText = GeminiService.analyzeMistake(
                                                    questionText = currentQuestion.questionText,
                                                    selectedOption = selText,
                                                    correctOption = corText,
                                                    topic = currentQuestion.topic
                                                )
                                                isAiLoading = false
                                            }
                                        },
                                        modifier = Modifier.height(32.dp).testTag("ask_ai_teacher_button"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Hatam Nerede? Sor", fontSize = 11.sp, color = Color(0xFF38BDF8))
                                    }
                                }

                                if (isSubmitted) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Öğretmen Çözümü:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = currentQuestion.explanation,
                                        fontSize = 12.sp,
                                        color = Color(0xFFCBD5E1),
                                        lineHeight = 18.sp
                                    )

                                    if (currentQuestion.teacherTip.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "💡 Püf Nokta: ${currentQuestion.teacherTip}",
                                            fontSize = 11.sp,
                                            color = Color(0xFFFBBF24),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // QUESTION COMMENTS & TACTICS DISCUSSION SYSTEM (Öğrenci & Öğretmen Yorumlama Sistemi)
                item {
                    val comments = commentsMap[currentQuestion.id] ?: emptyList()
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("question_comments_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { showCommentsSection = !showCommentsSection },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Soru Yorumları & Taktikler (${comments.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = if (showCommentsSection) "Gizle" else "Yorumları Gör",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (showCommentsSection) {
                                Spacer(modifier = Modifier.height(10.dp))

                                if (comments.isEmpty()) {
                                    Text("Bu soruya henüz yorum yapılmamış. İlk yorumu sen yaz!", fontSize = 11.sp, color = Color.Gray)
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        comments.forEach { c ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Slate800,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(c.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = if (c.authorRole == "Öğretmen") MathSuccess.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                                        ) {
                                                            Text(c.authorRole, fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(c.commentText, color = Color(0xFFCBD5E1), fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Add new comment row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = newCommentText,
                                        onValueChange = { newCommentText = it },
                                        placeholder = { Text("Püf nokta veya taktik yaz...", fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f).testTag("new_comment_field"),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            if (newCommentText.isNotBlank()) {
                                                onAddComment(currentQuestion.id, "Görkem Kaya", "Öğrenci", newCommentText)
                                                newCommentText = ""
                                            }
                                        },
                                        modifier = Modifier.testTag("send_comment_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Send, contentDescription = "Gönder", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // BOTTOM NAVIGATION BAR (Previous, Next, Finish)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            ttsManager.stop()
                            if (currentIndex > 0) currentIndex--
                        },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("prev_question_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Önceki")
                    }

                    if (currentIndex < questions.size - 1) {
                        Button(
                            onClick = {
                                ttsManager.stop()
                                currentIndex++
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("next_question_button")
                        ) {
                            Text("Sonraki")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = { showConfirmFinishDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MathSuccess),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("finish_exam_bottom_button")
                        ) {
                            Text("Sınavı Tamamla")
                        }
                    }
                }
            }
        }
    }

    // AI EXPLANATION MODAL DIALOG
    if (showAiExplanationDialog) {
        AlertDialog(
            onDismissRequest = { showAiExplanationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Yapay Zeka Pedagojik Hata Analizi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                if (isAiLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Yapay zeka hatanızı ve çözüm adımlarını analiz ediyor...", fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.height(260.dp)) {
                        item {
                            Text(
                                text = aiAnalysisText,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        ttsManager.speak(aiAnalysisText)
                    },
                    modifier = Modifier.testTag("tts_speak_ai_button")
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sesli Dinle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiExplanationDialog = false }) {
                    Text("Kapat")
                }
            }
        )
    }

    // CONFIRM FINISH EXAM DIALOG
    if (showConfirmFinishDialog) {
        val answeredCount = userAnswers.size
        val emptyCount = questions.size - answeredCount

        AlertDialog(
            onDismissRequest = { showConfirmFinishDialog = false },
            title = { Text("Sınavı Bitirmek İstiyor musun?") },
            text = {
                Text(
                    text = "Toplam ${questions.size} sorudan $answeredCount tanesini yanıtladın, $emptyCount tanesi boş.\n\nSınavı bitirdiğinde doğru/yanlış analizi ve puan karnen hesaplanacaktır."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmFinishDialog = false
                        isSubmitted = true
                        isTimerRunning = false

                        // Calculate results
                        var correct = 0
                        var wrong = 0
                        questions.forEach { q ->
                            val userAns = userAnswers[q.id]
                            if (userAns != null) {
                                if (userAns == q.correctIndex) correct++ else wrong++
                            }
                        }
                        val empty = questions.size - (correct + wrong)
                        val net = correct - (wrong * 0.25f)

                        val result = ExamResultEntity(
                            title = "${examType.title} Denemesi",
                            examType = examType.shortName,
                            totalQuestions = questions.size,
                            correctCount = correct,
                            wrongCount = wrong,
                            emptyCount = empty,
                            netScore = net,
                            durationSeconds = (examType.durationMinutes * 60) - remainingSeconds
                        )
                        onExamCompleted(result)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MathSuccess),
                    modifier = Modifier.testTag("confirm_finish_button")
                ) {
                    Text("Evet, Bitir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmFinishDialog = false }) {
                    Text("Devam Et")
                }
            }
        )
    }
}
