package com.intelligentComments.ui.comments.renderers.authors

import com.intelligentComments.ui.comments.model.authors.AuthorUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.RectanglesModelUtil.Companion.heightDeltaBetweenSections
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

interface CommentAuthorsRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    fun getRendererFor(authors: Collection<AuthorUiModel>): CommentAuthorsRenderer {
      return CommentAuthorsRendererImpl(authors)
    }
  }
}

class CommentAuthorsRendererImpl(private val authors: Collection<AuthorUiModel>) : CommentAuthorsRenderer {
  companion object {
    private const val lastAuthorText = "Last author: "
  }

  private var myCachedText: String? = null

  init {
    if (authors.isEmpty()) throw IllegalStateException("Authors must not be empty")
  }

  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    return TextUtil.renderText(g, rect, editor, getText(), heightDeltaBetweenSections)
  }

  private fun getText(): String {
    var cachedText = myCachedText
    if (cachedText != null) return cachedText

    cachedText = if (authors.isEmpty()) {
      "$lastAuthorText[Current]"
    } else {
      val lastAuthorInfo = authors.minByOrNull { it.date }!!
      "$lastAuthorText${lastAuthorInfo.name} ${lastAuthorInfo.date}"
    }

    myCachedText = cachedText
    return cachedText
  }

  override fun calculateExpectedHeightInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return TextUtil.getTextHeight(editor, null)
  }

  override fun calculateExpectedWidthInPixels(
    editor: Editor,
    additionalRenderInfo: RenderAdditionalInfo
  ): Int {
    return TextUtil.getTextWidth(editor, getText())
  }

  override fun accept(context: RectangleModelBuildContext) {
    val width = calculateExpectedWidthInPixels(context.editor, context.additionalRenderInfo)
    val height = calculateExpectedHeightInPixels(context.editor, context.additionalRenderInfo)
    val interactionModel = authors.first()
    val rect = Rectangle(context.rect.x, context.rect.y, width, height)

    context.rectanglesModel.addElement(interactionModel, rect)
    context.widthAndHeight.updateHeightSum(height)
    context.widthAndHeight.updateWidthMax(width)
    context.rect.y += height
  }
}