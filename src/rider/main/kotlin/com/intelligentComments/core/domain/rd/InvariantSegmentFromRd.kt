package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdInvariant
import com.jetbrains.rd.ide.model.RdTextInvariant

open class InvariantSegmentFromRd(
  rdInvariant: RdInvariant,
  parent: Parentable?
) : ContentSegmentFromRd(rdInvariant, parent), InvariantSegment {
  companion object {
    fun getFrom(invariant: RdInvariant, project: Project, parent: Parentable?): InvariantSegmentFromRd {
      return when (invariant) {
        is RdTextInvariant -> TextInvariantFromRdSegment(invariant, parent, project)
        else -> throw IllegalArgumentException(invariant.toString())
      }
    }
  }
}

class TextInvariantFromRdSegment(
  invariant: RdTextInvariant,
  parent: Parentable?,
  project: Project
) : InvariantSegmentFromRd(invariant, parent), TextInvariantSegment {
  override val description: EntityWithContentSegments = EntityWithContentSegmentsFromRd(invariant.description, this, project)
  override val name: HighlightedText = invariant.name.toIdeaHighlightedText(project, this)
}