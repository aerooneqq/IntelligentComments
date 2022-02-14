package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.ContentSegment
import com.intelligentComments.core.domain.core.ContentSegments
import com.intelligentComments.ui.comments.model.ModelWithContent
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.project.Project

class ContentSegmentsUiModel : UiInteractionModelBase, ModelWithContent {
  override val contentSection: SectionUiModel<ContentSegmentUiModel>


  constructor(
    project: Project,
    parent: UiInteractionModelBase?,
    content: ContentSegments
  ) : super(project, parent) {
    val content = content.segments.map { ContentSegmentUiModel.getFrom(project, this, it) }
    contentSection = SectionUiModel(project, parent, content)
  }

  constructor(
    project: Project,
    parent: UiInteractionModelBase?,
    content: Collection<ContentSegment>
  ) : super(project, parent) {
    val content = content.map { ContentSegmentUiModel.getFrom(project, this, it) }
    contentSection = SectionUiModel(project, parent, content)
  }


  override fun calculateStateHash(): Int {
    return contentSection.calculateStateHash()
  }

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}