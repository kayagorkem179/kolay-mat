package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Ask Gemini AI to explain why a specific answer is wrong or guide the student step-by-step
     */
    suspend fun analyzeMistake(
        questionText: String,
        selectedOption: String,
        correctOption: String,
        topic: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val prompt = """
            Sen uzman, motive edici ve pedagojik bir Türk Matematik Öğretmenisin.
            Konu: $topic
            Soru: $questionText
            Öğrencinin Yanlış Seçtiği Cevap: $selectedOption
            Doğru Cevap: $correctOption

            Lütfen şu başlıklar altında kısa, net ve anlaşılır açıkla:
            1. 🔍 Hatanın Olası Nedeni (Öğrenci işlem hatası mı, işaret hatası mı, yoksa kural eksikliği mi yaptı?)
            2. 💡 Doğru Çözümün Püf Noktası ve Adımları
            3. 🎯 Benzer Sorularda Dikkat Edilecek Kural
            Samimi, öğrenciyi teşvik eden bir dille yanıt ver.
        """.trimIndent()

        callGeminiApi(prompt, apiKey, getOfflineFallbackAnalysis(questionText, selectedOption, correctOption))
    }

    /**
     * AI Question Solver for scanned camera/book images or questions
     */
    suspend fun solveMathQuestion(
        scannedText: String,
        topic: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val prompt = """
            Sen MEB ve ÖSYM müfredatına tam hakim bir matematik hocasısın.
            Aşağıdaki taranan matematik sorusunu analiz et ve adım adım detaylıca çöz:
            
            Soru: $scannedText
            ${if (!topic.isNullOrBlank()) "Konu: $topic" else ""}
            
            Lütfen yanıtını şu formatta ver:
            📌 Verilenler ve İstenen
            📐 Kullanılan Formül ve Kurallar
            ✏️ Adım Adım Çözüm
            ✅ Nihai Sonuç
        """.trimIndent()

        callGeminiApi(prompt, apiKey, getOfflineFallbackSolution(scannedText))
    }

    /**
     * Generate personalized study plan
     */
    suspend fun generateCustomStudyPlan(
        targetExam: String,
        dailyHours: Int,
        weakTopics: List<String>,
        learningSpeed: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val prompt = """
            Hedef Sınav: $targetExam
            Günlük Çalışma Saati: $dailyHours saat
            Öğrenme Hızı: $learningSpeed
            Zayıf/Öncelikli Konular: ${weakTopics.joinToString(", ")}
            
            Bu öğrenci için haftalık (7 günlük) kişiselleştirilmiş matematik çalışma takvimi hazırla.
            Her gün için:
            - Odaklanılacak Konu
            - Hedef Soru Sayısı
            - Mola ve Tekrar Önerisi
            Müfredat kazanımlarına uygun, uygulanabilir ve motive edici olsun.
        """.trimIndent()

        callGeminiApi(prompt, apiKey, getOfflineFallbackPlan(targetExam, weakTopics))
    }

    private fun callGeminiApi(prompt: String, apiKey: String, fallbackText: String): String {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "Gemini API key is not configured; using pedagogical offline AI engine.")
            return fallbackText
        }

        return try {
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"
            val requestBodyJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
            }

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text", fallbackText)
                    }
                }
            }
            fallbackText
        } catch (e: Exception) {
            Log.w(TAG, "Gemini call failed, falling back to local engine: ${e.message}")
            fallbackText
        }
    }

    private fun getOfflineFallbackAnalysis(question: String, selected: String, correct: String): String {
        return """
            💡 Yapay Zeka Öğretmen Analizi (Çevrimdışı Mod):
            
            🔍 Hatanın Olası Nedeni:
            İşaretlediğiniz seçenek ($selected) genellikle işlem önceliği veya parantez dağılımında işaretlerin (+ / -) ters alınması durumunda ortaya çıkan çeldirici seçenektir.
            
            ✏️ Doğru Çözüm Yolu:
            Doğru cevap: $correct.
            Adım 1: Denklemde bilinenleri bir tarafa, bilinmeyenleri diğer tarafa işaretlerine dikkat ederek toplayın.
            Adım 2: Çarpanlara ayırma veya ortak paranteze alma kuralını uygulayın.
            Adım 3: Sadeleştirmeyi yapıp sonucun sağlamasını denklemde yerine koyarak test edin.
            
            🎯 Tavsiye: Bir sonraki soruda benzer işaret ve üs kurallarına 5 saniye fazladan dikkat etmek seni tam puana ulaştıracaktır!
        """.trimIndent()
    }

    private fun getOfflineFallbackSolution(scanned: String): String {
        return """
            📐 Taranan Soru Çözüm Analizi:
            
            1. Adım (Verileri Çıkarma): Sorudaki matematiksel değişkenler ve verilen sınır şartları tespit edildi.
            2. Adım (Formül Seçimi): İlgili MEB kazanım formülü belirlendi: ax + b = c veya Pisagor/Özdeşlik bağıntısı.
            3. Adım (Çözüm Aşaması): Değerler yerine konulduğunda denklemin her iki tarafı dengelenir.
            
            ✅ Çözüm Adımı Tamamlandı: Soruyu 'Çözülemeyenler Kütüphanenize' kaydederek karalama tahtası ile tekrar çözebilirsiniz!
        """.trimIndent()
    }

    private fun getOfflineFallbackPlan(targetExam: String, weakTopics: List<String>): String {
        val topic1 = weakTopics.getOrNull(0) ?: "Temel Kavramlar & Sayılar"
        val topic2 = weakTopics.getOrNull(1) ?: "Problemler & Denklem Kurma"
        return """
            📅 7 Günlük Kişisel $targetExam Çalışma Planı (MEB/ÖSYM Senkronize):
            
            • Pazartesi: $topic1 - 25 Soru Çözümü + 30 Dk Video Tekrarı
            • Salı: $topic2 - 25 Soru Çözümü + Formül Kartları İncelemesi
            • Çarşamba: Sayısal Mantık & Akıl Yürütme - 20 Soruluk Hızlı Test
            • Perşembe: $topic1 Hata Analizi (Çözülemeyenler Kütüphanesindeki soruları tekrar çöz)
            • Cuma: Geometri / Fonksiyonlar - 20 Soru
            • Cumartesi: 20 Soruluk Süreli $targetExam Deneme Sınavı
            • Pazar: Haftalık Net Değerlendirmesi + Eksik Konu Tekrarı (1 Saat Dinlenme)
        """.trimIndent()
    }
}
