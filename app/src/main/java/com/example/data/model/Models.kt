package com.example.data.model

enum class UserRole(val displayName: String, val badge: String) {
    STUDENT("Öğrenci", "Öğrenci Girişi"),
    TEACHER("Öğretmen", "Öğretmen Portalı"),
    PARENT("Veli", "Veli Gözetim Modu"),
    GUEST("Kayıtsız Ziyaretçi", "Misafir")
}

enum class ExamType(
    val title: String,
    val shortName: String,
    val badge: String,
    val defaultQuestionCount: Int,
    val durationMinutes: Int,
    val description: String
) {
    TYT(
        title = "YKS - TYT Matematik",
        shortName = "TYT",
        badge = "ÖSYM",
        defaultQuestionCount = 20,
        durationMinutes = 40,
        description = "Temel Yeterlilik Testi - Sayılar, Problemler, Mantık ve Temel Geometri"
    ),
    AYT(
        title = "YKS - AYT Matematik",
        shortName = "AYT",
        badge = "ÖSYM",
        defaultQuestionCount = 20,
        durationMinutes = 50,
        description = "Alan Yeterlilik Testi - Fonksiyonlar, Trigonometri, Logaritma, Türev & İntegral"
    ),
    LGS(
        title = "LGS Matematik (8. Sınıf)",
        shortName = "LGS",
        badge = "MEB",
        defaultQuestionCount = 20,
        durationMinutes = 40,
        description = "Liselere Geçiş Sistemi - Yeni Nesil Akıl Yürütme, Üslü-Köklü ve Geometri"
    ),
    KPSS(
        title = "KPSS Sayısal / Genel Yetenek",
        shortName = "KPSS",
        badge = "ÖSYM",
        defaultQuestionCount = 20,
        durationMinutes = 35,
        description = "Kamu Personel Sınavı - Temel Matematik, Problemler ve Tablo-Grafik Yorumlama"
    ),
    DGS(
        title = "DGS Sayısal Bölüm",
        shortName = "DGS",
        badge = "ÖSYM",
        defaultQuestionCount = 20,
        durationMinutes = 40,
        description = "Dikey Geçiş Sınavı - Mantıksal Akıl Yürütme, Sayısal Mantık ve Hızlı Çözüm"
    ),
    ALES(
        title = "ALES Sayısal Test",
        shortName = "ALES",
        badge = "ÖSYM",
        defaultQuestionCount = 20,
        durationMinutes = 45,
        description = "Akademik Lisansüstü Eğitimi - İleri Sayısal Mantık, Şekil Yeteneği ve Problemler"
    ),
    OKUL_YAZILISI(
        title = "MEB Okul Yazılısı (Klasik Sınav)",
        shortName = "Yazılı",
        badge = "MEB Ortak Sınav",
        defaultQuestionCount = 10,
        durationMinutes = 40,
        description = "Yeni MEB Müfredatına Uygun Açık Uçlu, Adımlı Puanlanan Klasik Yazılı Soruları"
    ),
    CIKMIS_SORULAR(
        title = "ÖSYM & MEB Çıkmış Sorular (2021-2024)",
        shortName = "Çıkmış",
        badge = "ÖSYM & MEB Arşiv",
        defaultQuestionCount = 20,
        durationMinutes = 40,
        description = "Önceki Yıllarda Gerçek Sınavlarda Sorulmuş Resmi Çıkmış Matematik Soruları"
    )
}

data class ClassicRubricStep(
    val stepNumber: Int,
    val title: String,
    val points: Int,
    val expectedExplanation: String
)

data class MathQuestion(
    val id: Int,
    val examType: ExamType,
    val topic: String,
    val questionText: String,
    val mathFormula: String? = null,
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val explanation: String,
    val whyWrongAnalysis: Map<Int, String> = emptyMap(),
    val isClassic: Boolean = false,
    val rubricSteps: List<ClassicRubricStep> = emptyList(),
    val difficulty: String = "Orta", // Kolay, Orta, İleri
    val difficultyLevel: Int = 2, // 1: Aktif Başlangıç, 2: Orta, 3: İleri
    val teacherTip: String = "",
    val mebGainCode: String = "M.8.1.1",
    val isPastExamQuestion: Boolean = false,
    val pastExamYear: Int? = null,
    val pastExamLabel: String? = null
)

data class QuestionComment(
    val id: String,
    val questionId: Int,
    val authorName: String,
    val authorRole: String,
    val commentText: String,
    val upvotes: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class BookLevelProgress(
    val bookName: String = "Aktif Öğrenme Yayınları: 0'dan Başlayanlara Matematik",
    val currentLevel: Int = 1, // 1: Başlangıç, 2: Orta, 3: İleri
    val levelName: String = "Seviye 1 (Temel & Aktif Matematik)",
    val targetExam: String = "TYT Matematik",
    val completedPages: Int = 48,
    val totalPages: Int = 180,
    val nextRecommendedBook: String = "Bilgi Sarmal / 345 TYT Matematik Soru Bankası",
    val isScanned: Boolean = true
)

data class TeacherHomework(
    val id: String,
    val title: String,
    val teacherName: String,
    val subject: String,
    val fileName: String,
    val targetGroup: String = "12-A / YKS Hazırlık",
    val deadline: String = "3 Gün Kaldı",
    val totalQuestions: Int = 25,
    val isSubmitted: Boolean = false
)

data class ParentStudentView(
    val studentName: String = "Görkem Kaya",
    val studentPhone: String = "0505 751 3807",
    val parentPhone: String = "0555 123 4567",
    val targetExam: String = "YKS 2027 Hedef Tıp/Mühendislik",
    val weeklyQuestionsSolved: Int = 210,
    val weeklyGoal: Int = 250,
    val averageNetTYT: Float = 26.5f,
    val totalStudyHoursThisWeek: Float = 14.5f,
    val strongestTopic: String = "Temel Kavramlar & Sayılar (%88 Başarı)",
    val weakTopicNeedsAttention: String = "Problemler & Geometri (%54 Başarı)",
    val teacherFeedback: String = "Görkem bu hafta düzenli soru çözdü. Aktif Matematik kitabını tamamladı, seviyesi arttığı için bir üst seviye kitaba geçti.",
    val streakDays: Int = 6
)

data class SmsAlert(
    val id: String,
    val recipientName: String,
    val recipientPhone: String,
    val recipientRole: String, // Öğrenci / Veli
    val alertType: String, // "HACK_PREVENTED", "VIRUS_ISOLATED", "FAILOVER_ACTIVATED"
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AccessibilityConfig(
    val fontScale: Float = 1.0f,
    val isHighContrast: Boolean = false,
    val ttsRate: Float = 1.0f
)

data class SecuritySystemState(
    val isShieldActive: Boolean = true,
    val encryptionAlgorithm: String = "AES-256-GCM + Quantum-Resistant Salt",
    val integrityHash: String = "SHA-256 (3b1a8... Doğrulandı)",
    val activeServerNode: String = "Node-01 (Frankfurt - Birincil)",
    val secondaryServerNode: String = "Node-02 (Dublin - Sıcak Yedek)",
    val failoverLatencyMs: Int = 42,
    val totalFilesScanned: Int = 184,
    val threatsNeutralized: Int = 0,
    val isZeroCrashModeEnabled: Boolean = true,
    val lastBackupTimestamp: Long = System.currentTimeMillis(),
    val isOfflineModeReady: Boolean = true,
    val smsGatewayActive: Boolean = true
)

data class VideoTopic(
    val id: String,
    val title: String,
    val durationText: String,
    val teacherName: String,
    val keyPoints: List<String>,
    val formulaSummary: String,
    val examTypes: List<ExamType>,
    val videoPreviewDesc: String
)

data class UserProfile(
    val name: String = "Görkem Kaya",
    val role: UserRole = UserRole.STUDENT,
    val isLoggedIn: Boolean = true,
    val email: String = "kayagorkem179@gmail.com",
    val phone: String = "0505 751 3807",
    val gradeOrTarget: String = "YKS (TYT-AYT) Hedef 2027",
    val studyGoalDailyMinutes: Int = 60,
    val currentStreakDays: Int = 6,
    val totalQuestionsSolved: Int = 185,
    val currentSkillScore: Int = 1450 // Gelişime göre zorlaşma skoru
)
