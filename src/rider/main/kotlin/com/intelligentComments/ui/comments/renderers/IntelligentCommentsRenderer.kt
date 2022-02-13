package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.IntelligentCommentUiModel
import com.intelligentComments.ui.comments.renderers.authors.CommentAuthorsRenderer
import com.intelligentComments.ui.comments.renderers.invariants.InvariantsRenderer
import com.intelligentComments.ui.comments.renderers.references.ReferencesRenderer
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intelligentComments.ui.comments.renderers.todos.ToDosRenderer
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.use
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Rectangle

class IntelligentCommentsRenderer(private val model: IntelligentCommentUiModel) : RendererWithRectangleModel(model) {
  companion object {
    private const val leftLineWidth = 2
    private const val deltaBetweenLeftLineAndContent = 10

    private val borderDeltas = Dimension(0, 0)
  }

  override val xDelta = deltaBetweenLeftLineAndContent + borderDeltas.width
  override val yDelta = borderDeltas.height


  override fun paintInternal(
    editor: Editor,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes,
    colorsProvider: ColorsProvider
  ) {
    var adjustedRect = adjustContentRect(targetRegion)
    val leftLineBackgroundColor = colorsProvider.getColorFor(Colors.LeftLineBackgroundColor)
    UpdatedGraphicsCookie(g, color = leftLineBackgroundColor).use {
      adjustedRect = drawLeftLine(g, adjustedRect)
    }

    adjustedRect = drawCommentAuthors(g, adjustedRect, editor)
    adjustedRect = drawCommentContent(g, adjustedRect, editor)
    adjustedRect = drawReferences(g, adjustedRect, editor)
    adjustedRect = drawInvariants(g, adjustedRect, editor)
    adjustedRect = drawToDos(g, adjustedRect, editor)
  }

  private fun adjustContentRect(rect: Rectangle): Rectangle {
    val w = borderDeltas.width
    val h = borderDeltas.height

    return Rectangle(rect.x + w, rect.y + h, rect.width - w, rect.height - h)
  }

  private fun drawLeftLine(g: Graphics, rect: Rectangle): Rectangle {
    g.fillRoundRect(rect.x, rect.y, leftLineWidth, rect.height, 2, 2)
    return Rectangle(
      rect.x + deltaBetweenLeftLineAndContent,
      rect.y,
      rect.width - deltaBetweenLeftLineAndContent,
      rect.height
    )
  }

  private fun drawCommentAuthors(g: Graphics, rect: Rectangle, editor: Editor): Rectangle {
    val renderer = CommentAuthorsRenderer.getRendererFor(model.authorsSection.content)
    val model = revalidateRectanglesModel(editor)
    return renderer.render(g, rect, editor, model, RenderAdditionalInfo.emptyInstance)
  }

  private fun drawCommentContent(g: Graphics, rect: Rectangle, editor: Editor): Rectangle {
    val model = revalidateRectanglesModel(editor)
    val renderer = SegmentsRenderer.getRendererFor(this.model.contentSection)
    return renderer.render(g, rect, editor, model, RenderAdditionalInfo.emptyInstance)
  }

  private fun drawReferences(g: Graphics, rect: Rectangle, editor: Editor): Rectangle {
    val renderer = ReferencesRenderer.getRendererFor(model.referencesSection)
    val model = revalidateRectanglesModel(editor)
    return renderer.render(g, rect, editor, model, RenderAdditionalInfo.emptyInstance)
  }

  private fun drawInvariants(g: Graphics, rect: Rectangle, editor: Editor): Rectangle {
    val renderer = InvariantsRenderer.getRendererFor(model.invariantsSection)
    val model = revalidateRectanglesModel(editor)
    return renderer.render(g, rect, editor, model, RenderAdditionalInfo.emptyInstance)
  }

  private fun drawToDos(g: Graphics, rect: Rectangle, editor: Editor): Rectangle {
    val renderer = ToDosRenderer.getRendererFor(model.todosSection)
    val model = revalidateRectanglesModel(editor)
    return renderer.render(g, rect, editor, model, RenderAdditionalInfo.emptyInstance)
  }
}