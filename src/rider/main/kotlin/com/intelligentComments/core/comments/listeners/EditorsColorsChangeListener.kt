package com.intelligentComments.core.comments.listeners

import com.intelligentComments.core.changes.ChangeManager
import com.intelligentComments.core.changes.ThemeChange
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.Project
import com.intellij.util.application
import com.jetbrains.rd.platform.util.subscribe
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent


class EditorsColorsChangeListener(project: Project) : LifetimedProjectComponent(project) {
  init {
    val changeManager = ChangeManager.getInstance()
    project.messageBus.subscribe(componentLifetime, EditorColorsManager.TOPIC, EditorColorsListener {
      application.invokeLater {
        changeManager.dispatch(ThemeChange())
      }
    })
  }
}