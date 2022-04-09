package com.intelligentComments.core.namesToolWindow

import com.intelligentComments.core.comments.navigation.CommentsNavigationHost
import com.intelligentComments.core.domain.core.NameKind
import com.intelligentComments.core.domain.core.NamedEntityReference
import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.intelligentComments.core.namesToolWindow.tree.FileTreeModel
import com.intelligentComments.core.namesToolWindow.tree.NameCellRenderer
import com.intelligentComments.core.namesToolWindow.tree.NameTreeModel
import com.intelligentComments.core.namesToolWindow.tree.NamesTree
import com.intelligentComments.core.problemsView.handleDoubleClick
import com.intellij.openapi.components.service
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
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import com.jetbrains.rider.projectView.solution
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTree

class NamedEntityToolWindowFactory() : ToolWindowFactory {
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val invariantComponent = NamedEntitiesComponent(project, NameKind.Invariant)
    val todoComponent = NamedEntitiesComponent(project, NameKind.Todo)
    val hacksComponent = NamedEntitiesComponent(project, NameKind.Hack)

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

class NamedEntitiesComponent(project: Project, private val nameKind: NameKind) : OnePixelSplitter() {
  private val navigationHost = project.service<CommentsNavigationHost>()
  private val textControlHost = FrontendTextControlHost.getInstance(project)
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
    val kind = nameKind
    tree.addMouseListener(object : MouseAdapter() {
      override fun mouseClicked(e: MouseEvent?) {
        handleDoubleClick(e, tree) { path ->
          val clickedModel = path.lastPathComponent
          if (clickedModel is NameTreeModel) {
            val offset = clickedModel.entity.documentOffset
            if (offset == null) {
              val editor = textControlHost.tryGetLastFocusedEditor() ?: return@handleDoubleClick
              val name = clickedModel.entity.name
              val reference = object : UniqueEntityImpl(), NamedEntityReference {
                override val nameKind: NameKind = kind
                override val name: String = name
                override val rawValue: String = name
              }

              navigationHost.performNavigation(reference, editor)
            } else {
              if (path.pathCount < 2) return@handleDoubleClick

              val secondComponent = path.getPathComponent(1)
              if (secondComponent is FileTreeModel) {
                val sourceFileId = secondComponent.rdModel.file.sourceFileId
                navigationHost.performNavigation(sourceFileId, offset)
              }
            }
          }
        }
      }
    })

    firstComponent = JBScrollPane(tree)
  }


  fun updateTree(model: RdFileNames) {
    treeModel.addOrUpdate(FileTreeModel(model))
  }
}