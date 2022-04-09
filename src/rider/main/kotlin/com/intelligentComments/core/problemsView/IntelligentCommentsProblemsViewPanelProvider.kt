package com.intelligentComments.core.problemsView

import com.intelligentComments.core.problemsView.tree.*
import com.intellij.analysis.problemsView.toolWindow.ProblemsViewPanelProvider
import com.intellij.analysis.problemsView.toolWindow.ProblemsViewTab
import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rider.RiderProjectExtensionsConfigurator
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
  init {
    if (!project.isDefault) {
      //xDDDDDDDDDDD
      val unused = project.getComponent(RiderProjectExtensionsConfigurator::class.java)
      val provider = IntelligentCommentsProblemsViewPanelProvider(project)
      project.extensionArea.getExtensionPoint(ProblemsViewPanelProvider.EP).registerExtension(provider)
    }
  }
}

class IntelligentCommentsProblemsViewPanelProvider(private val project: Project) : ProblemsViewPanelProvider {
  override fun create(): ProblemsViewTab {
    val model = project.solution.solutionAnalysisModel
    val lifetime = project.lifetime.createNested()

    return IntelligentCommentProblemsViewTab(project, model, lifetime)
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

    firstComponent = JBScrollPane(tree)
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

    val path = tree.getPathForRow(row)
    val clickedModel = path.lastPathComponent
    action(path)
  }
}