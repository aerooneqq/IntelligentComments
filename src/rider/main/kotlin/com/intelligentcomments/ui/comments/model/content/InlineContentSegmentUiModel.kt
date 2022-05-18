package com.intelligentcomments.ui.comments.model.content

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.domain.impl.HighlightedTextImpl
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.segments.LeftTextHeaderAndRightContentRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class InlineContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  private val segment: InlineContentSegment,
) : ContentSegmentUiModel(project, parent) {
  val content = ContentSegmentsUiModel(project, this, listOf(object : UniqueEntityImpl(), TextContentSegment {
    override val highlightedText: HighlightedText = segment.text
    override val parent: Parentable = segment
  }))


  val header = HighlightedTextUiWrapper(project, parent, createHeaderText())


  private fun createHeaderText(): HighlightedText {
    val text = segment.nameKind.toPresentation()
    val name = segment.name
    val finalText = if (name == null) {
      "$text:"
    } else {
      "$text (${name.text}):"
    }

    val highlighter = CommonsHighlightersFactory.tryCreateCommentHighlighter(segment, finalText.length)
    return HighlightedTextImpl(finalText, segment, highlighter)
  }

  override fun dumpModel(): String = "${super.dumpModel()}::${content.dumpModel()}::${header.dumpModel()}"

  override fun createRenderer(): Renderer = LeftTextHeaderAndRightContentRenderer(header, content)

  override fun calculateStateHash(): Int = HashUtil.hashCode(content.calculateStateHash(), header.calculateStateHash())
}