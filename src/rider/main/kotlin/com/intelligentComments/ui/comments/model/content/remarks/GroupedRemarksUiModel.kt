package com.intelligentComments.ui.comments.model.content.remarks

import com.intelligentComments.core.domain.impl.GroupedRemarksSegments
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intellij.openapi.project.Project


private const val remarksSectionName = "Remarks"
class GroupedRemarksUiModel(
  project: Project,
  model: GroupedRemarksSegments
) : GroupedContentUiModel(
  project,
  model,
  model.segments.map { it.content },
  getFirstLevelHeader(
    project,
    remarksSectionName,
    Colors.TextInSectionsRectanglesHeadersColor,
    Colors.RemarksSectionHeaderBackgroundColor
  )
)