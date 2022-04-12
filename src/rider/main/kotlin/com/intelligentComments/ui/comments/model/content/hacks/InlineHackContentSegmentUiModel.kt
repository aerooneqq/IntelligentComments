package com.intelligentComments.ui.comments.model.content.hacks

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.HackTextSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class InlineHackContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  segment: InlineHackContentSegment,
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, listOf(object : UniqueEntityImpl(), TextContentSegment {
    override val highlightedText: HighlightedText = segment.text
    override val parent: Parentable = segment
  }))

  val header = HighlightedTextUiWrapper(project, parent, HighlightedTextImpl("Hack:", segment))

  override fun createRenderer(): Renderer {
    return HackTextSegmentRenderer(this)
  }

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash(), header.calculateStateHash())
  }
}