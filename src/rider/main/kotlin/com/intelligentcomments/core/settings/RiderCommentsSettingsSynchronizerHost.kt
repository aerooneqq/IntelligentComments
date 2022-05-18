package com.intelligentcomments.core.settings

import com.intellij.util.application
import com.jetbrains.rd.ide.model.rdCommentsSettingsModel
import com.jetbrains.rd.ide.model.shellModel
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.reactive.AddRemove
import com.jetbrains.rd.util.reactive.flowInto
import com.jetbrains.rider.protocol.protocolManager

class RiderCommentsSettingsSynchronizerHost {
  init {
    application.protocolManager.protocolHosts.advise(application.lifetime) { addRemove, host ->
      application.invokeLater {
        if (addRemove == AddRemove.Add) {
          val model = host.protocol.shellModel.rdCommentsSettingsModel
          val settingsProvider = RiderIntelligentCommentsSettingsProvider.getInstance()
          model.enableExperimentalFeatures.set(settingsProvider.useExperimentalFeatures.value)
          settingsProvider.useExperimentalFeatures.flowInto(application.lifetime, model.enableExperimentalFeatures)
        }
      }
    }
  }
}