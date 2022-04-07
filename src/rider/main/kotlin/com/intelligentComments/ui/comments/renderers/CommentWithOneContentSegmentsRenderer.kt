package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.CommentWithOneContentSegmentsUiModel
import com.intelligentComments.ui.util.SectionModelUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Graphics
import java.awt.Rectangle

class CommentWithOneContentSegmentsRenderer(
  private val model: CommentWithOneContentSegmentsUiModel
) : RendererWithRectangleModel(model) {
  override fun paintInternal(
    editor: Editor,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes,
    colorsProvider: ColorsProvider
  ) {
    val rectangleModel = revalidateRectanglesModel(editor)
    SectionModelUtil.renderSection(g, targetRegion, editor, rectangleModel, model.contentSection)
  }
}