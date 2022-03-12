package com.intelligentComments.core.problemsView.tree

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.ui.ColoredTreeCellRenderer
import com.jetbrains.rider.icons.IconHost
import icons.ReSharperIcons
import javax.swing.JTree

class CellRenderer(private val project: Project) : ColoredTreeCellRenderer() {
  private val iconHost = IconHost.getInstance(project)


  override fun customizeCellRenderer(
    tree: JTree,
    value: Any?,
    selected: Boolean,
    expanded: Boolean,
    isLeaf: Boolean,
    rowIndex: Int,
    focused: Boolean
  ) {
    when(value) {
      is IntelligentCommentErrorTreeModel -> {
        icon = AllIcons.General.Error
        append(value.presentationText)
      }

      is FileTreeModel -> {
        val rdIcon = value.icon
        icon = if (rdIcon != null) iconHost.toIdeaIcon(value.icon) else AllIcons.FileTypes.Any_type
        append(value.fileName)
      }

      is IntelligentCommentsTreeModel -> {
        icon = ReSharperIcons.ProjectModel.SolutionFolder
        append(project.name)
      }
    }
  }
}