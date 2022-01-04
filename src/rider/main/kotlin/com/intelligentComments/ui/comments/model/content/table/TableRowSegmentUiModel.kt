package com.intelligentComments.ui.comments.model.content.table

import com.intelligentComments.core.domain.core.TableRow
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableRowSegmentUiModel(
  row: TableRow,
  parent: UiInteractionModelBase?,
  project: Project
) : UiInteractionModelBase(project, parent) {
  val cells = row.cells.map { TableCellUiModel(it, this, project) }


  override fun calculateStateHash(): Int {
    return HashUtil.calculateHashFor(cells) { it.calculateStateHash() }
  }
}