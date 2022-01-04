package com.intelligentComments.ui.comments.model.content.code

import com.intelligentComments.core.domain.core.CodeSegment
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class CodeSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  private val codeSegment: CodeSegment
) : ContentSegmentUiModel(project, parent, codeSegment) {
  val code: HighlightedTextUiWrapper
    get() = HighlightedTextUiWrapper(project, this, codeSegment.code.value)

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(code.calculateStateHash())
  }
}