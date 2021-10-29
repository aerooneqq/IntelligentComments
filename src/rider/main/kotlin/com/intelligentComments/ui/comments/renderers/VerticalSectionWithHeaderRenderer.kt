package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.colors.ColorsProvider
import com.intelligentComments.ui.util.CommentsUtil
import com.intelligentComments.ui.util.CommentsUtil.Companion.deltaBetweenHeaderAndContent
import com.intelligentComments.ui.util.CommentsUtil.Companion.deltaBetweenIconAndTextInHeader
import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.UpdatedGraphicsCookie
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.util.use
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

abstract class VerticalSectionWithHeaderRenderer<T : UiInteractionModelBase>(
        private val section: SectionWithHeaderUiModel<T>) : Renderer, RectangleModelBuildContributor {

    companion object {
        const val leftContentIndent = 20
        const val leftLineIndent = 7
    }

    private val highlightedText = section.headerUiModel.headerText

    private val icon = section.headerUiModel.icon
    private val shouldRenderContent: Boolean
        get() = section.isExpanded

    final override fun render(g: Graphics,
                              rect: Rectangle,
                              editorImpl: EditorImpl,
                              rectanglesModel: RectanglesModel): Rectangle {
        val deltaAfterHeader = if (shouldRenderContent) deltaBetweenHeaderAndContent else 0
        var adjustedRect = CommentsUtil.renderTextWithIcon(g, rect, editorImpl, highlightedText, icon, 2, deltaAfterHeader)

        if (section.isExpanded) {
            drawLeftLine(g, adjustedRect, editorImpl)
        }

        adjustedRect = if (!shouldRenderContent) {
            adjustedRect
        } else {
            adjustedRect.x += leftContentIndent
            adjustedRect = renderContent(g, adjustedRect, editorImpl, rectanglesModel)
            adjustedRect.apply {
                x -= leftContentIndent
            }
        }

        CommentsUtil.addDeltaBetweenSections(adjustedRect)
        return adjustedRect
    }

    private fun drawLeftLine(g: Graphics,
                             rect: Rectangle,
                             editorImpl: EditorImpl) {
        val contentHeight = calculateContentHeight(editorImpl)
        val color = section.project.service<ColorsProvider>().getColorFor(Colors.LeftLineBackgroundColor)

        UpdatedGraphicsCookie(g, color = color).use {
            g.fillRoundRect(rect.x + leftLineIndent, rect.y, 2, contentHeight, 3, 3)
        }
    }

    protected abstract fun renderContent(g: Graphics,
                                         rect: Rectangle,
                                         editorImpl: EditorImpl,
                                         rectanglesModel: RectanglesModel): Rectangle

    final override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        val height = CommentsUtil.calculateTextHeightWithIcon(editorImpl, icon, highlightedText)
        return if (shouldRenderContent) height + calculateContentHeight(editorImpl) else height
    }

    protected abstract fun calculateContentWidth(editorImpl: EditorImpl): Int

    final override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        val width = CommentsUtil.calculateWidthOfTextWithIcon(editorImpl, icon, deltaBetweenIconAndTextInHeader, highlightedText)
        return if (shouldRenderContent) max(width, calculateContentWidth(editorImpl) + leftContentIndent) else width
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
        context.rectanglesModel.addElement(highlightedText.highlighters[0], headerRect)

        CommentsUtil.addHeightDeltaTo(context, headerRect.height)

        if (shouldRenderContent) {
            context.rect.x += leftContentIndent
            acceptContent(context)
            context.rect.x -= leftContentIndent
        }
    }

    protected abstract fun acceptContent(context: RectangleModelBuildContext)
}