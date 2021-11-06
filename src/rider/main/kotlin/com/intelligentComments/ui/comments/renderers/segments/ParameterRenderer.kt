package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.ParameterUiModel
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

class ParameterRenderer(private val model: ParameterUiModel) : LeftHeaderRightContentRenderer(model.description) {
    override fun calculateHeaderWidth(editorImpl: EditorImpl): Int {
        return TextUtil.getTextWidthWithHighlighters(editorImpl, model.name)
    }

    override fun calculateHeaderHeight(editorImpl: EditorImpl): Int {
        return TextUtil.getLineHeightWithHighlighters(editorImpl, model.name.highlighters) + 2
    }

    override fun renderHeader(g: Graphics, rect: Rectangle, editorImpl: EditorImpl, rectanglesModel: RectanglesModel) {
        TextUtil.renderLine(g, rect, editorImpl, model.name, 0)
    }
}