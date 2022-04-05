package com.intelligentComments.ui.comments.model.content.hacks

import com.intelligentComments.core.domain.impl.GroupedHacksSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.renderers.segments.GroupedHacksRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project


private const val hackSectionsName = "Hacks"
class GroupedHackUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment: GroupedHacksSegment
) : GroupedContentUiModel(
  project,
  parent,
  segment,
  segment.segments.map { it.content.content },
  getFirstLevelHeader(
    project,
    hackSectionsName,
    segment
  )
) {
  override fun createRenderer(): Renderer {
    return GroupedHacksRenderer(this)
  }
}