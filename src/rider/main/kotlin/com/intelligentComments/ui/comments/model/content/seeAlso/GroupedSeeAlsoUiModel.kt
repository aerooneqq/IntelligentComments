package com.intelligentComments.ui.comments.model.content.seeAlso

import com.intelligentComments.core.domain.core.GroupedContentSegment
import com.intelligentComments.core.domain.core.SeeAlsoSegment
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.comments.model.content.GroupedContentWithTextUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intellij.openapi.project.Project

class GroupedSeeAlsoUiModel(
  project: Project,
  groupedSeeAlso: GroupedContentSegment<SeeAlsoSegment>
) : GroupedContentWithTextUiModel(
  project,
  groupedSeeAlso,
  groupedSeeAlso.segments.map { it.description },
  getFirstLevelHeader(
    project,
    seeAlsoText,
    Colors.TextInSectionsRectanglesHeadersColor,
    Colors.SeeAlsoBackgroundColor,
    groupedSeeAlso
  )
)