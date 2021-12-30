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
import com.intellij.openapi.editor.impl.EditorImpl
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
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  ): Rectangle {
    var adjustedRect = Rectangle(rect).apply {
      y += heightDelta
    }

    adjustedRect = renderTickets(g, adjustedRect, editorImpl, rectanglesModel).apply {
      y += heightDelta
    }

    return renderToDoDescription(g, adjustedRect, editorImpl, rectanglesModel)
  }

  private fun renderTickets(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  ): Rectangle {
    val textWidth = TextUtil.getTextWidth(editorImpl, ticketsHeader)
    TextUtil.renderText(g, rect, editorImpl, ticketsHeader, heightDelta)
    var adjustedRect = Rectangle(rect).apply { x += textWidth + deltaBetweenTicketsHeaderAndTickets }

    for (i in todo.tickets.indices) {
      adjustedRect = renderTicket(g, adjustedRect, editorImpl, todo.tickets[i], i != todo.tickets.size - 1)
    }

    return Rectangle(rect).apply {
      y += TextUtil.getTextHeight(editorImpl, null)
    }
  }

  private fun renderTicket(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    ticket: TicketUiModel,
    addComma: Boolean
  ): Rectangle {
    val text = ticket.nameText.text
    val highlighters = ticket.nameText.highlighters
    TextUtil.renderLine(g, rect, editorImpl, text, highlighters, 0)
    var width = TextUtil.getTextWidthWithHighlighters(editorImpl, ticket.nameText)
    val adjustedRect = Rectangle(rect).apply { x += width }

    if (addComma) {
      val commaAndSpace = ", "
      TextUtil.renderLine(g, adjustedRect, editorImpl, commaAndSpace, listOf(), 0)
      width += TextUtil.getTextWidth(editorImpl, commaAndSpace)
    }

    return Rectangle(rect).apply { x += width }
  }

  private fun renderToDoDescription(
    g: Graphics,
    rect: Rectangle,
    editorImpl: EditorImpl,
    rectanglesModel: RectanglesModel
  ): Rectangle {
    return ContentSegmentsUtil.renderSegments(todo.description.content, g, rect, editorImpl, rectanglesModel)
  }

  override fun calculateContentHeight(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int {
    val ticketsHeaderHeight = calculateTicketsHeight(editorImpl) + heightDelta
    val contentHeight = ContentSegmentsUtil.calculateContentHeight(todo.description.content, editorImpl, additionalRenderInfo) + heightDelta
    return ticketsHeaderHeight + contentHeight
  }

  private fun calculateTicketsHeight(editorImpl: EditorImpl): Int {
    return TextUtil.getTextHeight(editorImpl, null)
  }

  override fun calculateContentWidth(editorImpl: EditorImpl, additionalRenderInfo: RenderAdditionalInfo): Int {
    return ContentSegmentsUtil.calculateContentWidth(todo.description.content, editorImpl, additionalRenderInfo)
  }

  override fun acceptContent(context: RectangleModelBuildContext) {
    val currentRect = acceptForTickets(context)

    for (segment in todo.description.content) {
      val renderer = SegmentRenderer.getRendererFor(segment)
      val rect = Rectangle(currentRect).apply {
        width = renderer.calculateExpectedWidthInPixels(context.editorImpl, context.additionalRenderInfo)
        height = renderer.calculateExpectedHeightInPixels(context.editorImpl, context.additionalRenderInfo)
      }

      context.rectanglesModel.addElement(segment, rect)
      renderer.accept(context.withRectangle(rect))
      currentRect.y += rect.height + ContentSegmentsUtil.deltaBetweenSegments
    }
  }

  private fun acceptForTickets(context: RectangleModelBuildContext): Rectangle {
    val rect = Rectangle(context.rect).apply { y += heightDelta }
    val editorImpl = context.editorImpl
    val ticketsHeight = calculateTicketsHeight(context.editorImpl)
    var xDelta = TextUtil.getTextWidth(context.editorImpl, ticketsHeader) + deltaBetweenTicketsHeaderAndTickets

    val tickets = todo.tickets
    val commaAndSpaceWidth = TextUtil.getTextWidth(editorImpl, ", ")
    for (i in tickets.indices) {
      val width = TextUtil.getTextWidthWithHighlighters(editorImpl, tickets[i].nameText)
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