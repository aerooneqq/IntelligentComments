package com.intelligentComments.ui.comments.renderers.invariants

import com.intelligentComments.ui.comments.model.InvariantUiModel
import com.intelligentComments.ui.comments.model.TextInvariantUiModel
import com.intelligentComments.ui.core.Renderer
import com.intellij.openapi.editor.impl.EditorImpl

interface InvariantRenderer : Renderer {
    companion object {
        fun getRendererFor(invariant: InvariantUiModel): InvariantRenderer {
            return when(invariant) {
                is TextInvariantUiModel -> TextDefaultInvariantRenderer(invariant)
                else -> throw IllegalArgumentException(invariant.toString())
            }
        }
    }

    fun calculateWidthWithInvariantInterval(editorImpl: EditorImpl): Int
}

