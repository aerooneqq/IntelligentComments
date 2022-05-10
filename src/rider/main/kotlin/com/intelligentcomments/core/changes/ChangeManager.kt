package com.intelligentcomments.core.changes

import com.intellij.openapi.application.ApplicationManager
import com.jetbrains.rd.util.lifetime.Lifetime

interface ChangeListener {
  fun handleChange(change: Change)
}

interface ChangeManager {
  companion object {
    fun getInstance(): ChangeManager = ApplicationManager.getApplication().getService(ChangeManager::class.java)
  }

  fun dispatch(change: Change)
  fun addListener(lifetime: Lifetime, listener: ChangeListener)
}