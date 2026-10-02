package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExamType
import com.example.ui.components.ScratchpadCard
import com.example.ui.screens.AccessibilityDialog
import com.example.ui.screens.AiPlannerScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BookProgressionScreen
import com.example.ui.screens.ClassicExamScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.HelpSupportDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ParentPortalScreen
import com.example.ui.screens.PastExamsScreen
import com.example.ui.screens.QuestionLibraryScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.TeacherPortalScreen
import com.example.ui.screens.TopicVideosScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.theme.KolayMatTheme
import com.example.ui.theme.MathAccent
import com.example.ui.theme.MathPrimary
import com.example.ui.theme.MathSuccess
import com.example.ui.viewmodel.KolayMatViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.TtsManager

class MainActivity : ComponentActivity() {
    private val viewModel: KolayMatViewModel by viewModels()
    private lateinit var ttsManager: TtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ttsManager = TtsManager(this)

        setContent {
            val accessibilityConfig by viewModel.accessibilityConfig.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val activeExamType by viewModel.activeExamType.collectAsStateWithLifecycle()
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

            val savedQuestions by viewModel.savedQuestions.collectAsStateWithLifecycle()
            val unsolvedQuestions by viewModel.unsolvedQuestions.collectAsStateWithLifecycle()
            val examResults by viewModel.examResults.collectAsStateWithLifecycle()
            val studyPlans by viewModel.studyPlans.collectAsStateWithLifecycle()
            val securityLogs by viewModel.securityLogs.collectAsStateWithLifecycle()
            val bookProgress by viewModel.bookProgress.collectAsStateWithLifecycle()
            val teacherHomeworks by viewModel.teacherHomeworks.collectAsStateWithLifecycle()
            val parentView by viewModel.parentView.collectAsStateWithLifecycle()
            val smsAlerts by viewModel.smsAlerts.collectAsStateWithLifecycle()

            var showAccessibilityDialog by remember { mutableStateOf(false) }
            var showHelpSupportDialog by remember { mutableStateOf(false) }

            KolayMatTheme(
                isHighContrast = accessibilityConfig.isHighContrast
            ) {
                // Handle system back navigation to return home
                if (currentScreen != Screen.HOME) {
                    BackHandler {
                        ttsManager.stop()
                        viewModel.navigateTo(Screen.HOME)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (currentScreen == Screen.HOME) {
                            HomeMainTopBar(
                                isOnline = isOnline,
                                onOpenSecurity = { viewModel.navigateTo(Screen.SECURITY) },
                                onOpenAccessibility = { showAccessibilityDialog = true },
                                onOpenWebPortal = { viewModel.navigateTo(Screen.WEB_PORTAL) },
                                onOpenScratchpad = { viewModel.navigateTo(Screen.STANDALONE_SCRATCHPAD) }
                            )
                        }
                    },
                    bottomBar = {
                        // Show bottom nav on main dashboard screens
                        if (currentScreen == Screen.HOME ||
                            currentScreen == Screen.LIBRARY ||
                            currentScreen == Screen.VIDEOS ||
                            currentScreen == Screen.ANALYTICS
                        ) {
                            KolayMatBottomNavigation(
                                currentScreen = currentScreen,
                                unsolvedCount = unsolvedQuestions.size,
                                onSelectScreen = { screen ->
                                    ttsManager.stop()
                                    viewModel.navigateTo(screen)
                                },
                                onStartQuickExam = {
                                    ttsManager.stop()
                                    viewModel.startExam(ExamType.TYT)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.HOME -> HomeScreen(
                                userProfile = userProfile,
                                unsolvedCount = unsolvedQuestions.size,
                                onStartExam = { examType ->
                                    ttsManager.stop()
                                    viewModel.startExam(examType)
                                },
                                onOpenScratchpad = { viewModel.navigateTo(Screen.STANDALONE_SCRATCHPAD) },
                                onOpenScanner = { viewModel.navigateTo(Screen.SCANNER) },
                                onOpenLibrary = { viewModel.navigateTo(Screen.LIBRARY) },
                                onOpenVideos = { viewModel.navigateTo(Screen.VIDEOS) },
                                onOpenPlanner = { viewModel.navigateTo(Screen.PLANNER) },
                                onOpenSecurity = { viewModel.navigateTo(Screen.SECURITY) },
                                onOpenClassicYazili = {
                                    ttsManager.stop()
                                    viewModel.startExam(ExamType.OKUL_YAZILISI)
                                },
                                onOpenPastExams = { viewModel.navigateTo(Screen.PAST_EXAMS) },
                                onOpenBookProgression = { viewModel.navigateTo(Screen.BOOK_PROGRESSION) },
                                onOpenTeacherPortal = { viewModel.navigateTo(Screen.TEACHER_PORTAL) },
                                onOpenParentPortal = { viewModel.navigateTo(Screen.PARENT_PORTAL) },
                                onOpenHelpSupport = { showHelpSupportDialog = true }
                            )

                            Screen.EXAM -> ExamScreen(
                                examType = activeExamType,
                                questions = viewModel.repository.getQuestionsForExam(activeExamType),
                                ttsManager = ttsManager,
                                onSaveQuestionToLibrary = { q, wrongIdx ->
                                    viewModel.saveQuestionToLibrary(q, wrongIdx)
                                },
                                onExamCompleted = { record ->
                                    viewModel.recordExamResult(record)
                                },
                                onExit = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.CLASSIC_YAZILI -> ClassicExamScreen(
                                questions = viewModel.repository.classicYaziliQuestions,
                                ttsManager = ttsManager,
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.LIBRARY -> QuestionLibraryScreen(
                                savedQuestions = savedQuestions,
                                ttsManager = ttsManager,
                                onDeleteQuestion = { id -> viewModel.removeQuestionFromLibrary(id) },
                                onToggleSolved = { id, isSolved -> viewModel.toggleQuestionSolved(id, isSolved) },
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.VIDEOS -> TopicVideosScreen(
                                videoTopics = viewModel.repository.videoTopics,
                                ttsManager = ttsManager,
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.SCANNER -> ScannerScreen(
                                ttsManager = ttsManager,
                                onSaveToLibrary = { q -> viewModel.saveQuestionToLibrary(q) },
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.PLANNER -> AiPlannerScreen(
                                planItems = studyPlans,
                                onUpdatePlanItem = { item -> viewModel.updatePlanItem(item) },
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.SECURITY -> SecurityScreen(
                                securityLogs = securityLogs,
                                smsAlerts = smsAlerts,
                                onTriggerSms = { role, phone, type, msg ->
                                    viewModel.triggerSecuritySms(role, phone, type, msg)
                                },
                                onAddLog = { event, detail, hash, node ->
                                    viewModel.addSecurityLog(event, detail, hash, node)
                                },
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.ANALYTICS -> AnalyticsScreen(
                                examResults = examResults,
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.WEB_PORTAL -> WebPortalScreen(
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.STANDALONE_SCRATCHPAD -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                 ) {
                                    ScratchpadCard(
                                        title = "Tam Ekran Matematik Karalama Tahtası",
                                        initialHeightDp = 480,
                                        onClose = { viewModel.navigateTo(Screen.HOME) }
                                    )
                                }
                            }

                            Screen.PAST_EXAMS -> PastExamsScreen(
                                questions = viewModel.repository.pastExamQuestions,
                                ttsManager = ttsManager,
                                onStartPastExamTrial = {
                                    ttsManager.stop()
                                    viewModel.startExam(ExamType.TYT)
                                },
                                onSaveToLibrary = { q ->
                                    viewModel.saveQuestionToLibrary(q)
                                },
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.BOOK_PROGRESSION -> BookProgressionScreen(
                                bookProgress = bookProgress,
                                onAdvanceLevel = { viewModel.advanceBookLevel() },
                                onScanNewBook = { viewModel.navigateTo(Screen.SCANNER) },
                                onStartAdaptiveTest = { _ ->
                                    ttsManager.stop()
                                    viewModel.startExam(ExamType.TYT)
                                },
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.TEACHER_PORTAL -> TeacherPortalScreen(
                                homeworks = teacherHomeworks,
                                onAddHomework = { title, teacher, subject, fileName ->
                                    viewModel.addTeacherHomework(title, teacher, subject, fileName)
                                },
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )

                            Screen.PARENT_PORTAL -> ParentPortalScreen(
                                parentData = parentView,
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )
                        }
                    }

                    if (showAccessibilityDialog) {
                        AccessibilityDialog(
                            config = accessibilityConfig,
                            onUpdateConfig = { viewModel.updateAccessibilityConfig(it) },
                            onDismiss = { showAccessibilityDialog = false }
                        )
                    }

                    if (showHelpSupportDialog) {
                        HelpSupportDialog(
                            userProfile = userProfile,
                            onDismiss = { showHelpSupportDialog = false }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMainTopBar(
    isOnline: Boolean,
    onOpenSecurity: () -> Unit,
    onOpenAccessibility: () -> Unit,
    onOpenWebPortal: () -> Unit,
    onOpenScratchpad: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("π", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "KolayMat",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        navigationIcon = {
            // Online / Offline Status Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isOnline) MathSuccess.copy(alpha = 0.15f) else Color(0xFF64748B).copy(alpha = 0.2f),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = if (isOnline) MathSuccess else Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOnline) "Bulut Aktif" else "Çevrimdışı Hazır",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOnline) MathSuccess else Color.Gray
                    )
                }
            }
        },
        actions = {
            // Standalone Scratchpad Shortcut
            IconButton(onClick = onOpenScratchpad, modifier = Modifier.testTag("top_scratchpad_button")) {
                Icon(imageVector = Icons.Default.Create, contentDescription = "Karalama Tahtası", tint = MathAccent)
            }

            // Web Portal Shortcut
            IconButton(onClick = onOpenWebPortal, modifier = Modifier.testTag("top_web_portal_button")) {
                Icon(imageVector = Icons.Default.Language, contentDescription = "Web Portalı", tint = MaterialTheme.colorScheme.secondary)
            }

            // Accessibility Settings Shortcut
            IconButton(onClick = onOpenAccessibility, modifier = Modifier.testTag("top_accessibility_button")) {
                Icon(imageVector = Icons.Default.AccessibilityNew, contentDescription = "Erişilebilirlik", tint = MaterialTheme.colorScheme.primary)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun KolayMatBottomNavigation(
    currentScreen: Screen,
    unsolvedCount: Int,
    onSelectScreen: (Screen) -> Unit,
    onStartQuickExam: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.HOME,
            onClick = { onSelectScreen(Screen.HOME) },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Anasayfa") },
            label = { Text("Anasayfa", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = false,
            onClick = onStartQuickExam,
            icon = { Icon(imageVector = Icons.Default.Timer, contentDescription = "20'lik Deneme", tint = MaterialTheme.colorScheme.primary) },
            label = { Text("Denemeler", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_exam")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.LIBRARY,
            onClick = { onSelectScreen(Screen.LIBRARY) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unsolvedCount > 0) {
                            Badge { Text("$unsolvedCount") }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.MenuBook, contentDescription = "Kütüphane")
                }
            },
            label = { Text("Hata Defteri", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_library")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.VIDEOS,
            onClick = { onSelectScreen(Screen.VIDEOS) },
            icon = { Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = "Videolar") },
            label = { Text("Videolar", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_videos")
        )

        NavigationBarItem(
            selected = currentScreen == Screen.ANALYTICS,
            onClick = { onSelectScreen(Screen.ANALYTICS) },
            icon = { Icon(imageVector = Icons.Default.Scoreboard, contentDescription = "Analitik") },
            label = { Text("Analitik", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_analytics")
        )
    }
}
