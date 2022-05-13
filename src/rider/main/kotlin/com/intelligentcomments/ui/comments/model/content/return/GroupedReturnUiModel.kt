package com.intelligentcomments.ui.comments.model.content.`return`

import com.intelligentcomments.core.domain.core.GroupedContentSegment
import com.intelligentcomments.core.domain.core.ReturnSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.GroupedContentUiModel
import com.intelligentcomments.ui.comments.model.content.getFirstLevelHeader
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentcomments.ui.core.Renderer
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
    return LeftTextHeaderAndRightContentRenderer(header, content)
  }
}