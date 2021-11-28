package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.RectanglesModelHolder
import com.intelligentComments.ui.util.TextUtil
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intelligentComments.ui.util.UpdatedRectCookie
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.CustomFoldRegion
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.use
import com.jetbrains.rd.platform.util.application
import java.awt.Graphics
import java.awt.Rectangle

abstract class RendererWithRectangleModel(model: UiInteractionModelBase) : EditorCustomElementRenderer,
  CustomFoldRegionRenderer {
  private val rectangleModelHolder = RectanglesModelHolder(model)

  val rectanglesModel
    get() = rectangleModelHolder.model

  abstract val xDelta: Int
  abstract val yDelta: Int


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

  private fun calculateExpectedWith(editorImpl: EditorImpl) = getOrCreateRectanglesModel(editorImpl).width

  final override fun calcHeightInPixels(inlay: Inlay<*>): Int {
    application.assertIsDispatchThread()
    return calculateExpectedHeight(inlay.editor as EditorImpl)
  }

  private fun calculateExpectedHeight(editorImpl: EditorImpl) = getOrCreateRectanglesModel(editorImpl).height

  protected fun getOrCreateRectanglesModel(editorImpl: EditorImpl): RectanglesModel {
    return rectangleModelHolder.revalidate(editorImpl, xDelta, yDelta)
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
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes
  ) {
    doPaint(region.editor as? EditorImpl ?: return, g, targetRegion, textAttributes)
  }

  private fun doPaint(
    editor: EditorImpl,
    g: Graphics,
    targetRegion: Rectangle,
    textAttributes: TextAttributes
  ) {
    application.assertIsDispatchThread()

    val project = editor.project ?: return
    val colorsProvider = project.service<ColorsProvider>()
    val defaultTextColor = colorsProvider.getColorFor(Colors.TextDefaultColor)

    UpdatedRectCookie(targetRegion, xDelta = xDelta, yDelta = yDelta).use {
      UpdatedGraphicsCookie(g, defaultTextColor, TextUtil.font).use {
        paintInternal(editor, g, targetRegion, textAttributes, colorsProvider)
      }
    }

    UpdatedGraphicsCookie(g, defaultTextColor, TextUtil.font).use {
      for (rectangle in rectanglesModel!!.allRectangles) {
        g.drawRect(rectangle.x + targetRegion.x, rectangle.y + targetRegion.y, rectangle.width, rectangle.height)
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

  override fun calcGutterIconRenderer(region: CustomFoldRegion): GutterIconRenderer? = doCalculateGutterIconRenderer(region.editor as EditorImpl)
  override fun calcGutterIconRenderer(inlay: Inlay<*>): GutterIconRenderer? = doCalculateGutterIconRenderer(inlay.editor as EditorImpl)

  protected abstract fun doCalculateGutterIconRenderer(editorImpl: EditorImpl): GutterIconRenderer?
}