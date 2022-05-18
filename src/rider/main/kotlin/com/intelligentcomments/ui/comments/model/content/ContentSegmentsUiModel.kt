package com.intelligentcomments.ui.comments.model.content

import com.intelligentcomments.core.domain.core.ContentSegment
import com.intelligentcomments.core.domain.core.ContentSegments
import com.intelligentcomments.ui.comments.model.ModelWithContent
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.sections.SectionUiModel
import com.intelligentcomments.ui.comments.renderers.segments.DefaultSegmentsRenderer
import com.intelligentcomments.ui.core.Renderer
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


  override fun dumpModel(): String = "${super.dumpModel()}: \n{\n${contentSection.dumpModel()}\n}"
  override fun calculateStateHash(): Int = contentSection.calculateStateHash()
  override fun createRenderer(): Renderer = DefaultSegmentsRenderer(contentSection)
}