package com.intelligentcomments.ui.comments.renderers

import com.intelligentcomments.ui.colors.ColorsProvider
import com.intelligentcomments.ui.comments.model.CollapsedCommentUiModel
import com.intelligentcomments.ui.util.SectionModelUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Graphics
import java.awt.Rectangle

class CollapsedCommentRenderer(
  private val commentUiModel: CollapsedCommentUiModel
) : RendererWithRectangleModel(commentUiModel) {

  override fun paintInternal(
    editor: Editor,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes,
    colorsProvider: ColorsProvider
  ) {
    val rectangleModel = revalidateRectanglesModel(editor)
    SectionModelUtil.renderSection(g, targetRegion, editor, rectangleModel, commentUiModel.contentSection)
  }
}