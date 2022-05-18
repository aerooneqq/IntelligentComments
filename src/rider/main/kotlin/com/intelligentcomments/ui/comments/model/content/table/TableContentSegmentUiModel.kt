package com.intelligentcomments.ui.comments.model.content.table

import com.intelligentcomments.core.domain.core.TableContentSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.renderers.segments.TableSegmentRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
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

  override fun dumpModel() = "${super.dumpModel()}::${headerUiModel?.dumpModel()}: \n{\n${rows.joinToString("\n") { it.dumpModel() }}\n}"
  override fun createRenderer() = TableSegmentRenderer(this)
}