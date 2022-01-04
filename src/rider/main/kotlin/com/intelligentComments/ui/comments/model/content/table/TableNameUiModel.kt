package com.intelligentComments.ui.comments.model.content.table

import com.intelligentComments.core.domain.core.HighlightedText
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableNameUiModel(
  header: HighlightedText,
  parent: UiInteractionModelBase?,
  project: Project
) : UiInteractionModelBase(project, parent) {
  val highlightedTextUiWrapper = HighlightedTextUiWrapper(project, this, header)


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(highlightedTextUiWrapper.calculateStateHash())
  }
}