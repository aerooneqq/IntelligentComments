package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.CommentUiModelBase
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.RectanglesModelHolder
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.RenderAdditionalInfo
import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intelligentComments.ui.util.UpdatedRectCookie
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.*
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
  val baseModel: CommentUiModelBase
) : EditorCustomElementRenderer, CustomFoldRegionRenderer, Renderer {
  private var rectModelXDelta = 0
  private var rectModelYDelta = 0

  private val rectangleModelHolder = RectanglesModelHolder(baseModel)
  private var myXDelta = -1
  private var cachedGutterMark: DocCommentSwitchRenderModeGutterMark? = null

  val gutterMark
    get() = cachedGutterMark

  open val xDelta: Int
    get() {
      var currentDelta = myXDelta
      if (currentDelta != -1) return currentDelta

      val document = baseModel.editor.document
      val lineNumber = document.getLineNumber(baseModel.comment.identifier.rangeMarker.startOffset)
      currentDelta = if (lineNumber < document.lineCount) {
        val lineStartOffset = document.getLineStartOffset(lineNumber)
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

  fun invalidateRectangleModel(editor: Editor) {
    revalidateRectanglesModel(editor)
  }

  final override fun calcHeightInPixels(foldRegion: CustomFoldRegion): Int {
    application.assertIsDispatchThread()
    return calculateExpectedHeight(foldRegion.editor)
  }

  final override fun calcWidthInPixels(foldRegion: CustomFoldRegion): Int {
    application.assertIsDispatchThread()
    return calculateExpectedWidth(foldRegion.editor)
  }

  final override fun calcWidthInPixels(inlay: Inlay<*>): Int {
    application.assertIsDispatchThread()
    return calculateExpectedWidth(inlay.editor)
  }

  private fun calculateExpectedWidth(editor: Editor) = revalidateRectanglesModel(editor).width

  final override fun calcHeightInPixels(inlay: Inlay<*>): Int {
    application.assertIsDispatchThread()
    return calculateExpectedHeight(inlay.editor)
  }

  private fun calculateExpectedHeight(editor: Editor) = revalidateRectanglesModel(editor).height

  protected fun revalidateRectanglesModel(editor: Editor): RectanglesModel {
    val buildResult = rectangleModelHolder.revalidate(editor, xDelta, yDelta)

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
    doPaint(inlay.editor, g, targetRegion, textAttributes)
  }

  final override fun paint(
    region: CustomFoldRegion,
    g: Graphics2D,
    targetRegion: Rectangle2D,
    textAttributes: TextAttributes
  ) {
    doPaint(region.editor, g, targetRegion, textAttributes)
  }

  fun paint(
    editor: Editor,
    g: Graphics2D,
    targetRegion: Rectangle2D,
    textAttributes: TextAttributes
  ) = doPaint(editor, g, targetRegion, textAttributes)

  private fun doPaint(
    editor: Editor,
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
    editor: Editor,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes,
    colorsProvider: ColorsProvider
  )

  override fun calcGutterIconRenderer(region: CustomFoldRegion) = doCalculateGutterIconRenderer(region.editor)
  override fun calcGutterIconRenderer(inlay: Inlay<*>) = doCalculateGutterIconRenderer(inlay.editor)

  private fun doCalculateGutterIconRenderer(editor: Editor): GutterIconRenderer? {
    var gutter = cachedGutterMark
    if (gutter != null) {
      return gutter
    }

    val project = editor.project ?: return null
    gutter = DocCommentSwitchRenderModeGutterMark(baseModel.comment, editor, project)
    cachedGutterMark = gutter
    return gutter
  }

  override fun calculateExpectedHeightInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    application.assertIsDispatchThread()
    return calculateExpectedWidth(editor)
  }

  override fun calculateExpectedWidthInPixels(editor: Editor, additionalRenderInfo: RenderAdditionalInfo): Int {
    application.assertIsDispatchThread()
    return calculateExpectedHeight(editor)
  }

  override fun render(
    g: Graphics,
    rect: Rectangle,
    editor: Editor,
    rectanglesModel: RectanglesModel,
    additionalRenderInfo: RenderAdditionalInfo
  ): Rectangle {
    doPaint(editor, g, rect, TextAttributes())
    return rect
  }

  override fun accept(context: RectangleModelBuildContext) {
  }
}