package com.intelligentcomments.core.settings

import com.intelligentcomments.core.changes.SettingChange
import com.intelligentcomments.core.changes.SettingsChange
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializer
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.reactive.IProperty
import org.jdom.Element

@State(name = "IntelligentCommentsSettings", storages = [Storage("CommentsSettings.xml")])
class RiderCommentsSettings : PersistentStateComponent<Element> {
  companion object {
    private val logger = getLogger<RiderCommentsSettings>()
    fun getInstance(): RiderCommentsSettings = ApplicationManager.getApplication().getComponent(RiderCommentsSettings::class.java)
  }

  var renderComments: Boolean = true

  var renderDocComments: Boolean = true
  var renderMultilineComments: Boolean = true
  var renderSingleLineComments: Boolean = true
  var renderGroupOfSingleLineComments: Boolean = true

  var hideAllComments: Boolean = false
  var editMode: Boolean = false
  var maxCharsInLine: Int = 120

  var groupSeeAlso: Boolean = true
  var groupReturns: Boolean = true
  var groupRemarks: Boolean = true
  var groupSummaries: Boolean = true
  var groupParams: Boolean = true
  var groupExceptions: Boolean = true

  var showEmptyContent: Boolean = false
  var showFirstLevelHeaderWhenOneElement: Boolean = false

  var useItalicFontForComments = true
  var showOnlySummary = false
  var renderCommentsOnlyInDecompiledSources = false

  var useExperimentalFeatures = false

  fun applyToSettings(settings: RiderIntelligentCommentsSettingsProvider) {
    settings.commentsDisplayKind.set(computeDisplayKind())

    settings.renderDocComments.set(renderDocComments)
    settings.renderMultilineComments.set(renderMultilineComments)
    settings.renderGroupOfSingleLineComments.set(renderGroupOfSingleLineComments)
    settings.renderSingleLineComments.set(renderSingleLineComments)

    settings.maxCharsInLine.set(maxCharsInLine)

    settings.groupSeeAlso.set(groupSeeAlso)
    settings.groupReturns.set(groupReturns)
    settings.groupRemarks.set(groupRemarks)
    settings.groupSummaries.set(groupSummaries)
    settings.groupParams.set(groupParams)
    settings.groupExceptions.set(groupExceptions)

    settings.showEmptyContent.set(showEmptyContent)
    settings.showFirstLevelHeaderWhenOneElement.set(showFirstLevelHeaderWhenOneElement)

    settings.useItalicFont.set(useItalicFontForComments)
    settings.showOnlySummary.set(showOnlySummary)
    settings.renderCommentsOnlyInDecompiledSources.set(renderCommentsOnlyInDecompiledSources)

    settings.useExperimentalFeatures.set(useExperimentalFeatures)
  }

  private fun computeDisplayKind(): CommentsDisplayKind {
    return if (renderComments) {
      CommentsDisplayKind.Render
    } else if (hideAllComments) {
      CommentsDisplayKind.Hide
    } else {
      CommentsDisplayKind.Code
    }
  }

  fun anySettingsChanged(settings: RiderIntelligentCommentsSettingsProvider): Boolean {
    return computeDisplayKind() != settings.commentsDisplayKind.value ||
      renderDocComments != settings.renderDocComments.value ||
      renderMultilineComments != settings.renderMultilineComments.value ||
      renderSingleLineComments != settings.renderSingleLineComments.value ||
      renderGroupOfSingleLineComments != settings.renderGroupOfSingleLineComments.value ||
      maxCharsInLine != settings.maxCharsInLine.value ||
      groupSeeAlso != settings.groupSeeAlso.value ||
      groupReturns != settings.groupReturns.value ||
      groupRemarks != settings.groupRemarks.value ||
      groupSummaries != settings.groupSummaries.value ||
      groupParams != settings.groupParams.value ||
      groupExceptions != settings.groupExceptions.value ||
      showEmptyContent != settings.showEmptyContent.value ||
      showFirstLevelHeaderWhenOneElement != settings.showFirstLevelHeaderWhenOneElement.value ||
      useItalicFontForComments != settings.useItalicFont.value ||
      showOnlySummary != settings.showOnlySummary.value ||
      renderCommentsOnlyInDecompiledSources != settings.renderCommentsOnlyInDecompiledSources.value ||
      useExperimentalFeatures != settings.useExperimentalFeatures.value
  }

  fun reset(settings: RiderIntelligentCommentsSettingsProvider) {
    renderComments = settings.commentsDisplayKind.value == CommentsDisplayKind.Render
    hideAllComments = settings.commentsDisplayKind.value == CommentsDisplayKind.Hide
    editMode = settings.commentsDisplayKind.value == CommentsDisplayKind.Code

    renderSingleLineComments = settings.renderSingleLineComments.value
    renderDocComments = settings.renderDocComments.value
    renderMultilineComments = settings.renderMultilineComments.value
    renderGroupOfSingleLineComments = settings.renderGroupOfSingleLineComments.value

    maxCharsInLine = settings.maxCharsInLine.value

    groupSeeAlso = settings.groupSeeAlso.value
    groupReturns = settings.groupReturns.value
    groupRemarks = settings.groupRemarks.value
    groupSummaries = settings.groupSummaries.value
    groupParams = settings.groupParams.value
    groupExceptions = settings.groupExceptions.value

    showEmptyContent = settings.showEmptyContent.value
    showFirstLevelHeaderWhenOneElement = settings.showFirstLevelHeaderWhenOneElement.value

    useItalicFontForComments = settings.useItalicFont.value
    showOnlySummary = settings.showOnlySummary.value
    renderCommentsOnlyInDecompiledSources = settings.renderCommentsOnlyInDecompiledSources.value

    useExperimentalFeatures = settings.useExperimentalFeatures.value
  }

  fun createSettingsChange(settings: RiderIntelligentCommentsSettingsProvider): SettingsChange {
    val changes = mutableMapOf<IProperty<*>, SettingChange>()

    val newDisplayKind = computeDisplayKind()
    if (settings.commentsDisplayKind.value != newDisplayKind) {
      changes[settings.commentsDisplayKind] = SettingChange(settings.commentsDisplayKind.value, newDisplayKind)
    }

    val renderOnlyDecompiledFiles = settings.renderCommentsOnlyInDecompiledSources.value
    if (renderOnlyDecompiledFiles != renderCommentsOnlyInDecompiledSources) {
      changes[settings.renderCommentsOnlyInDecompiledSources] = SettingChange(renderOnlyDecompiledFiles, renderCommentsOnlyInDecompiledSources)
    }

    return SettingsChange(changes)
  }

  override fun getState(): Element? {
    try {
      return XmlSerializer.serialize(this)
    } catch (ex: Exception) {
      logger.error(ex)
    }

    return null
  }

  override fun loadState(element: Element) {
    try {
      XmlSerializer.deserializeInto(this, element)
    } catch (ex: Exception) {
      logger.error(ex)
    }

    applyToSettings(RiderIntelligentCommentsSettingsProvider.getInstance())
  }
}