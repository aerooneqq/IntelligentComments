package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.ContentProcessingStrategy
import com.intelligentComments.core.domain.core.ContentSegment
import com.intelligentComments.core.domain.core.IntelligentCommentContent
import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdIntelligentCommentContent
import java.util.*

class IntelligentCommentContentFromRd(
  rdContent: RdIntelligentCommentContent?,
  project: Project
) : UniqueEntityImpl(), IntelligentCommentContent {
  private val myCachedSegments: MutableList<ContentSegment>

  init {
    val segments = rdContent?.content?.content
    myCachedSegments = segments?.map { ContentSegmentFromRd.getFrom(it, project) }?.toMutableList() ?: mutableListOf()
  }

  override val segments: Collection<ContentSegment> = myCachedSegments

  override fun processSegments(strategy: ContentProcessingStrategy) {
    strategy.process(myCachedSegments)
  }
}