package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.DocCommentUiModel
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Graphics
import java.awt.Rectangle

class DocCommentRenderer(val model: DocCommentUiModel) : RendererWithRectangleModel(model) {

  override fun paintInternal(
    editorImpl: EditorImpl,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes,
    colorsProvider: ColorsProvider
  ) {
    drawCommentContent(g, targetRegion, editorImpl)
  }

  private fun drawCommentContent(g: Graphics, rect: Rectangle, editorImpl: EditorImpl): Rectangle {
    val model = getOrCreateRectanglesModel(editorImpl)
    val renderer = SegmentsRenderer.getRendererFor(this.model.contentSection)
    return renderer.render(g, rect, editorImpl, model, RenderAdditionalInfo.emptyInstance)
  }
}

