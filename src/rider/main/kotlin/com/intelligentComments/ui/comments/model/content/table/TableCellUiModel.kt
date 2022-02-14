package com.intelligentComments.ui.comments.model.content.table

import com.intelligentComments.core.domain.core.TableCell
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableCellUiModel(
  cell: TableCell,
  parent: UiInteractionModelBase?,
  project: Project
) : UiInteractionModelBase(project, parent) {
  val contentSegments = ContentSegmentsUiModel(project, this, cell.contentSegments)
  val properties = cell.properties

  override val backgroundColorKey: ColorName
    get() = if (properties.isHeader) Colors.TableHeaderCellBackgroundColor else Colors.EmptyColor


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(contentSegments.calculateStateHash(), properties.hashCode())
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}