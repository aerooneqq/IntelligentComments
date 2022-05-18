package com.intelligentcomments.ui.comments.model.content.invariants

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.ui.comments.model.ModelWithContentSegments
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.segments.TextRendererBase
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
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

  override fun dumpModel() = "${super.dumpModel()}::${name.dumpModel()}: \n{\n${content.dumpModel()}\n}"
  override fun calculateStateHash() = HashUtil.hashCode(name.calculateStateHash(), content.calculateStateHash())
  override fun createRenderer(): Renderer = TextRendererBase(name)
}