package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.ContentSegment
import com.intelligentComments.core.domain.core.IntelligentCommentContent
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdIntelligentCommentContent
import java.util.*

class IntelligentCommentContentFromRd(rdContent: RdIntelligentCommentContent?, project: Project) :
  IntelligentCommentContent {
  private val myCachedSegments: Collection<ContentSegment>

  init {
    val segments = rdContent?.content?.content
    myCachedSegments = segments?.map { ContentSegmentFromRd.getFrom(it, project) } ?: emptyList()
  }

  override val segments: Collection<ContentSegment> = myCachedSegments
  override val id: UUID = UUID.randomUUID()
}