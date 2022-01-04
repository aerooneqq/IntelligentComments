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
  private var previousHash: Int? = null
  private var cachedText: HighlightedTextUiWrapper? = null

  val code: HighlightedTextUiWrapper
    get() {
      val hash = codeSegment.code.value.hashCode()
      if (previousHash == null || previousHash != hash) {
        val text = HighlightedTextUiWrapper(project, this, codeSegment.code.value)
        previousHash = hash
        cachedText = text
        return text
      }

      return cachedText!!
    }

  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(code.calculateStateHash())
  }
}