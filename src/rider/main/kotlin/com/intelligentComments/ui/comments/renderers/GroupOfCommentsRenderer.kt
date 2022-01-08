package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.GroupOfLineCommentsUiModel
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Graphics
import java.awt.Rectangle

class GroupOfCommentsRenderer(private val model: GroupOfLineCommentsUiModel) : RendererWithRectangleModel(model) {

  override fun paintInternal(
    editorImpl: EditorImpl,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes,
    colorsProvider: ColorsProvider
  ) {
    val rectanglesModel = getOrCreateRectanglesModel(editorImpl)
    val renderer = SegmentsRenderer.getRendererFor(model.contentSection)
    renderer.render(g, targetRegion, editorImpl, rectanglesModel, RenderAdditionalInfo.emptyInstance)
  }
}