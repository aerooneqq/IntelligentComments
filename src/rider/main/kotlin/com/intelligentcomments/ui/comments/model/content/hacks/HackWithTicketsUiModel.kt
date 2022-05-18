package com.intelligentcomments.ui.comments.model.content.hacks

import com.intelligentcomments.core.domain.core.HackWithTicketsContentSegment
import com.intelligentcomments.core.domain.core.NameKind
import com.intelligentcomments.ui.comments.model.ModelWithContentSegments
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.renderers.ContentSegmentsRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intellij.openapi.project.Project

class HackWithTicketsUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment : HackWithTicketsContentSegment,
) : ContentSegmentUiModel(project, parent), ModelWithContentSegments {
  override val content = generateContentSegmentsUiModelForNamedEntity(NameKind.Hack, segment, project, this)

  override fun dumpModel() = "${super.dumpModel()}: \n{\n${content.dumpModel()}\n}"
  override fun calculateStateHash() = content.calculateStateHash()
  override fun createRenderer(): Renderer = ContentSegmentsRenderer(content)
}