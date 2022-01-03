package com.intelligentComments.core.changes

import com.jetbrains.rd.util.lifetime.Lifetime

interface ChangeListener {
  fun handleChange(change: Change)
}

interface ChangeManager {
  fun dispatch(change: Change)
  fun addListener(lifetime: Lifetime, listener: ChangeListener)
}