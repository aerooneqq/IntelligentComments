package com.intelligentComments.core.domain.impl

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.settings.RiderIntelligentCommentsSettingsProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

class ContentProcessingStrategyImpl(private val project: Project) : ContentProcessingStrategy {

  override fun process(segments: MutableList<ContentSegment>) {
    return preprocessSegments(segments)
  }

  private fun preprocessSegments(segments: MutableList<ContentSegment>) {
    val settings = project.service<RiderIntelligentCommentsSettingsProvider>()
    groupSeeAlsoIfNeeded(settings, segments)
  }

  private fun groupSeeAlsoIfNeeded(
    settings: RiderIntelligentCommentsSettingsProvider,
    segments: MutableList<ContentSegment>
  ) {
    if (!settings.groupSeeAlso.value) return

    val topLevelSeeAlsoSegments = segments.filterIsInstance<SeeAlsoSegment>()
    val groupedSeeAlso = object : UniqueEntityImpl(), GroupedSeeAlsoContentSegment {
      override val segments: List<SeeAlsoSegment> = topLevelSeeAlsoSegments
    }

    val indexOfLastSeeAlso = segments.indexOf(topLevelSeeAlsoSegments.last())
    segments[indexOfLastSeeAlso] = groupedSeeAlso
    segments.removeAll(topLevelSeeAlsoSegments)
  }
}