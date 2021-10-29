package com.intelligentComments.ui.comments.model.content.table

import com.intelligentComments.core.domain.core.TableRow
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableRowSegmentUiModel(row: TableRow, project: Project) : UiInteractionModelBase(project) {
    val cells = row.cells.map { TableCellUiModel(it, project) }

    override fun hashCode(): Int = HashUtil.calculateHashFor(cells)
    override fun equals(other: Any?): Boolean = other is TableRowSegmentUiModel && other.hashCode() == hashCode()
}