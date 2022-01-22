package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.CommentWithOneTextSegmentUiModel
import com.intelligentComments.ui.util.SectionModelUtil
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Graphics
import java.awt.Rectangle

class CommentWithOneTextSegmentRenderer(private val model: CommentWithOneTextSegmentUiModel) : RendererWithRectangleModel(model) {

  override fun paintInternal(
    editorImpl: EditorImpl,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes,
    colorsProvider: ColorsProvider
  ) {
    val rectanglesModel = revalidateRectanglesModel(editorImpl)
    SectionModelUtil.renderSection(g, targetRegion, editorImpl, rectanglesModel, model.contentSection)
  }
}