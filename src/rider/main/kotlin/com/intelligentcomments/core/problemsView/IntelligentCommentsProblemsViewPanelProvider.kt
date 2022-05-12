package com.intelligentcomments.core.problemsView

import com.intelligentcomments.core.namesToolWindow.createProblemsViewLikeComponent
import com.intelligentcomments.core.namesToolWindow.setNodeExpandedState
import com.intelligentcomments.core.problemsView.tree.*
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.analysis.problemsView.toolWindow.ProblemsViewTab
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.content.Content
import com.intellij.ui.content.impl.ContentImpl
import com.intellij.ui.content.impl.ContentManagerImpl
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.lifetime.SequentialLifetimes
import com.jetbrains.rider.model.SolutionAnalysisModel
import com.jetbrains.rider.model.SolutionAnalysisNavigation
import com.jetbrains.rider.model.solutionAnalysisModel
import com.jetbrains.rider.projectView.solution
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTree
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.tree.TreePath


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

class IntelligentCommentProblemsViewTab(
  private val project: Project,
  model: SolutionAnalysisModel,
  lifetime: Lifetime
) : OnePixelSplitter(), ProblemsViewTab, TreeExpansionListener {
  private val treeModel: IntelligentCommentsTreeModel
  private val tree: JTree

  init {
    treeModel = IntelligentCommentsTreeModel(model, lifetime)
    tree = object : JTree(treeModel) {
      override fun convertValueToText(
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
      ): String {
        return when (value) {
          is FileTreeModel -> value.fileName
          is IntelligentCommentReferenceErrorTreeModel -> value.presentationText
          is IntelligentCommentsTreeModel -> project.name
          else -> "UNDEFINED"
        }
      }
    }

    tree.addMouseListener(object : MouseAdapter() {
      override fun mousePressed(e: MouseEvent?) {
        handleDoubleClick(e, tree) { clickedModel ->
          if (clickedModel is IntelligentCommentErrorTreeModel) {
            val navigationRequest = SolutionAnalysisNavigation(clickedModel.originalError.offset, true)
            clickedModel.parentModel.rdModel.navigateToError.fire(navigationRequest)
          }
        }
      }
    })

    tree.cellRenderer = CellRenderer(project)
    tree.addTreeExpansionListener(this)

    fun handleExpansionOrCollapse(expand: Boolean, event: AnActionEvent) {
      setNodeExpandedState(tree.model.root, TreePath(tree.model.root), tree, expand)
    }

    firstComponent = createProblemsViewLikeComponent(tree) { expand, event -> handleExpansionOrCollapse(expand, event) }
  }

  override fun getName(count: Int): String {
    return "Intelligent Comments"
  }

  override fun getTabId(): String {
    return this.javaClass.name
  }

  override fun treeExpanded(event: TreeExpansionEvent?) {
    handleTreeExpansionOrCollapse(event, true)
  }

  private fun handleTreeExpansionOrCollapse(event: TreeExpansionEvent?, isExpanded: Boolean) {
    val model = event?.path?.lastPathComponent ?: return
    if (model is FileTreeModel) {
      model.setRequiresUpdate(isExpanded)
    }
  }

  override fun treeCollapsed(event: TreeExpansionEvent?) {
    handleTreeExpansionOrCollapse(event, false)
  }
}

fun handleDoubleClick(e: MouseEvent?, tree: JTree, action: (TreePath) -> Unit) {
  if (e == null) return

  if (e.clickCount == 2) {
    val row = tree.getClosestRowForLocation(e.x, e.y)
    if (row == -1) return

    action(tree.getPathForRow(row))
  }
}