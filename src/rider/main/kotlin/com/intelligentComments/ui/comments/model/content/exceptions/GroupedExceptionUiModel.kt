package com.intelligentComments.ui.comments.model.content.exceptions

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intellij.openapi.project.Project

class GroupedExceptionUiModel(
  project: Project,
  model: GroupedContentSegment<ExceptionSegment>
) : GroupedContentUiModel(
  project,
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
    Colors.TextInSectionsRectanglesHeadersColor,
    Colors.ExceptionBackgroundColor,
    model
  )
)

private const val exceptionsSectionName = "Exceptions"