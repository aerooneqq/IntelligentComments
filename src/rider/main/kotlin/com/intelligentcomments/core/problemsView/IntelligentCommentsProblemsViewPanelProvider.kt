package com.intelligentcomments.core.problemsView

import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.impl.ContentImpl
import com.intellij.ui.content.impl.ContentManagerImpl
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.lifetime.SequentialLifetimes
import com.jetbrains.rider.model.solutionAnalysisModel
import com.jetbrains.rider.projectView.solution


class IntelligentCommentsExtensionsRegistrar(project: Project) {
  private var currentContent: Content? = null
  private val tabLifetimes = SequentialLifetimes(project.lifetime)


  init {
    fun updateProblemsView(showOurTab: Boolean) {
      val manager = ToolWindowManager.getInstance(project)

      manager.invokeLater {
        val toolWindow = manager.getToolWindow("Problems View")
        val contentManager = toolWindow?.contentManager as? ContentManagerImpl

        if (showOurTab) {
          if (currentContent != null) return@invokeLater
          val model = project.solution.solutionAnalysisModel
          val lifetimeDef = tabLifetimes.next()
          val tab = IntelligentCommentProblemsViewTab(project, model, lifetimeDef.lifetime)
          val content = ContentImpl(tab, "Intelligent Comments", false).apply {
            this.isCloseable = false
          }

          currentContent = content
          contentManager?.addContent(content)
        } else {
          val copyOfCurrentContent = currentContent ?: return@invokeLater
          contentManager?.removeContent(copyOfCurrentContent, true)
          currentContent = null
          tabLifetimes.terminateCurrent()
        }
      }
    }

    val useExperimentalFeatures = RiderIntelligentCommentsSettingsProvider.getInstance().useExperimentalFeatures
    updateProblemsView(useExperimentalFeatures.value)
    useExperimentalFeatures.advise(project.lifetime) {
      updateProblemsView(it)
    }
  }
}