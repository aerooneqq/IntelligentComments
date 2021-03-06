package com.intelligentcomments.test.cases

import com.intelligentcomments.core.comments.RiderCommentsController
import com.jetbrains.rdclient.testFramework.executeWithGold
import com.jetbrains.rdclient.testFramework.waitForDaemon
import com.jetbrains.rider.test.base.BaseTestWithSolution
import com.jetbrains.rider.test.scriptingApi.doHighlighting
import com.jetbrains.rider.test.scriptingApi.withOpenedEditor
import org.testng.annotations.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class UiModelTest : BaseTestWithSolution() {
  override val waitForCaches: Boolean = true
  override fun getSolutionDirectoryName() = "RectanglesModelTest"

  @Test
  fun checkThatCommentsWhereAdded() {
    withOpenedEditor("SimpleTest1.cs") {
      val project = this.project ?: fail("Project was null")
      doHighlighting(project, this)
      waitForDaemon()
      assertTrue { project.getComponent(RiderCommentsController::class.java).getAllCommentsFor(this).isNotEmpty() }
    }
  }

  @Test
  fun test1() = executeTestWithRectangleModel("SimpleTest1.cs")

  @Test
  fun test2() = executeTestWithRectangleModel("Test2.cs")

  @Test
  fun test3() = executeTestWithRectangleModel("Test3.cs")

  @Test
  fun test4() = executeTestWithRectangleModel("Test4.cs")



  private fun executeTestWithRectangleModel(fileName: String) {
    withOpenedEditor(fileName) {
      val project = this.project ?: fail("Project was null")
      doHighlighting(project, this)
      waitForDaemon()
      executeWithGold(testGoldFile) {
        for (comment in project.getComponent(RiderCommentsController::class.java).getAllCommentsFor(this)) {
          it.println(comment.uiModel.dumpModel())
        }
      }
    }
  }
}