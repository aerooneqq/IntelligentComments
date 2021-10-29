package com.intelligentComments.ui.comments.renderers

import com.intelligentComments.ui.util.CommentsUtil
import com.intelligentComments.ui.util.CommentsUtil.Companion.heightDeltaBetweenSections
import com.intelligentComments.ui.comments.model.AuthorUiModel
import com.intelligentComments.ui.core.RectangleModelBuildContext
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.RectanglesModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl
import java.awt.Graphics
import java.awt.Rectangle
import kotlin.math.max

interface CommentAuthorsRenderer : Renderer, RectangleModelBuildContributor {
    companion object {
        fun getRendererFor(authors: Collection<AuthorUiModel>): CommentAuthorsRenderer {
            return CommentAuthorsRendererImpl(authors)
        }
    }
}

class CommentAuthorsRendererImpl(private val authors: Collection<AuthorUiModel>) : CommentAuthorsRenderer {
    companion object {
        private const val lastAuthorText = "Last author: "
    }

    private var myCachedText: String? = null

    init {
        if (authors.isEmpty()) throw IllegalStateException("Authors must not be empty")
    }

    override fun render(g: Graphics,
                        rect: Rectangle,
                        editorImpl: EditorImpl,
                        rectanglesModel: RectanglesModel): Rectangle {
        return CommentsUtil.renderText(g, rect, editorImpl, getText(), heightDeltaBetweenSections)
    }

    private fun getText(): String {
        var cachedText = myCachedText
        if (cachedText != null) return cachedText

        cachedText = if (authors.isEmpty()) {
            "$lastAuthorText[Current]"
        } else {
            val lastAuthorInfo = authors.minByOrNull { it.date }!!
            "$lastAuthorText${lastAuthorInfo.name} ${lastAuthorInfo.date}"
        }

        myCachedText = cachedText
        return cachedText
    }

    override fun calculateExpectedHeightInPixels(editorImpl: EditorImpl): Int {
        return CommentsUtil.getTextHeight(editorImpl, null)
    }

    override fun calculateExpectedWidthInPixels(editorImpl: EditorImpl): Int {
        return CommentsUtil.getTextWidth(editorImpl, getText())
    }

    override fun accept(context: RectangleModelBuildContext) {
        val width = calculateExpectedWidthInPixels(context.editorImpl)
        val height = calculateExpectedHeightInPixels(context.editorImpl)
        val interactionModel = authors.first()
        val rect = Rectangle(context.rect.x, context.rect.y, width, height)

        context.rectanglesModel.addElement(interactionModel, rect)
        context.widthAndHeight.height += height
        context.widthAndHeight.width = max(context.widthAndHeight.width, width)
        context.rect.y += height
    }
}