package com.intelligentcomments.hacks

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.TextControlId
import com.jetbrains.rd.util.reactive.ViewableMap
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import java.lang.reflect.Field

//I promise I will fix it somewhere in the future
class FrontendTextControlHostHacks(project: Project) {
    private val textControlHost = FrontendTextControlHost.getInstance(project)
    private val field: Field = FrontendTextControlHost::class.java.getDeclaredField("openedEditors")

    init {
        field.isAccessible = true
    }

    fun getOpenedEditors(): ViewableMap<TextControlId, Editor> {
        return field.get(textControlHost) as ViewableMap<TextControlId, Editor>
    }
}