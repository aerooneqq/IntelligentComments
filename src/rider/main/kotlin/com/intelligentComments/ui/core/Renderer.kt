package com.intelligentComments.ui.core

import com.intelligentComments.ui.WidthAndHeight
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle

interface Renderer {
    fun render(g: Graphics, rect: Rectangle, editorImpl: EditorImpl, rectanglesModel: RectanglesModel): Rectangle
    fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int
    fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int
}


data class RectangleModelBuildContext(val rectanglesModel: RectanglesModel,
                                      val widthAndHeight: WidthAndHeight,
                                      val rect: Rectangle,
                                      val editorImpl: EditorImpl)

interface RectangleModelBuildContributor {
    fun accept(context: RectangleModelBuildContext)
}