package com.intelligentcomments.core.domain.rd

import com.intelligentcomments.core.domain.core.ContentSegment
import com.intelligentcomments.core.domain.core.IntelligentCommentContent
import com.intelligentcomments.core.domain.core.Parentable
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdIntelligentCommentContent

class IntelligentCommentContentFromRd(
  rdContent: RdIntelligentCommentContent,
  override val parent: Parentable?,
  project: Project
) : EntityWithContentSegmentsFromRd(rdContent, parent, project), IntelligentCommentContent {
  private val myCachedSegments: MutableList<ContentSegment>

  init {
    val segments = rdContent.content.content
    myCachedSegments = segments.map { getFrom(it, this, project) }.toMutableList()
  }
}