package com.intelligentComments.ui.comments.model.content.table

import com.intelligentComments.core.domain.core.HighlightedText
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TableNameUiModel(header: HighlightedText, project: Project) : UiInteractionModelBase(project) {
  val highlightedTextUiWrapper = HighlightedTextUiWrapper(project, header)

  override fun hashCode(): Int = HashUtil.hashCode(highlightedTextUiWrapper.hashCode())
  override fun equals(other: Any?): Boolean = other is TableNameUiModel && other.hashCode() == hashCode()
}