package com.intelligentComments.ui.comments.model.content.`return`

import com.intelligentComments.core.domain.core.GroupedContentSegment
import com.intelligentComments.core.domain.core.ReturnSegment
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intellij.openapi.project.Project

class GroupedReturnUiModel(
  project: Project,
  groupedReturns: GroupedContentSegment<ReturnSegment>
) : GroupedContentUiModel(
  project,
  groupedReturns,
  groupedReturns.segments.map { it.content },
  getFirstLevelHeader(
    project,
    returnSectionName,
    Colors.TextInSectionsRectanglesHeadersColor,
    Colors.ReturnBackgroundColor,
    groupedReturns
  )
)