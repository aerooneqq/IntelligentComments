package com.intelligentcomments.core.comments.listeners

import com.intellij.codeInsight.daemon.impl.EditorTrackerListener
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.platform.util.subscribe
import com.jetbrains.rd.util.reactive.Property


class RiderEditorsTracker(project: Project) : EditorTrackerListener {
    val visibleEditors = Property<List<Editor>>(emptyList())

    init {
        project.messageBus.subscribe(project.lifetime, EditorTrackerListener.TOPIC, this)
    }

    override fun activeEditorsChanged(activeEditors: List<Editor>) {
        visibleEditors.set(activeEditors)
    }
}