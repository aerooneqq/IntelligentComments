package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdInvariant
import com.jetbrains.rd.ide.model.RdInvariantContentSegment
import com.jetbrains.rd.ide.model.RdTextInvariant

open class InvariantFromRd(private val invariant: RdInvariant) : UniqueEntityImpl(), Invariant {
  companion object {
    fun getFrom(invariant: RdInvariant): InvariantFromRd {
      return when (invariant) {
        is RdTextInvariant -> TextInvariantFromRd(invariant)
        else -> throw IllegalArgumentException(invariant.toString())
      }
    }
  }
}

class TextInvariantFromRd(private val invariant: RdTextInvariant) : InvariantFromRd(invariant), TextInvariant {
  override val text: String = invariant.text
}

class InvariantContentSegmentFromRd(
  contentSegment: RdInvariantContentSegment,
  parent: Parentable?,
  project: Project
) : ContentSegmentFromRd(contentSegment, parent), TextInvariantContentSegment {
  override val invariant = TextInvariantFromRd(contentSegment.invariant)
}