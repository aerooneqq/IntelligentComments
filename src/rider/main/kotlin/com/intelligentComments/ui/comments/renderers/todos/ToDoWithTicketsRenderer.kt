package com.intelligentComments.ui.comments.renderers.todos

import com.intelligentComments.ui.comments.model.tickets.TicketUiModel
import com.intelligentComments.ui.comments.model.todo.ToDoUiModel
import com.intelligentComments.ui.comments.model.todo.ToDoWithTicketsUiModel
import com.intelligentComments.ui.comments.renderers.ExpandableContentWithHeader
import com.intelligentComments.ui.comments.renderers.segments.SegmentRenderer
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.ContentSegmentsUtil
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intellij.openapi.editor.Editor
import java.awt.Graphics
import java.awt.Rectangle

interface ToDoRenderer : Renderer, RectangleModelBuildContributor {
  companion object {
    fun getRendererFor(todo: ToDoUiModel): ToDoRenderer {
      return when (todo) {
        is ToDoWithTicketsUiModel -> ToDoWithTicketsRenderer(todo)
        else -> throw IllegalArgumentException(todo.toString())
      }
    }
  }
}

class ToDoWithTicketsRenderer(private val todo: ToDoWithTicketsUiModel) :
  ExpandableContentWithHeader(todo.headerUiModel), ToDoRenderer {
  companion object {
    private const val heightDelta = 4
    private const val deltaBetweenTicketsHeaderAndTickets = 4
    private const val ticketsHeader = "Related tickets:"
  }


  override fun renderContent(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel
  ): Rectangle {
    var adjustedRect = Rectangle(rect).apply {
      y += heightDelta
    }

    adjustedRect = renderTickets(g, adjustedRect, editor, rectanglesModel).apply {
      y += heightDelta
    }

    return renderToDoDescription(g, adjustedRect, editor, rectanglesModel)
  }

  private fun renderTickets(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel
  ): Rectangle {
    val textWidth = TextUtil.getTextWidth(editor, ticketsHeader)
    TextUtil.renderText(g, rect, editor, ticketsHeader, heightDelta)
    var adjustedRect = Rectangle(rect).apply { x += textWidth + deltaBetweenTicketsHeaderAndTickets }

    for (i in todo.tickets.indices) {
      adjustedRect = renderTicket(g, adjustedRect, editor, todo.tickets[i], i != todo.tickets.size - 1)
    }

    return Rectangle(rect).apply {
      y += TextUtil.getTextHeight(editor, null)
    }
  }

  private fun renderTicket(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    ticket: TicketUiModel,
    addComma: Boolean
  ): Rectangle {
    val text = ticket.nameText.text
    val highlighters = ticket.nameText.highlighters
    TextUtil.renderLine(g, rect, editor, text, highlighters, 0)
    var width = TextUtil.getTextWidthWithHighlighters(editor, ticket.nameText)
    val adjustedRect = Rectangle(rect).apply { x += width }

    if (addComma) {
      val commaAndSpace = ", "
      TextUtil.renderLine(g, adjustedRect, editor, commaAndSpace, listOf(), 0)
      width += TextUtil.getTextWidth(editor, commaAndSpace)
    }

    return Rectangle(rect).apply { x += width }
  }

  private fun renderToDoDescription(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel
  ): Rectangle {
    return ContentSegmentsUtil.renderSegments(todo.description, g, rect, editor, rectanglesModel)
  }

  override fun calculateContentHeight(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    val ticketsHeaderHeight = calculateTicketsHeight(editor) + heightDelta
    val contentHeight = ContentSegmentsUtil.calculateContentHeight(todo.description, editor, additionalRenderInfo) + heightDelta
    return ticketsHeaderHeight + contentHeight
  }

  private fun calculateTicketsHeight(editor: Editor): Int {
    return TextUtil.getTextHeight(editor, null)
  }

  override fun calculateContentWidth(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    return ContentSegmentsUtil.calculateContentWidth(todo.description, editor, additionalRenderInfo)
  }

  override fun acceptContent(context: RectangleModelBuildContext) {
    val currentRect = acceptForTickets(context)

    for (segment in todo.description.contentSection.content) {
      val renderer = SegmentRenderer.getRendererFor(segment)
      val rect = Rectangle(currentRect).apply {
        width = renderer.calculateExpectedWidthInPixels(context.editor, context.additionalRenderInfo)
        height = renderer.calculateExpectedHeightInPixels(context.editor, context.additionalRenderInfo)
      }

      context.rectanglesModel.addElement(segment, rect)
      renderer.accept(context.withRectangle(rect))
      currentRect.y += rect.height + ContentSegmentsUtil.deltaBetweenSegments
    }
  }

  private fun acceptForTickets(context: RectangleModelBuildContext): Rectangle {
    val rect = Rectangle(context.rect).apply { y += heightDelta }
    val editor = context.editor
    val ticketsHeight = calculateTicketsHeight(context.editor)
    var xDelta = TextUtil.getTextWidth(context.editor, ticketsHeader) + deltaBetweenTicketsHeaderAndTickets

    val tickets = todo.tickets
    val commaAndSpaceWidth = TextUtil.getTextWidth(editor, ", ")
    for (i in tickets.indices) {
      val width = TextUtil.getTextWidthWithHighlighters(editor, tickets[i].nameText)
      val currentRect = Rectangle(rect).apply {
        x += xDelta
        this.width = width
        this.height = ticketsHeight
      }

      context.rectanglesModel.addElement(tickets[i].nameText.highlighters[0], currentRect)
      xDelta += width + commaAndSpaceWidth
    }

    return Rectangle(context.rect).apply {
      y += ticketsHeight + 2 * heightDelta
    }
  }
}