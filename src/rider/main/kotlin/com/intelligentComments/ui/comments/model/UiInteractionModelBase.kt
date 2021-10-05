package com.intelligentComments.ui.comments.model

import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.awt.Color

open class UiInteractionModelBase(val project: Project) {
    private val colorsProvider = project.service<ColorsProvider>()

    protected var myMouseIn: Boolean = false
    val mouseIn
        get() = myMouseIn

    protected open val backgroundColorKey: ColorName = Colors.EmptyColor
    protected open val hoveredBackgroundColorKey: ColorName = Colors.EmptyColor

    open val backgroundColor: Color
        get() {
            return if (myMouseIn) {
                colorsProvider.getColorFor(backgroundColorKey)
            } else {
                colorsProvider.getColorFor(hoveredBackgroundColorKey)
            }
        }


    open fun handleMouseIn(): Boolean {
        myMouseIn = true
        return true
    }

    open fun handleMouseOut(): Boolean {
        myMouseIn = false
        return true
    }

    open fun handleClick(): Boolean {
        return true
    }
}

interface ExpandableUiModel {
    var isExpanded: Boolean
}