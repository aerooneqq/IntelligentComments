package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.intelligentComments.core.domain.core.Invariant
import com.intelligentComments.core.domain.core.TextInvariant
import com.jetbrains.rd.ide.model.RdInvariant
import com.jetbrains.rd.ide.model.RdTextInvariant

open class InvariantFromRd(private val invariant: RdInvariant) : UniqueEntityImpl(), Invariant {
    companion object {
        fun getFrom(invariant: RdInvariant) : InvariantFromRd {
            return when(invariant) {
                is RdTextInvariant -> TextInvariantFromRd(invariant)
                else -> throw IllegalArgumentException(invariant.toString())
            }
        }
    }
}

class TextInvariantFromRd(private val invariant: RdTextInvariant) : InvariantFromRd(invariant), TextInvariant {
    override val text: String = invariant.text
}