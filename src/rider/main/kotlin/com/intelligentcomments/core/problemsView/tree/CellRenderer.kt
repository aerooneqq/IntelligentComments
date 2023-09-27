package com.intelligentcomments.core.problemsView.tree

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.ColoredTreeCellRenderer
import com.jetbrains.rd.ui.icons.ProtocolIconConverter
import com.jetbrains.rd.ui.icons.ProtocolIconRegistry
import icons.ReSharperIcons
import javax.swing.JTree

class CellRenderer(private val project: Project) : ColoredTreeCellRenderer() {
  private val iconsRegistry = project.service<ProtocolIconRegistry>();

  override fun customizeCellRenderer(
    tree: JTree,
    value: Any?,
    selected: Boolean,
    expanded: Boolean,
    isLeaf: Boolean,
    rowIndex: Int,
    focused: Boolean
  ) {
    when (value) {
      is IntelligentCommentErrorTreeModel -> {
        icon = AllIcons.General.Error
        append(value.presentationText)
      }

      is FileTreeModel -> {
        val rdIcon = value.icon
        if (rdIcon != null) {
          for (converter in ProtocolIconConverter.EP_NAME.getExtensions(null)) {
            val createdIcon = converter.createIcon(value.icon, iconsRegistry);
            if (createdIcon != null) {
              icon = createdIcon
              break;
            }
          }
        } else {
          icon = AllIcons.FileTypes.Any_type
        }

        append(value.fileName)
      }

      is IntelligentCommentsTreeModel -> {
        icon = ReSharperIcons.ProjectModel.SolutionFolder
        append(project.name)
      }
    }
  }
}