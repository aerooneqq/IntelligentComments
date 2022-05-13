package com.intelligentcomments.ui.comments.model.content.seeAlso

import com.intelligentcomments.core.domain.core.GroupedContentSegment
import com.intelligentcomments.core.domain.core.SeeAlsoSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentWithTextUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.project.Project

class GroupedSeeAlsoUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  groupedSeeAlso: GroupedContentSegment<SeeAlsoSegment>
) : GroupedContentWithTextUiModel(
  project,
  parent,
  groupedSeeAlso,
  groupedSeeAlso.segments.map { it.description },
  getFirstLevelHeader(
    project,
    seeAlsoText,
    groupedSeeAlso
  )
) {
  override fun createRenderer(): Renderer {
    return LeftTextHeaderAndRightContentRenderer(header, listOf(description))
  }
}