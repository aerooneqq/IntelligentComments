package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.CommentUiModelBase
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.RectanglesModelHolder
import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intelligentComments.ui.util.UpdatedRectCookie
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.use
import com.intellij.util.application
import com.intellij.util.text.CharArrayUtil
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.geom.Rectangle2D

abstract class RendererWithRectangleModel(
  private val baseModel: CommentUiModelBase
) : EditorCustomElementRenderer, CustomFoldRegionRenderer {
  private var rectModelXDelta = 0
  private var rectModelYDelta = 0

  private val rectangleModelHolder = RectanglesModelHolder(baseModel)
  private var myXDelta = -1

  open val xDelta: Int
    get() {
      var currentDelta = myXDelta
      if (currentDelta != -1) return currentDelta

      val document = baseModel.editor.document
      val nextLineNumber = document.getLineNumber(baseModel.comment.rangeMarker.endOffset) + 1
      currentDelta = if (nextLineNumber < document.lineCount) {
        val lineStartOffset = document.getLineStartOffset(nextLineNumber)
        val contentStartOffset = CharArrayUtil.shiftForward(document.immutableCharSequence, lineStartOffset, " \t\n")
        baseModel.editor.offsetToXY(contentStartOffset, false, true).x
      } else {
        baseModel.editor.insets.left
      }

      myXDelta = currentDelta
      return currentDelta
    }

  open val yDelta: Int = 0

  val rectanglesModel
    get() = rectangleModelHolder.model

  fun invalidateRectangleModel(editorImpl: EditorImpl) {
    revalidateRectanglesModel(editorImpl)
  }

  final override fun calcHeightInPixels(foldRegion: CustomFoldRegion): Int {
    application.assertIsDispatchThread()
    return calculateExpectedHeight(foldRegion.editor as EditorImpl)
  }

  final override fun calcWidthInPixels(foldRegion: CustomFoldRegion): Int {
    application.assertIsDispatchThread()
    return calculateExpectedWith(foldRegion.editor as EditorImpl)
  }

  final override fun calcWidthInPixels(inlay: Inlay<*>): Int {
    application.assertIsDispatchThread()
    return calculateExpectedWith(inlay.editor as EditorImpl)
  }

  private fun calculateExpectedWith(editorImpl: EditorImpl) = revalidateRectanglesModel(editorImpl).width

  final override fun calcHeightInPixels(inlay: Inlay<*>): Int {
    application.assertIsDispatchThread()
    return calculateExpectedHeight(inlay.editor as EditorImpl)
  }

  private fun calculateExpectedHeight(editorImpl: EditorImpl) = revalidateRectanglesModel(editorImpl).height

  protected fun revalidateRectanglesModel(editorImpl: EditorImpl): RectanglesModel {
    val buildResult = rectangleModelHolder.revalidate(editorImpl, xDelta, yDelta)

    rectModelXDelta = buildResult.xShift
    rectModelYDelta = buildResult.yShift

    return buildResult.model
  }

  final override fun paint(
    inlay: Inlay<*>,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes
  ) {
    doPaint(inlay.editor as? EditorImpl ?: return, g, targetRegion, textAttributes)
  }

  final override fun paint(
    region: CustomFoldRegion,
    g: Graphics2D,
    targetRegion: Rectangle2D,
    textAttributes: TextAttributes
  ) {
    doPaint(region.editor as? EditorImpl ?: return, g, targetRegion, textAttributes)
  }

  private fun doPaint(
    editor: EditorImpl,
    g: Graphics,
    targetRegion: Rectangle2D,
    textAttributes: TextAttributes
  ) {
    application.assertIsDispatchThread()

    val project = editor.project ?: return
    val colorsProvider = project.service<ColorsProvider>()
    val defaultTextColor = colorsProvider.getColorFor(Colors.TextDefaultColor)

    val rect = targetRegion.bounds
    UpdatedRectCookie(rect, xDelta = xDelta + rectModelXDelta, yDelta = yDelta + rectModelYDelta).use {
      UpdatedGraphicsCookie(g, defaultTextColor, TextUtil.getFont(editor)).use {
        paintInternal(editor, g, rect, textAttributes, colorsProvider)
      }
    }

    UpdatedGraphicsCookie(g, defaultTextColor, TextUtil.getFont(editor)).use {
      for (rectangle in rectanglesModel!!.allRectangles) {
        //g.drawRect(rectangle.x + targetRegion.x, rectangle.y + targetRegion.y, rectangle.width, rectangle.height)
      }
    }
  }

  protected abstract fun paintInternal(
    editorImpl: EditorImpl,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes,
    colorsProvider: ColorsProvider
  )

  override fun calcGutterIconRenderer(region: CustomFoldRegion) = doCalculateGutterIconRenderer(region.editor as EditorImpl)
  override fun calcGutterIconRenderer(inlay: Inlay<*>) = doCalculateGutterIconRenderer(inlay.editor as EditorImpl)

  private fun doCalculateGutterIconRenderer(editorImpl: EditorImpl): GutterIconRenderer? {
    val project = editorImpl.project ?: return null
    return DocCommentSwitchRenderModeGutterMark(baseModel.comment, editorImpl, project)
  }
}