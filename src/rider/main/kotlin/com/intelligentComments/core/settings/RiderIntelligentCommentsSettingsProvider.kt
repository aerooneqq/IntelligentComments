package com.intelligentComments.core.settings

import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rd.util.reactive.Property

enum class CommentsDisplayKind {
  Render,
  Code
}

interface RiderIntelligentCommentsSettingsProvider {
  val commentsDisplayKind: Property<CommentsDisplayKind>
  val groupingDelimiter: Property<String>
  val groupSeeAlso: Property<Boolean>
  val groupReturns: Property<Boolean>
  val groupRemarks: Property<Boolean>
  val groupSummaries: Property<Boolean>
  val groupParams: Property<Boolean>
  val groupExceptions: Property<Boolean>
}

class RiderIntelligentCommentsSettingsProviderImpl : LifetimedService(), RiderIntelligentCommentsSettingsProvider {
  override val commentsDisplayKind: Property<CommentsDisplayKind> = Property(CommentsDisplayKind.Render)
  override val groupingDelimiter: Property<String> = Property("\n")
  override val groupSeeAlso: Property<Boolean> = Property(true)
  override val groupReturns: Property<Boolean> = Property(true)
  override val groupRemarks: Property<Boolean> = Property(true)
  override val groupSummaries: Property<Boolean> = Property(true)
  override val groupParams: Property<Boolean> = Property(true)
  override val groupExceptions: Property<Boolean> = Property(true)
}