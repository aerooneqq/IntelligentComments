package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.DocCommentUiModel
import com.intelligentComments.ui.comments.renderers.segments.SegmentsRenderer
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.util.text.CharArrayUtil
import java.awt.Graphics
import java.awt.Rectangle
import javax.swing.Icon

class DocCommentRenderer(private val model: DocCommentUiModel) : RendererWithRectangleModel(model) {
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

  override fun calcGutterIconRenderer(inlay: Inlay<*>): GutterIconRenderer {
    return DocCommentSwitchRenderModeGutterMark(model.docComment)
  }
}

class DocCommentSwitchRenderModeGutterMark(private val docComment: DocComment) : GutterIconRenderer() {
  private val icon = AllIcons.Gutter.JavadocEdit


  override fun equals(other: Any?): Boolean {
    return other is DocCommentSwitchRenderModeGutterMark && docComment == other.docComment
  }

  override fun hashCode(): Int {
    return docComment.hashCode()
  }

  override fun getIcon(): Icon {
    return icon
  }
}