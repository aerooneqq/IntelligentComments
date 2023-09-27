package com.intelligentcomments.ui.comments.model.content.code

import com.intelligentcomments.core.domain.core.CodeSegment
import com.intelligentcomments.ui.comments.model.UiInteractionModelBase
import com.intelligentcomments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentcomments.ui.comments.model.highlighters.HighlightedTextUiWrapper
import com.intelligentcomments.ui.comments.renderers.segments.TextRendererBase
import com.intelligentcomments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class CodeSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  private val codeSegment: CodeSegment
) : ContentSegmentUiModel(project, parent) {
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


  override fun dumpModel() = "${super.dumpModel()}::${code.dumpModel()}"
  override fun calculateStateHash() = HashUtil.hashCode(code.calculateStateHash())
  override fun createRenderer() = TextRendererBase(code)
}