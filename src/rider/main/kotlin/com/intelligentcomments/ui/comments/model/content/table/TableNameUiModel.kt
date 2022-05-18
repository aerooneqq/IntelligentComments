package com.intelligentcomments.ui.comments.model.content.table

import com.intelligentcomments.core.domain.core.HighlightedText
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentcomments.ui.core.Renderer
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableNameUiModel(
  header: HighlightedText,
  parent: UiInteractionModelBase?,
  project: Project
) : UiInteractionModelBase(project, parent) {
  val highlightedTextUiWrapper = HighlightedTextUiWrapper(project, this, header)


  override fun dumpModel() = "${super.dumpModel()}::${highlightedTextUiWrapper}"
  override fun calculateStateHash() = HashUtil.hashCode(highlightedTextUiWrapper.calculateStateHash())
  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}