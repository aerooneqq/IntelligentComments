package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.ContentSegment
import com.intelligentComments.core.domain.core.TextContentSegment
import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.jetbrains.rd.ide.model.RdContentSegment
import com.jetbrains.rd.ide.model.RdTextSegment

open class ContentSegmentFromRd(private val contentSegment: RdContentSegment) : UniqueEntityImpl(), ContentSegment {
    companion object {
        fun getFrom(contentSegment: RdContentSegment): ContentSegmentFromRd {
            return when(contentSegment) {
                is RdTextSegment -> TextContentSegmentFromRd(contentSegment)
                else -> throw IllegalArgumentException(contentSegment.toString())
            }
        }
    }
}

class TextContentSegmentFromRd(private val segment: RdTextSegment) : ContentSegmentFromRd(segment), TextContentSegment {
    override val text: String? = segment.text.valueOrNull
}