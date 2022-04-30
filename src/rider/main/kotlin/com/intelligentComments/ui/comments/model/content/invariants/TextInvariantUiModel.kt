package com.intelligentComments.ui.comments.model.content.invariants

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.ModelWithContentSegments
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.invariants.InvariantRenderer
import com.intelligentComments.ui.comments.renderers.segments.invariants.TextDefaultInvariantRenderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TextInvariantUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment: TextInvariantSegment,
) : ContentSegmentUiModel(project, parent), ModelWithContentSegments {
  val name = HighlightedTextUiWrapper(project, this, segment.name ?: segment.description)

  override val content = ContentSegmentsUiModel(project, this, object : UniqueEntityImpl(), ContentSegments {
    override val segments: Collection<ContentSegment> = listOf(object : UniqueEntityImpl(), TextContentSegment {
      override val highlightedText: HighlightedText = segment.description
      override val parent = null
    })

    override val parent = null
  })

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(name.calculateStateHash(), content.calculateStateHash())
  }

  override fun createRenderer(): InvariantRenderer = TextDefaultInvariantRenderer(this)
}