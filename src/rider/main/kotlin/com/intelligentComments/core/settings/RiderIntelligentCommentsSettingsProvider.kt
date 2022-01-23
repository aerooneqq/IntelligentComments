package com.intelligentComments.core.settings

import com.intellij.openapi.application.ApplicationManager
import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rd.util.reactive.Property

enum class CommentsDisplayKind {
  Hide,
  Render,
  Code
}

interface RiderIntelligentCommentsSettingsProvider {
  companion object {
    fun getInstance(): RiderIntelligentCommentsSettingsProvider {
      return ApplicationManager.getApplication().getService(RiderIntelligentCommentsSettingsProvider::class.java)
    }
  }


  val commentsDisplayKind: Property<CommentsDisplayKind>
  val groupingDelimiter: Property<String>
  val groupSeeAlso: Property<Boolean>
  val groupReturns: Property<Boolean>
  val groupRemarks: Property<Boolean>
  val groupSummaries: Property<Boolean>
  val groupParams: Property<Boolean>
  val groupExceptions: Property<Boolean>

  val showEmptyContent: Property<Boolean>
  val showFirstLevelHeaderWhenOneElement: Property<Boolean>

  val fontSize: Property<Float>
  val boldFontSize: Property<Float>

  val maxCharsInLine: Property<Int>
}

class RiderIntelligentCommentsSettingsProviderImpl : LifetimedService(), RiderIntelligentCommentsSettingsProvider {
  override val commentsDisplayKind: Property<CommentsDisplayKind> = Property(CommentsDisplayKind.Hide)
  override val groupingDelimiter: Property<String> = Property("\n")
  override val groupSeeAlso: Property<Boolean> = Property(true)
  override val groupReturns: Property<Boolean> = Property(true)
  override val groupRemarks: Property<Boolean> = Property(true)
  override val groupSummaries: Property<Boolean> = Property(true)
  override val groupParams: Property<Boolean> = Property(true)
  override val groupExceptions: Property<Boolean> = Property(true)

  override val showEmptyContent: Property<Boolean> = Property(false)
  override val showFirstLevelHeaderWhenOneElement: Property<Boolean> = Property(false)

  override val fontSize: Property<Float> = Property(12f)
  override val boldFontSize: Property<Float> = Property(14f)

  override val maxCharsInLine: Property<Int> = Property(120)
}