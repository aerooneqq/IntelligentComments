package com.intelligentComments.ui.comments.model

import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.CustomFoldRegionRenderer
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import java.awt.Color

open class UiInteractionModelBase(val project: Project) {
    protected val colorsProvider = project.service<ColorsProvider>()

    protected var myMouseIn: Boolean = false
    val mouseIn
        get() = myMouseIn

    protected open val backgroundColorKey: ColorName = Colors.EmptyColor
    protected open val hoveredBackgroundColorKey: ColorName = Colors.EmptyColor

    open val backgroundColor: Color
        get() = if (myMouseIn) {
            colorsProvider.getColorFor(hoveredBackgroundColorKey)
        } else {
            colorsProvider.getColorFor(backgroundColorKey)
        }

    open fun handleMouseIn(e: EditorMouseEvent): Boolean {
        myMouseIn = true
        return true
    }

    open fun handleMouseOut(e: EditorMouseEvent): Boolean {
        myMouseIn = false
        return true
    }

    open fun handleClick(e: EditorMouseEvent): Boolean {
        return true
    }
}

interface RootUiModel {
    fun getEditorCustomElementRenderer(project: Project): EditorCustomElementRenderer
    fun getCustomFoldRegionRenderer(project: Project): CustomFoldRegionRenderer
}

interface ExpandableUiModel {
    var isExpanded: Boolean
}