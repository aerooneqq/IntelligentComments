package com.intelligentcomments.ui.comments.model.content.table

import com.intelligentcomments.core.domain.core.TableRow
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableRowSegmentUiModel(
  row: TableRow,
  parent: UiInteractionModelBase?,
  project: Project
) : UiInteractionModelBase(project, parent) {
  val cells = row.cells.map { TableCellUiModel(it, this, project) }


  override fun dumpModel() = "${super.dumpModel()}: \n{\n${cells.joinToString("\n") { it.dumpModel() }}\n}"
  override fun calculateStateHash() = HashUtil.calculateHashFor(cells) { it.calculateStateHash() }
  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}