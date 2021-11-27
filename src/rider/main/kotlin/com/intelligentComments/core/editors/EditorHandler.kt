package com.intelligentComments.core.editors

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.extensions.ExtensionPointName
import com.jetbrains.rd.util.lifetime.Lifetime

interface EditorHandler {
  companion object {
    val EP_NAME = ExtensionPointName.create<EditorHandler>("com.intelligentComments.intelligentCommentsEditorHandler")

    val allExtensions: Array<EditorHandler> = EP_NAME.extensions
  }

  fun startMonitoringEditor(editor: Editor, monitoringLifetime: Lifetime)
}