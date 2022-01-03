package com.intelligentComments.core.changes

import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.util.application
import com.jetbrains.rd.util.addUnique
import com.jetbrains.rd.util.lifetime.Lifetime

class ChangeManagerImpl(project: Project) : ChangeManager {
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