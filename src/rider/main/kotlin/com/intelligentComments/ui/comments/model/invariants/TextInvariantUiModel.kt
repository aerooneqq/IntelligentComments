package com.intelligentComments.ui.comments.model.invariants

import com.intelligentComments.core.domain.core.TextInvariant
import com.intelligentComments.ui.colors.ColorName
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

class TextInvariantUiModel(project: Project,
                           textInvariant: TextInvariant) : InvariantUiModel(project) {
    override val backgroundColorKey: ColorName = Colors.TextInvariantBackgroundColor
    override val hoveredBackgroundColorKey: ColorName = Colors.TextInvariantHoveredBackgroundColor

    val text = textInvariant.text

    override fun hashCode(): Int = text.hashCode() % HashUtil.mod
    override fun equals(other: Any?): Boolean = other is TextInvariantUiModel && other.hashCode() == hashCode()
}