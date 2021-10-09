package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdContentSegment
import com.jetbrains.rd.ide.model.RdTextHighlighter
import com.jetbrains.rd.ide.model.RdTextSegment

open class ContentSegmentFromRd(private val contentSegment: RdContentSegment) : UniqueEntityImpl(), ContentSegment {
    companion object {
        fun getFrom(contentSegment: RdContentSegment, project: Project): ContentSegmentFromRd {
            return when(contentSegment) {
                is RdTextSegment -> TextContentSegmentFromRd(contentSegment, project)
                else -> throw IllegalArgumentException(contentSegment.toString())
            }
        }
    }
}

class TextContentSegmentFromRd(segment: RdTextSegment,
                               private val project: Project) : ContentSegmentFromRd(segment), TextContentSegment {
    override val text: String = segment.text.text
    override val highlighters: Collection<TextHighlighter>

    init {
        highlighters = segment.text.highlighters?.map { TextHighlighterFromRd(project, it) } ?: listOf()
    }
}