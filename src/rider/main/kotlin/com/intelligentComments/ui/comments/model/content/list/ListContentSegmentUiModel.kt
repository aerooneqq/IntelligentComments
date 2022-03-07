package com.intelligentComments.ui.comments.model.content.list

import com.intelligentComments.core.domain.core.ListContentSegment
import com.intelligentComments.core.domain.core.ListItem
import com.intelligentComments.ui.comments.model.ExpandableUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.comments.renderers.segments.ListSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ListContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  listSegment: ListContentSegment
) : ContentSegmentUiModel(project, parent), ExpandableUiModel {
  private val header = listSegment.header
  val headerUiModel = if (header == null) null else {
    ListContentSegmentHeaderUiModel(project, this, header)
  }

  val listKind = listSegment.listKind
  val items = listSegment.content.map { ListItemUiModel(project, this, it) }

  override var isExpanded = true

  override fun calculateStateHash(): Int {
    val headerHash = headerUiModel?.calculateStateHash() ?: 1
    return HashUtil.hashCode(headerHash, HashUtil.calculateHashFor(items) { it.calculateStateHash() }, isExpanded.hashCode())
  }

  override fun createRenderer(): Renderer {
    return ListSegmentRenderer(this)
  }
}

class ListItemUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  listItem: ListItem
) : UiInteractionModelBase(project, parent) {
  val header = if (listItem.header == null) null else ContentSegmentsUiModel(project, this, listItem.header)
  val description = if (listItem.description == null) null else ContentSegmentsUiModel(project, this, listItem.description)


  override fun calculateStateHash(): Int {
    var hash = header?.calculateStateHash() ?: 1
    hash *= description?.calculateStateHash() ?: 1

    return hash
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}