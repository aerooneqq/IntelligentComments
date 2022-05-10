package com.intelligentcomments.core.changes

import com.intellij.util.application
import com.jetbrains.rd.util.addUnique
import com.jetbrains.rd.util.lifetime.Lifetime

class ChangeManagerImpl : ChangeManager {
  private val listeners = mutableListOf<ChangeListener>()


  override fun dispatch(change: Change) {
    application.assertIsDispatchThread()
    for (listener in listeners) {
      listener.handleChange(change)
    }
  }

  override fun addListener(lifetime: Lifetime, listener: ChangeListener) {
    listeners.addUnique(lifetime, listener)
  }
}