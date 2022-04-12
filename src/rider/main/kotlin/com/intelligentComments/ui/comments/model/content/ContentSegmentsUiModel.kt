package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.ContentSegment
import com.intelligentComments.core.domain.core.ContentSegments
import com.intelligentComments.ui.comments.model.ModelWithContent
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.segments.DefaultSegmentsRenderer
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

class ContentSegmentsUiModel : ContentSegmentUiModel, ModelWithContent {
  override val contentSection: SectionUiModel


  constructor(
    project: Project,
    parent: UiInteractionModelBase?,
    content: ContentSegments
  ) : super(project, parent) {
    val models = content.segments.map { it.createUiModel(project, this) }
    contentSection = SectionUiModel(project, parent, models)
  }

  constructor(
    project: Project,
    parent: UiInteractionModelBase?,
    content: Collection<ContentSegment>
  ) : super(project, parent) {
    val models = content.map { it.createUiModel(project, this) }
    contentSection = SectionUiModel(project, parent, models)
  }


  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }

  override fun createRenderer(): Renderer = DefaultSegmentsRenderer(contentSection)
}