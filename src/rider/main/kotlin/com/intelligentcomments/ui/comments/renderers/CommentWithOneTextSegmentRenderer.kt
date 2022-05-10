package com.intelligentcomments.ui.comments.renderers

import com.intelligentcomments.ui.colors.ColorsProvider
import com.intelligentcomments.ui.comments.model.CommentWithOneTextSegmentUiModel
import com.intelligentcomments.ui.util.SectionModelUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Graphics
import java.awt.Rectangle

class CommentWithOneTextSegmentRenderer(
  private val model: CommentWithOneTextSegmentUiModel
) : RendererWithRectangleModel(model) {

  override fun paintInternal(
    editor: Editor,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes,
    colorsProvider: ColorsProvider
  ) {
    val rectanglesModel = revalidateRectanglesModel(editor)
    SectionModelUtil.renderSection(g, targetRegion, editor, rectanglesModel, model.contentSection)
  }
}