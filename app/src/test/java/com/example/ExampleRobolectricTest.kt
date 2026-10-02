package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ExamType
import com.example.data.repository.MathRepository
import com.example.ui.viewmodel.KolayMatViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("KolayMat", appName)
  }

  @Test
  fun `verify exam question counts satisfy minimum 20 questions requirement`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = MathRepository(context)

    // Check TYT
    val tytQuestions = repo.getQuestionsForExam(ExamType.TYT)
    assertTrue("TYT should have at least 20 questions", tytQuestions.size >= 20)

    // Check Past Exams
    assertTrue("Past exams should have at least 20 questions", repo.pastExamQuestions.size >= 20)

    // Check AYT
    val aytQuestions = repo.getQuestionsForExam(ExamType.AYT)
    assertTrue("AYT should have questions", aytQuestions.isNotEmpty())

    // Check Classic Written Exam
    assertTrue("Classic exam should have questions", repo.classicYaziliQuestions.isNotEmpty())
  }

  @Test
  fun `test activity launch without crash`() {
    val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
    val activity = controller.get()
    assertNotNull(activity)
    controller.pause().stop().destroy()
  }

  @Test
  fun `test viewmodel state navigation and actions`() = runTest {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = KolayMatViewModel(application)

    // Initial screen should be HOME
    assertEquals(Screen.HOME, viewModel.currentScreen.value)

    // Test navigation to all screens
    val screensToTest = listOf(
      Screen.EXAM,
      Screen.CLASSIC_YAZILI,
      Screen.LIBRARY,
      Screen.VIDEOS,
      Screen.SCANNER,
      Screen.PLANNER,
      Screen.SECURITY,
      Screen.ANALYTICS,
      Screen.WEB_PORTAL,
      Screen.STANDALONE_SCRATCHPAD,
      Screen.PAST_EXAMS,
      Screen.BOOK_PROGRESSION,
      Screen.TEACHER_PORTAL,
      Screen.PARENT_PORTAL,
      Screen.HOME
    )
    for (screen in screensToTest) {
      viewModel.navigateTo(screen)
      assertEquals(screen, viewModel.currentScreen.value)
    }

    // Test starting an exam
    viewModel.startExam(ExamType.TYT)
    assertEquals(Screen.EXAM, viewModel.currentScreen.value)
    assertEquals(ExamType.TYT, viewModel.activeExamType.value)

    // Test starting classic exam
    viewModel.startExam(ExamType.OKUL_YAZILISI)
    assertEquals(Screen.CLASSIC_YAZILI, viewModel.currentScreen.value)

    // Test book progress advancement
    val initialLevel = viewModel.bookProgress.value.currentLevel
    viewModel.advanceBookLevel()
    assertEquals(initialLevel + 1, viewModel.bookProgress.value.currentLevel)

    // Test adding homework
    viewModel.addTeacherHomework(
      title = "Trigonometri Ödevi",
      teacher = "Ahmet Hoca",
      subject = "AYT Matematik",
      fileName = "trig_test.pdf"
    )
    assertTrue(viewModel.teacherHomeworks.value.any { it.title == "Trigonometri Ödevi" })

    // Test adding security log
    viewModel.addSecurityLog(
      eventType = "Test Tehdit Taraması",
      detail = "SHA-256 doğrulandı, sıfır hata.",
      hash = "TEST_HASH_123",
      node = "Node-01"
    )
  }
}
