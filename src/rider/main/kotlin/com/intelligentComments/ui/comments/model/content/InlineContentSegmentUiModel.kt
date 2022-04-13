package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.InlineContentSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class InlineContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment: InlineContentSegment,
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, listOf(object : UniqueEntityImpl(), TextContentSegment {
    override val highlightedText: HighlightedText = segment.text
    override val parent: Parentable = segment
  }))

  private val headerText = when (segment.nameKind) {
    NameKind.Hack -> "Hack: "
    NameKind.Invariant -> "Invariant: "
    NameKind.Todo -> "Todo: "
  }

  val header = HighlightedTextUiWrapper(project, parent, HighlightedTextImpl(headerText, segment))

  override fun createRenderer(): Renderer {
    return InlineContentSegmentRenderer(this)
  }

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash(), header.calculateStateHash())
  }
}