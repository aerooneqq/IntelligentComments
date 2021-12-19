package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoLinkUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoMemberUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoUiModel
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

abstract class SeeAlsoSegmentRenderer(
  private val seeAlsoUiModel: SeeAlsoUiModel
) : LeftHeaderRightContentRenderer(listOf(seeAlsoUiModel.description)) {
  companion object {
    fun getFor(seeAlsoUiModel: SeeAlsoUiModel): SeeAlsoSegmentRenderer {
      return when(seeAlsoUiModel) {
        is SeeAlsoLinkUiModel -> SeeAlsoLinkSegmentRenderer(seeAlsoUiModel)
        is SeeAlsoMemberUiModel -> SeeAlsoMemberSegmentRenderer(seeAlsoUiModel)
        else -> throw IllegalArgumentException(seeAlsoUiModel.javaClass.name)
      }
    }
  }

  open override fun calculateHeaderWidth(editorImpl: EditorImpl): Int {
    return TextUtil.getTextWidthWithHighlighters(editorImpl, seeAlsoUiModel.header)
  }

  open override fun calculateHeaderHeight(editorImpl: EditorImpl): Int {
    return TextUtil.getLineHeightWithHighlighters(editorImpl, seeAlsoUiModel.header.highlighters)
  }

  open override fun renderHeader(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  ) {
    TextUtil.renderLine(g, rect, editorImpl, seeAlsoUiModel.header, 0)
  }
}

class SeeAlsoLinkSegmentRenderer(seeAlso: SeeAlsoLinkUiModel) : SeeAlsoSegmentRenderer(seeAlso)
class SeeAlsoMemberSegmentRenderer(seeAlso: SeeAlsoMemberUiModel) : SeeAlsoSegmentRenderer(seeAlso)