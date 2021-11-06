package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.`return`.ReturnUiModel
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

class ReturnSegmentRenderer(private val model: ReturnUiModel) : LeftHeaderRightContentRenderer(model.content) {
    override fun calculateHeaderWidth(editorImpl: EditorImpl): Int {
        return TextUtil.getTextWidthWithHighlighters(editorImpl, model.headerText)
    }

    override fun calculateHeaderHeight(editorImpl: EditorImpl): Int {
        return TextUtil.getLineHeightWithHighlighters(editorImpl, model.headerText.highlighters) + 2
    }

    override fun renderHeader(g: Graphics, rect: Rectangle, editorImpl: EditorImpl, rectanglesModel: RectanglesModel) {
        TextUtil.renderLine(g, rect, editorImpl, model.headerText, 0)
    }
}