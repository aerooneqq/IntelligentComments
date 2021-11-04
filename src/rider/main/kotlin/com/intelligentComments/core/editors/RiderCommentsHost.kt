package com.intelligentComments.core.editors

import com.intelligentComments.core.domain.rd.IntelligentCommentFromRd
import com.intelligentComments.ui.listeners.CommentMouseListener
import com.intelligentComments.ui.listeners.CommentMouseMoveListener
import com.intellij.openapi.editor.InlayProperties
import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.createNestedDisposable
import com.jetbrains.rd.ide.model.RdFileDocumentId
import com.jetbrains.rd.ide.model.RdIntelligentComment
import com.jetbrains.rd.ide.model.rdCommentsModel
import com.jetbrains.rdclient.document.getDocumentId
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent
import com.jetbrains.rider.editors.RiderTextControlHost
import com.jetbrains.rider.projectView.solution

class RiderCommentsHost(project: Project) : LifetimedProjectComponent(project) {
    init {
        project.solution.rdCommentsModel.comments.advise(componentLifetime) {
            val editors = RiderTextControlHost.getInstance(project).getAllEditors(it.key)

            val rdDocumentComments = it.newValueOpt ?: return@advise
            for (editor in editors) {
                for (comment in rdDocumentComments.comments) {
                    val inlayProperties = InlayProperties().apply {
                        showAbove(true)
                        showWhenFolded(false)
                    }

                    val intelligentComment = IntelligentCommentFromRd(comment as RdIntelligentComment, project)
                    val inlayRenderer = intelligentComment.getRenderer(project)
                    val inlay = editor.inlayModel.addBlockElement(comment.offset, inlayProperties, inlayRenderer)
                    editor.addEditorMouseMotionListener(CommentMouseMoveListener(inlay!!), componentLifetime.createNestedDisposable())
                    editor.addEditorMouseListener(CommentMouseListener(inlay), componentLifetime.createNestedDisposable())

                    inlay.update()
                    inlay.repaint()
                }
            }
        }
    }
}