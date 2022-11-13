package com.intelligentcomments.core.settings

import com.intelligentcomments.core.changes.ChangeManager
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.ui.layout.selected
import com.intellij.util.application
import com.jetbrains.rd.swing.textProperty
import com.jetbrains.rd.util.lifetime.Lifetime
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty


class RiderCommentsConfigurable : BoundConfigurable("Intelligent comments", null) {
  private val viewModel = RiderCommentsSettings.getInstance()
  private val settingsProvider = RiderIntelligentCommentsSettingsProvider.getInstance()
  private var panel: DialogPanel? = null


  init {
    viewModel.reset(settingsProvider)
  }


  override fun isModified(): Boolean = viewModel.anySettingsChanged(settingsProvider)

  override fun apply() {
    val change = viewModel.createSettingsChange(settingsProvider)
    viewModel.applyToSettings(settingsProvider)
    application.invokeLater {
      ChangeManager.getInstance().dispatch(change)
    }
  }

  override fun reset() {
    viewModel.reset(settingsProvider)
    panel?.reset()
  }

  override fun createPanel(): DialogPanel {
    val panel = panel {
      createDisplayKindButtonGroup(this)
      createGroupingsButtonGroup(this)
      createMaxLinesComponent(this)
      createFontTextBoxComponent(this)
      createBoldFontTextBoxComponent(this)
      createOtherDocCommentsOptions(this)
    }

    this.panel = panel
    return panel
  }

  private fun createDisplayKindButtonGroup(panel: Panel) {
    panel.apply {
      buttonGroup {
        var renderComments: JBRadioButton? = null

        row {
          renderComments = radioButton("Render comments").associateWith(viewModel::renderComments).component
        }

        indent {
          row {
            checkBox("Render doc comments").enabledIf(renderComments!!.selected).associateWith(viewModel::renderDocComments)
          }

          row {
            checkBox("Render multi-line comments").enabledIf(renderComments!!.selected).associateWith(viewModel::renderMultilineComments)
          }

          row {
            checkBox("Render group of single-line comments").enabledIf(renderComments!!.selected).associateWith(viewModel::renderGroupOfSingleLineComments)
          }

          row {
            checkBox("Render single-line comments").enabledIf(renderComments!!.selected).associateWith(viewModel::renderSingleLineComments)
          }
        }

        row {
          radioButton("Hide all comments").associateWith(viewModel::hideAllComments)
        }

        row {
          radioButton("Display comments in edit-mode").associateWith(viewModel::editMode)
        }

        row {
          checkBox("Use experimental features").associateWith(viewModel::useExperimentalFeatures)
        }

        row {
          checkBox("Render comments only for decompiled sources").associateWith(viewModel::renderCommentsOnlyInDecompiledSources)
        }
      }
    }
  }

  private fun createGroupingsButtonGroup(panel: Panel) {
    panel.apply {
      buttonGroup("Grouping of documentation comments sections:") {
        row {
          checkBox("Group see also").associateWith(viewModel::groupSeeAlso)
        }

        row {
          checkBox("Group returns").associateWith(viewModel::groupReturns)
        }

        row {
          checkBox("Group remarks").associateWith(viewModel::groupRemarks)
        }

        row {
          checkBox("Group summaries").associateWith(viewModel::groupSummaries)
        }

        row {
          checkBox("Group params").associateWith(viewModel::groupParams)
        }

        row {
          checkBox("Group exceptions").associateWith(viewModel::groupExceptions)
        }
      }
    }
  }

  private fun createMaxLinesComponent(panel: Panel) {
    createIntegerTextBoxComponent(panel, "Maximum line length in comment: ", IntRange(80, Int.MAX_VALUE), viewModel::maxCharsInLine)
  }

  private fun createFontTextBoxComponent(panel: Panel) {
    createIntegerTextBoxComponent(panel, "Comments font size", IntRange(1, Int.MAX_VALUE), viewModel::fontSize)
  }

  private fun createBoldFontTextBoxComponent(panel: Panel) {
    createIntegerTextBoxComponent(panel, "Comments bold size", IntRange(1, Int.MAX_VALUE), viewModel::boldFontSize)
  }

  private fun createIntegerTextBoxComponent(
    panel: Panel,
    title: String,
    range: IntRange,
    property: KMutableProperty0<Int>) {
    panel.apply {
      row {
        label(title)
        intTextField(range).apply {
          component.textProperty().advise(Lifetime.Eternal) {
            val newValue = it.toIntOrNull() ?: return@advise
            property.set(newValue)
          }
        }.bindIntText(property)
      }
    }
  }

  private fun createOtherDocCommentsOptions(panel: Panel) {
    panel.apply {
      buttonGroup("Other documentation comment render options:") {
        row {
          checkBox("Show empty content").associateWith(viewModel::showEmptyContent)
        }

        row {
          checkBox("Show header of a single section in comment").associateWith(viewModel::showFirstLevelHeaderWhenOneElement)
        }

        row {
          checkBox("Use italic font for comments").associateWith(viewModel::useItalicFontForComments)
        }

        row {
          checkBox("Show only summary").associateWith(viewModel::showOnlySummary)
        }
      }
    }
  }
}

private fun Cell<JBCheckBox>.associateWith(property: KProperty<Boolean>): Cell<JBCheckBox> {
  addListener(this.component.selected, property)
  if (property is KMutableProperty0<Boolean>) bindSelected(property)

  return this
}

private fun addListener(predicate: ComponentPredicate, property: KProperty<Boolean>) {
  predicate.addListener {
    if (property is KMutableProperty<Boolean>) {
      property.setter.call(it)
    }
  }
}

@JvmName("associateWithJBRadioButton")
private fun Cell<JBRadioButton>.associateWith(property: KProperty<Boolean>): Cell<JBRadioButton> {
  addListener(this.component.selected, property)
  if (property is KMutableProperty0<Boolean>) bindSelected(property)

  return this
}