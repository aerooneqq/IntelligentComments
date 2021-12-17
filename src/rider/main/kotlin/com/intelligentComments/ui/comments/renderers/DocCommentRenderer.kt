package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.DocCommentUiModel
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.util.text.CharArrayUtil
import java.awt.Graphics
import java.awt.Rectangle

class DocCommentRenderer(val model: DocCommentUiModel) : RendererWithRectangleModel(model) {
  private var myXDelta = -1
  override val xDelta: Int
    get() {
      var currentDelta = myXDelta
      if (currentDelta != -1) return currentDelta

      val document = model.editor.document
      val nextLineNumber = document.getLineNumber(model.underlyingTextRange.endOffset) + 1
      currentDelta = if (nextLineNumber < document.lineCount) {
        val lineStartOffset = document.getLineStartOffset(nextLineNumber)
        val contentStartOffset = CharArrayUtil.shiftForward(document.immutableCharSequence, lineStartOffset, " \t\n")
        model.editor.offsetToXY(contentStartOffset, false, true).x
      } else {
        model.editor.insets.left
      }

      myXDelta = currentDelta
      return currentDelta
    }


  override val yDelta: Int = 0


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
    return renderer.render(g, rect, editorImpl, model)
  }

  override fun doCalculateGutterIconRenderer(editorImpl: EditorImpl): GutterIconRenderer? {
    val project = editorImpl.project ?: return null
    val highlighter = model.docComment.highlighter
    var existingGutter = highlighter.gutterIconRenderer
    if (existingGutter != null) {
      return existingGutter
    }

    existingGutter = DocCommentSwitchRenderModeGutterMark(model.docComment, editorImpl, project)
    highlighter.gutterIconRenderer = existingGutter
    return existingGutter
  }
}

