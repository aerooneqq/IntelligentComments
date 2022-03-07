package com.intelligentComments.ui.comments.model.content.table

import com.intelligentComments.core.domain.core.TableContentSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.renderers.segments.TableSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment: TableContentSegment
) : ContentSegmentUiModel(project, parent) {
  val rows = segment.rows.map { TableRowSegmentUiModel(it, this, project) }

  private val header = segment.header
  val headerUiModel = if (header == null) null else TableNameUiModel(header, this, project)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(headerUiModel?.calculateStateHash() ?: 1, HashUtil.calculateHashFor(rows) { it.calculateStateHash() })
  }

  override fun createRenderer(): Renderer {
    return TableSegmentRenderer(this)
  }
}