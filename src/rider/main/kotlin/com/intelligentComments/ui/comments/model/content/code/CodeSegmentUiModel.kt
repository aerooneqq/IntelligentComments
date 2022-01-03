package com.intelligentComments.ui.comments.model.content.code

import com.intelligentComments.core.domain.core.CodeSegment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intellij.openapi.project.Project

class CodeSegmentUiModel(
  project: Project,
  private val codeSegment: CodeSegment
) : ContentSegmentUiModel(project, codeSegment) {
  val code: HighlightedTextUiWrapper
    get() = HighlightedTextUiWrapper(project, codeSegment.code.value)
}