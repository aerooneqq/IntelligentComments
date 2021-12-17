package com.intelligentComments.core.settings

import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rd.util.reactive.Property

enum class CommentsDisplayKind {
  Render,
  Code
}

interface RiderIntelligentCommentsSettingsProvider {
  val commentsDisplayKind: Property<CommentsDisplayKind>
}

class RiderIntelligentCommentsSettingsProviderImpl : LifetimedService(), RiderIntelligentCommentsSettingsProvider {
  override val commentsDisplayKind: Property<CommentsDisplayKind> = Property(CommentsDisplayKind.Render)
}