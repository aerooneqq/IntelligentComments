package com.intelligentcomments.core.namesToolWindow

import com.intelligentcomments.core.comments.listeners.RiderFocusedEditorsListener
import com.intelligentcomments.core.comments.navigation.CommentsNavigationHost
import com.intelligentcomments.core.domain.core.NameKind
import com.intelligentcomments.core.domain.core.NamedEntityReference
import com.intelligentcomments.core.domain.core.UniqueEntityImpl
import com.intelligentcomments.core.namesToolWindow.tree.FileTreeModel
import com.intelligentcomments.core.namesToolWindow.tree.NameCellRenderer
import com.intelligentcomments.core.namesToolWindow.tree.NameTreeModel
import com.intelligentcomments.core.namesToolWindow.tree.NamesTree
import com.intelligentcomments.core.problemsView.handleDoubleClick
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.impl.ContentImpl
import com.intellij.util.application
import com.jetbrains.rd.ide.model.RdFileNames
import com.jetbrains.rd.ide.model.RdNameKind
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.lifetime.SequentialLifetimes
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.TreePath

class NamedEntityToolWindowFactory(private val project: Project) {
  private var toolWindow: ToolWindow? = null
  private val toolWindowLifetime = SequentialLifetimes(project.lifetime)

  init {
    RiderIntelligentCommentsSettingsProvider.getInstance().useExperimentalFeatures.advise(project.lifetime) {
      if (!it) {
        toolWindow?.remove()
        toolWindow = null
        toolWindowLifetime.terminateCurrent()
      } else {
        createNewToolWindow()
      }
    }
  }

  private fun createNewToolWindow() {
    application.invokeLater {
      if (toolWindow != null) return@invokeLater
      val manager = ToolWindowManager.getInstance(project)
      manager.invokeLater {
        val invariantComponent = NamedEntitiesComponent(project, NameKind.Invariant)
        val todoComponent = NamedEntitiesComponent(project, NameKind.Todo)
        val hacksComponent = NamedEntitiesComponent(project, NameKind.Hack)
        val newToolWindow = manager.registerToolWindow("Hacks, Todos and Invariants") { }

        newToolWindow.contentManager.addContent(ContentImpl(invariantComponent, "Invariants", false))
        newToolWindow.contentManager.addContent(ContentImpl(todoComponent, "Todos", false))
        newToolWindow.contentManager.addContent(ContentImpl(hacksComponent, "Hacks", false))

        toolWindow = newToolWindow

        fun updateTree(fileNames: RdFileNames) {
          when (fileNames.nameKind) {
            RdNameKind.Hack -> {
              hacksComponent.updateTree(fileNames)
            }

            RdNameKind.Todo -> {
              todoComponent.updateTree(fileNames)
            }

            RdNameKind.Invariant -> {
              invariantComponent.updateTree(fileNames)
            }
          }
        }

        application.invokeLater {
          val host = project.getComponent(RiderCommentsNamedEntitiesHost::class.java)
          val currentEntities = host.getAllCurrentEntities()
          for (entity in currentEntities) {
            updateTree(entity)
          }

          val lifetime = toolWindowLifetime.next().lifetime
          host.fileEntitiesChanged.advise(lifetime) {
            if (it != null) {
              updateTree(it)
            }
          }
        }
      }
    }
  }
}

class NamedEntitiesComponent(project: Project, nameKind: NameKind) : OnePixelSplitter() {
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
              val editor = project.getComponent(RiderFocusedEditorsListener::class.java).lastFocusedEditor ?: return@handleDoubleClick
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

    fun handleExpansionOrCollapse(expand: Boolean, event: AnActionEvent) {
      val model = tree.model
      setNodeExpandedState(model.root, TreePath(model.root), tree, expand)
    }

    firstComponent = createProblemsViewLikeComponent(tree) { expand, event -> handleExpansionOrCollapse(expand, event) }
  }


  fun updateTree(model: RdFileNames) {
    treeModel.addOrUpdate(FileTreeModel(model))
  }

  fun clear() {
    treeModel.clear()
  }
}

fun createProblemsViewLikeComponent(tree: JTree, handleExpansionOrCollapse: (Boolean, AnActionEvent) -> Unit): JPanel {
  val actionGroup = DefaultActionGroup().apply {
    add(ExpandOrCollapseAllTreeItemsAction(true) { expand, event -> handleExpansionOrCollapse(expand, event) })
    add(ExpandOrCollapseAllTreeItemsAction(false) { expand, event -> handleExpansionOrCollapse(expand, event) })
  }

  val toolbar = ActionManager.getInstance().createActionToolbar("ProblemsViewLikeToolbar", actionGroup, false)
  val component = JPanel().apply {
    layout = BorderLayout()
    add(toolbar.component, BorderLayout.WEST)
    add(JBScrollPane(tree), BorderLayout.CENTER)
  }

  return component
}

class ExpandOrCollapseAllTreeItemsAction(
  private val expand: Boolean,
  private val handler: (Boolean, AnActionEvent) -> Unit
) : AnAction(
  if (expand) "Expand Items" else "Collapse Items",
) {
  override fun update(e: AnActionEvent) {
    e.presentation.icon = if (expand) AllIcons.General.ExpandComponent else AllIcons.General.CollapseComponent
  }

  override fun actionPerformed(event: AnActionEvent) {
    handler(expand, event)
  }
}

//todo: bad, but ok for now
fun setNodeExpandedState(node: Any, path: TreePath, tree: JTree, expand: Boolean) {
  val model = tree.model
  val childrenCount = model.getChildCount(node)
  if (childrenCount == 0) return

  val list = mutableListOf<Any>()
  for (i in 0 until childrenCount) {
    list.add(model.getChild(node, i))
  }

  for (treeNode in list) {
    setNodeExpandedState(treeNode, path.pathByAddingChild(treeNode), tree, expand)
  }

  if (!expand && node is NamesTree) {
    return
  }

  if (expand) {
    tree.expandPath(path)
  } else {
    tree.collapsePath(path)
  }
}