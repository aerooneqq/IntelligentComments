package com.intelligentcomments.ui.comments.model.content.exceptions

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.renderers.segments.GroupedExceptionsRenderer
import com.intelligentcomments.ui.core.Renderer
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