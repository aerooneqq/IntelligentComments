package com.intelligentComments.ui.comments.model.content.list

import com.intelligentComments.core.domain.core.ListContentSegment
import com.intelligentComments.core.domain.core.ListItem
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ListContentSegmentUiModel(
  project: Project,
  listSegment: ListContentSegment
) : ContentSegmentUiModel(project, listSegment), ExpandableUiModel {
  private val header = listSegment.header
  val headerUiModel = if (header == null) null else {
    ListContentSegmentHeaderUiModel(project, header, this)
  }

  val items = listSegment.content.map { ListItemUiModel(project, it) }

  override var isExpanded = true

  override fun hashCode(): Int =
    HashUtil.hashCode(header.hashCode(), HashUtil.calculateHashFor(items), isExpanded.hashCode())

  override fun equals(other: Any?): Boolean = other is ListContentSegmentUiModel && other.hashCode() == hashCode()
}

class ListItemUiModel(project: Project, listItem: ListItem) {
  val header = if (listItem.header == null) null else ContentSegmentsUiModel(project, listItem.header)
  val description = if (listItem.description == null) null else ContentSegmentsUiModel(project, listItem.description)

  override fun hashCode(): Int {
    var hash = header?.hashCode() ?: 1
    hash *= description?.hashCode() ?: 1

    return hash
  }

  override fun equals(other: Any?): Boolean {
    return other is ListItemUiModel && other.hashCode() == hashCode()
  }
}