package com.intelligentComments.core.namesToolWindow

import com.intelligentComments.core.namesToolWindow.tree.FileTreeModel
import com.intelligentComments.core.namesToolWindow.tree.NameCellRenderer
import com.intelligentComments.core.namesToolWindow.tree.NameTreeModel
import com.intelligentComments.core.namesToolWindow.tree.NamesTree
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.impl.ContentImpl
import com.jetbrains.rd.ide.model.RdFileNames
import com.jetbrains.rd.ide.model.RdNameKind
import com.jetbrains.rd.ide.model.rdCommentsModel
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rider.projectView.solution
import javax.swing.JTree

class NamedEntityToolWindowFactory() : ToolWindowFactory {
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val invariantComponent = NamedEntitiesComponent(project)
    val todoComponent = NamedEntitiesComponent(project)
    val hacksComponent = NamedEntitiesComponent(project)

    project.solution.rdCommentsModel.namedEntitiesChange.advise(project.lifetime) {
      when (it.nameKind) {
        RdNameKind.Hack -> {
          hacksComponent.updateTree(it)
        }

        RdNameKind.Todo -> {
          todoComponent.updateTree(it)
        }

        RdNameKind.Invariant -> {
          invariantComponent.updateTree(it)
        }
      }
    }

    toolWindow.contentManager.addContent(ContentImpl(invariantComponent, "Invariants", false))
    toolWindow.contentManager.addContent(ContentImpl(todoComponent, "Todos", false))
    toolWindow.contentManager.addContent(ContentImpl(hacksComponent, "Hacks", false))
  }
}

class NamedEntitiesComponent(project: Project) : OnePixelSplitter() {
  private val treeModel: NamesTree = NamesTree()

  init {
    val tree = object : JTree(treeModel) {
      override fun convertValueToText(
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
      ): String {
        return when (value) {
          is FileTreeModel -> value.rdModel.file.name
          is NameTreeModel -> value.entity.presentation
          is NamesTree -> project.name
          else -> "UNDEFINED"
        }
      }
    }

    tree.cellRenderer = NameCellRenderer(project)
    firstComponent = JBScrollPane(tree)
  }

  fun updateTree(model: RdFileNames) {
    treeModel.addOrUpdate(FileTreeModel(model))
  }
}