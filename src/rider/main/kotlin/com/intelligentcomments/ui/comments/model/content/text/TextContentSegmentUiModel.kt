package com.intelligentcomments.ui.comments.model.content.text

import com.intelligentcomments.core.domain.core.TextContentSegment
import com.intelligentcomments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.segments.TextSegmentRenderer
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TextContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  textSegment: TextContentSegment
) : ContentSegmentUiModel(project, parent) {
  val highlightedTextWrapper = HighlightedTextUiWrapper(project, this, textSegment.highlightedText.apply {
    ensureThatAllLinesAreNoLongerThan(RiderIntelligentCommentsSettingsProvider.getInstance().maxCharsInLine.value)
  })


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(highlightedTextWrapper.calculateStateHash())
  }

  override fun createRenderer(): Renderer {
    return TextSegmentRenderer(this)
  }
}