package com.intelligentComments.ui.comments.model.content.`return`

import com.intelligentComments.core.domain.core.GroupedContentSegment
import com.intelligentComments.core.domain.core.ReturnSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentComments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentComments.ui.comments.renderers.segments.GroupedReturnsRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

class GroupedReturnUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  groupedReturns: GroupedContentSegment<ReturnSegment>
) : GroupedContentUiModel(
  project,
  parent,
  groupedReturns,
  groupedReturns.segments.map { it.content },
  getFirstLevelHeader(
    project,
    returnSectionName,
    groupedReturns
  )
) {
  override fun createRenderer(): Renderer {
    return GroupedReturnsRenderer(this)
  }
}