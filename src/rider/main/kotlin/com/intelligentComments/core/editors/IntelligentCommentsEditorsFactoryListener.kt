package com.intelligentComments.core.editors

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.createNestedDisposable
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.application
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent

class IntelligentCommentsEditorsFactoryListener(project: Project) : LifetimedProjectComponent(project), EditorFactoryListener {
    companion object {
        private val logger = Logger.getInstance(IntelligentCommentsEditorsFactoryListener::class.java)
    }


    private val currentEditors = HashMap<Editor, LifetimeDefinition>()


    init {
        EditorFactory.getInstance().addEditorFactoryListener(this, componentLifetime.createNestedDisposable())
    }


    override fun editorCreated(event: EditorFactoryEvent) {
        application.assertIsDispatchThread()
        val editorMonitoringLifetimeDef = componentLifetime.createNested()
        for (handler in EditorHandler.allExtensions) {
            handler.startMonitoringEditor(event.editor, editorMonitoringLifetimeDef)

            if (currentEditors.containsKey(event.editor)) {
                logger.logAssertion("Editor ${event.editor} already in currentEditors when created")
            }

            currentEditors[event.editor] = editorMonitoringLifetimeDef
        }
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        application.assertIsDispatchThread()
        val monitoringDef = currentEditors.getOrDefault(event.editor, null)
        if (monitoringDef == null) {
            logger.logAssertion("Editor ${event.editor} was not in currentEditors when released")
            return
        }

        monitoringDef.terminate()
    }
}