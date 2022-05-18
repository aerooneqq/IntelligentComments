package com.intelligentcomments.ui.comments.model.content.table

import com.intelligentcomments.core.domain.core.TableCell
import com.intelligentcomments.ui.colors.ColorName
import com.intelligentcomments.ui.colors.Colors
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
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


  override fun dumpModel(): String = "${super.dumpModel()}::${properties}: \n{\n${contentSegments.dumpModel()}\n}"
  override fun calculateStateHash() = HashUtil.hashCode(contentSegments.calculateStateHash(), properties.hashCode())
  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}