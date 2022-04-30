package com.intelligentComments.ui.comments.model.content.exceptions

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.renderers.segments.GroupedExceptionsRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

class GroupedExceptionUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedContentSegment<ExceptionSegment>
) : GroupedContentUiModel(
  project,
  parent,
  model,
  model.segments.map {
    object : ContentSegments {
      override val segments: Collection<ContentSegment> = listOf(it)
      override val parent: Parentable = model
    }
  },
  getFirstLevelHeader(
    project,
    exceptionsSectionName,
    model
  )
) {
  override fun createRenderer(): Renderer {
    return GroupedExceptionsRenderer(this)
  }
}

private const val exceptionsSectionName = "Exceptions"