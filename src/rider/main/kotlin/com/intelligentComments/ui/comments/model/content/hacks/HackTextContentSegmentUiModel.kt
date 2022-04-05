package com.intelligentComments.ui.comments.model.content.hacks

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.HackTextSegmentRenderer
import com.intelligentComments.ui.comments.renderers.segments.ToDoTextSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class HackTextContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  toDoTextSegment: HackTextContentSegment,
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, listOf(object : UniqueEntityImpl(), TextContentSegment {
    override val highlightedText: HighlightedText = toDoTextSegment.text
    override val parent: Parentable = toDoTextSegment
  }))

  val header = HighlightedTextUiWrapper(project, parent, HighlightedTextImpl("Hack:", toDoTextSegment))

  override fun createRenderer(): Renderer {
    return HackTextSegmentRenderer(this)
  }

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash(), header.calculateStateHash())
  }
}