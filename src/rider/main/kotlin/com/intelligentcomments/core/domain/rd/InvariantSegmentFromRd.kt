package com.intelligentcomments.core.domain.rd

import com.intelligentcomments.core.domain.core.HighlightedText
import com.intelligentcomments.core.domain.core.InvariantSegment
import com.intelligentcomments.core.domain.core.Parentable
import com.intelligentcomments.core.domain.core.TextInvariantSegment
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
  override val description: HighlightedText = invariant.description.toIdeaHighlightedText(project, this)
  override val name: HighlightedText? = invariant.name?.toIdeaHighlightedText(project, this)

  override fun isValid(): Boolean {
    return name != null || description.text.isNotEmpty()
  }
}