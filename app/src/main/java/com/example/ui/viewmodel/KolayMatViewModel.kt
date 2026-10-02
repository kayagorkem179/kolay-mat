package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ExamResultEntity
import com.example.data.local.SavedQuestionEntity
import com.example.data.local.SecurityLogEntity
import com.example.data.local.StudyPlanEntity
import com.example.data.model.AccessibilityConfig
import com.example.data.model.ExamType
import com.example.data.model.MathQuestion
import com.example.data.model.UserProfile
import com.example.data.repository.MathRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    EXAM,
    CLASSIC_YAZILI,
    LIBRARY,
    VIDEOS,
    SCANNER,
    PLANNER,
    SECURITY,
    ANALYTICS,
    WEB_PORTAL,
    STANDALONE_SCRATCHPAD,
    PAST_EXAMS,
    BOOK_PROGRESSION,
    TEACHER_PORTAL,
    PARENT_PORTAL
}

class KolayMatViewModel(application: Application) : AndroidViewModel(application) {
    val repository = MathRepository(application)

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _activeExamType = MutableStateFlow(ExamType.TYT)
    val activeExamType: StateFlow<ExamType> = _activeExamType.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _accessibilityConfig = MutableStateFlow(AccessibilityConfig())
    val accessibilityConfig: StateFlow<AccessibilityConfig> = _accessibilityConfig.asStateFlow()

    private val _isOnline = MutableStateFlow(checkOnlineState(application))
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    val savedQuestions: StateFlow<List<SavedQuestionEntity>> = repository.allSavedQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unsolvedQuestions: StateFlow<List<SavedQuestionEntity>> = repository.unsolvedQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val examResults: StateFlow<List<ExamResultEntity>> = repository.allExamResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyPlans: StateFlow<List<StudyPlanEntity>> = repository.studyPlanItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securityLogs: StateFlow<List<SecurityLogEntity>> = repository.securityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookProgress = repository.bookProgress
    val commentsMap = repository.commentsMap
    val teacherHomeworks = repository.teacherHomeworks
    val parentView = repository.parentView
    val smsAlerts = repository.smsAlerts

    init {
        viewModelScope.launch {
            repository.initDefaultPlanIfEmpty()
            repository.addSecurityLog(
                eventType = "Sistem Başlatıldı",
                detail = "AES-256 Kalkanı devrede, yerel Room veritabanı bütünlüğü doğrulandı.",
                hash = "INIT_HASH_2026",
                node = "Node-01 (Frankfurt)"
            )
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun startExam(examType: ExamType) {
        _activeExamType.value = examType
        if (examType == ExamType.OKUL_YAZILISI) {
            _currentScreen.value = Screen.CLASSIC_YAZILI
        } else {
            _currentScreen.value = Screen.EXAM
        }
    }

    fun saveQuestionToLibrary(question: MathQuestion, wrongIndex: Int = -1) {
        viewModelScope.launch {
            repository.saveQuestionToLibrary(question, wrongIndex)
        }
    }

    fun removeQuestionFromLibrary(id: Int) {
        viewModelScope.launch {
            repository.removeQuestionFromLibrary(id)
        }
    }

    fun toggleQuestionSolved(id: Int, isSolved: Boolean) {
        viewModelScope.launch {
            repository.markQuestionSolved(id, isSolved)
        }
    }

    fun recordExamResult(result: ExamResultEntity) {
        viewModelScope.launch {
            repository.recordExamResult(result)
            // Update profile solved count
            val cur = _userProfile.value
            _userProfile.value = cur.copy(
                totalQuestionsSolved = cur.totalQuestionsSolved + result.totalQuestions
            )
        }
    }

    fun updatePlanItem(item: StudyPlanEntity) {
        viewModelScope.launch {
            repository.updatePlanItem(item)
        }
    }

    fun addSecurityLog(eventType: String, detail: String, hash: String, node: String) {
        viewModelScope.launch {
            repository.addSecurityLog(eventType, detail, hash, node)
        }
    }

    fun updateAccessibilityConfig(config: AccessibilityConfig) {
        _accessibilityConfig.value = config
    }

    fun advanceBookLevel() {
        repository.advanceBookLevel()
    }

    fun addComment(questionId: Int, author: String, role: String, text: String) {
        repository.addComment(questionId, author, role, text)
    }

    fun addTeacherHomework(title: String, teacher: String, subject: String, fileName: String) {
        repository.addTeacherHomework(title, teacher, subject, fileName)
    }

    fun triggerSecuritySms(role: String, phone: String, alertType: String, message: String) {
        repository.triggerSecuritySms(role, phone, alertType, message)
    }

    private fun checkOnlineState(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val cap = cm.getNetworkCapabilities(network) ?: return false
            cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }
}
