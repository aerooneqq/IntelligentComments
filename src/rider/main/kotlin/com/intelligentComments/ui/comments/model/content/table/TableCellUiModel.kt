package com.intelligentComments.ui.comments.model.content.table

import com.intelligentComments.core.domain.core.TableCell
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableCellUiModel(cell: TableCell, project: Project) : UiInteractionModelBase(project) {
  val contentSegments = ContentSegmentsUiModel(project, cell.contentSegments)
  val properties = cell.properties

  override val backgroundColorKey: ColorName
    get() = if (properties.isHeader) Colors.TableHeaderCellBackground else Colors.EmptyColor

  override fun hashCode(): Int = (contentSegments.hashCode() * properties.hashCode()) % HashUtil.mod
  override fun equals(other: Any?): Boolean = other is TableCellUiModel && other.hashCode() == hashCode()
}