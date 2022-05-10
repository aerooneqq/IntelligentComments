package com.intelligentcomments.core.changes

import com.jetbrains.rd.util.reactive.IProperty

interface Change

class ThemeChange : Change

class SettingsChange(val changes: Map<IProperty<*>, SettingChange>) : Change

data class SettingChange(
  val oldValue: Any,
  val newValue: Any
)