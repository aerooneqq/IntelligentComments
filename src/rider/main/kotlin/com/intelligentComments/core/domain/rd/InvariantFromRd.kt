package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdInvariant
import com.jetbrains.rd.ide.model.RdTextInvariant

open class InvariantFromRd(
  rdInvariant: RdInvariant,
  parent: Parentable?
) : ContentSegmentFromRd(rdInvariant, parent), Invariant {
  companion object {
    fun getFrom(invariant: RdInvariant, project: Project, parent: Parentable?): InvariantFromRd {
      return when (invariant) {
        is RdTextInvariant -> TextInvariantFromRd(invariant, parent, project)
        else -> throw IllegalArgumentException(invariant.toString())
      }
    }
  }
}

class TextInvariantFromRd(
  invariant: RdTextInvariant,
  parent: Parentable?,
  project: Project
) : InvariantFromRd(invariant, parent), TextInvariant {
  override val description: HighlightedText = invariant.description.toIdeaHighlightedText(project, this)
  override val name: HighlightedText = invariant.name.toIdeaHighlightedText(project, this)
}