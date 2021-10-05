package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.CommentsUtil
import com.intelligentComments.ui.CommentsUtil.Companion.deltaBetweenHeaderAndContent
import com.intelligentComments.ui.CommentsUtil.Companion.deltaBetweenIconAndTextInHeader
import com.intelligentComments.ui.UpdatedGraphicsCookie
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.comments.model.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

abstract class VerticalSectionWithHeaderRenderer<T : UiInteractionModelBase>(private val section: SectionWithHeaderUiModel<T>)
    : Renderer, RectangleModelBuildContributor {

    val text = section.headerUiModel.headerText

    private val icon = section.headerUiModel.icon
    private val shouldRenderContent: Boolean
        get() = section.isExpanded

    final override fun render(g: Graphics,
                              rect: Rectangle,
                              editorImpl: EditorImpl,
                              rectanglesModel: RectanglesModel): Rectangle {
        val textColorKey = if (section.headerUiModel.mouseIn) Colors.TextDefaultHoveredColor else Colors.TextDefaultColor
        val textColor = section.project.service<ColorsProvider>().getColorFor(textColorKey)
        val deltaAfterHeader = if (shouldRenderContent) deltaBetweenHeaderAndContent else 0

        var adjustedRect = rect
        UpdatedGraphicsCookie(g, textColor).use {
            adjustedRect = CommentsUtil.renderTextWithIcon(g, adjustedRect, editorImpl, text, icon, 2, deltaAfterHeader)
        }

        adjustedRect = if (!shouldRenderContent) {
            adjustedRect
        } else {
            renderContent(g, adjustedRect, editorImpl, rectanglesModel)
        }

        CommentsUtil.addDeltaBetweenSections(adjustedRect)
        return adjustedRect
    }

    protected abstract fun renderContent(g: Graphics,
                                         rect: Rectangle,
                                         editorImpl: EditorImpl,
                                         rectanglesModel: RectanglesModel): Rectangle

    final override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        val height = CommentsUtil.calculateTextHeightWithIcon(editorImpl, icon, text)
        return if (shouldRenderContent) height + calculateContentHeight(editorImpl) else height
    }

    protected abstract fun calculateContentWidth(editorImpl: EditorImpl): Int

    final override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        val width = CommentsUtil.calculateWidthOfTextWithIcon(editorImpl, icon, deltaBetweenIconAndTextInHeader, text)
        return if (shouldRenderContent) max(width, calculateContentWidth(editorImpl)) else width
    }

    protected abstract fun calculateContentHeight(editorImpl: EditorImpl): Int

    final override fun accept(context: RectangleModelBuildContext) {
        val rect = context.rect
        val icon = section.headerUiModel.icon
        val headerText = section.headerUiModel.headerText

        val headerRect = Rectangle(rect).apply {
            height = CommentsUtil.calculateTextHeightWithIcon(context.editorImpl, icon, headerText)
            width = CommentsUtil.calculateWidthOfTextWithIcon(context.editorImpl, icon, deltaBetweenIconAndTextInHeader, headerText)
        }

        val sectionHeaderModel = section.headerUiModel
        context.rectanglesModel.addElement(sectionHeaderModel, headerRect)

        CommentsUtil.addHeightDeltaTo(context, headerRect.height)

        if (shouldRenderContent) {
            acceptContent(context)
        }
    }

    protected abstract fun acceptContent(context: RectangleModelBuildContext)
}