package com.intelligentComments.ui.comments.model.content.table

import com.intelligentComments.core.domain.core.TableContentSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableContentSegmentUiModel(
  project: Project,
  segment: TableContentSegment
) : ContentSegmentUiModel(project, segment) {
  val rows = segment.rows.map { TableRowSegmentUiModel(it, project) }
  val header = TableNameUiModel(segment.header, project)

  override fun hashCode(): Int = HashUtil.hashCode(header.hashCode(), HashUtil.calculateHashFor(rows))
  override fun equals(other: Any?): Boolean = other is TableContentSegmentUiModel && other.hashCode() == hashCode()
}