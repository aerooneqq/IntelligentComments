package com.intelligentComments.ui.comments.model.content.todo

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.HighlightedTextImpl
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.comments.renderers.segments.ToDoTextSegmentRenderer
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class ToDoTextContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  toDoTextSegment: ToDoTextContentSegment,
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, listOf(object : UniqueEntityImpl(), TextContentSegment {
    override val highlightedText: HighlightedText = toDoTextSegment.text
    override val parent: Parentable = toDoTextSegment
  }))

  val header = HighlightedTextUiWrapper(project, parent, HighlightedTextImpl("Todo:", toDoTextSegment))

  override fun createRenderer(): Renderer {
    return ToDoTextSegmentRenderer(this)
  }

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(content.calculateStateHash(), header.calculateStateHash())
  }
}