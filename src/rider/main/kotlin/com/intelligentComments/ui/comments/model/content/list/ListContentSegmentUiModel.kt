package com.intelligentComments.ui.comments.model.content.list

import com.intelligentComments.core.domain.core.ListContentSegment
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ListContentSegmentUiModel(
  project: Project,
  listSegment: ListContentSegment
) : ContentSegmentUiModel(project, listSegment), ExpandableUiModel {
  val header = ListContentSegmentHeaderUiModel(project, listSegment.header, this)
  val contentSegments = listSegment.content.map { ContentSegmentsUiModel(project, it) }

  override var isExpanded = true

  override fun hashCode(): Int =
    HashUtil.hashCode(header.hashCode(), HashUtil.calculateHashFor(contentSegments), isExpanded.hashCode())

  override fun equals(other: Any?): Boolean = other is ListContentSegmentUiModel && other.hashCode() == hashCode()
}