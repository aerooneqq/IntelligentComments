package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.DocCommentUiModel
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Graphics
import java.awt.Rectangle

class DocCommentRenderer(private val model: DocCommentUiModel) : RendererWithRectangleModel(model) {
    override val xDelta: Int = 0
    override val yDelta: Int = 0


    override fun paintInternal(
        inlay: Inlay<*>,
        g: Graphics,
        targetRegion: Rectangle,
        textAttributes: TextAttributes,
        colorsProvider: ColorsProvider
    ) {
        val editorImpl = inlay.editor as? EditorImpl ?: return
        drawCommentContent(g, targetRegion, editorImpl)
    }

    private fun drawCommentContent(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
        val model = getOrCreateRectanglesModel(editorImpl)
        val renderer = SegmentsRenderer.getRendererFor(this.model.contentSection)
        return renderer.render(g, rect, editorImpl, model)
    }
}