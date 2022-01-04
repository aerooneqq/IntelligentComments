package com.intelligentComments.ui.comments.model.content.summary

import com.intelligentComments.core.domain.core.GroupedContentSegment
import com.intelligentComments.core.domain.core.SummaryContentSegment
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intellij.openapi.project.Project

class GroupedSummaryUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  model: GroupedContentSegment<SummaryContentSegment>
) : GroupedContentUiModel(
  project,
  parent,
  model,
  model.segments.map { it.content },
  getFirstLevelHeader(
    project,
    summarySectionName,
    Colors.TextInSectionsRectanglesHeadersColor,
    Colors.SummarySectionsHeaderBackgroundColor,
    model
  )
)

private const val summarySectionName = "Summary"