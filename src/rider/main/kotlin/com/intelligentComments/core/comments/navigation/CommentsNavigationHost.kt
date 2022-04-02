package com.intelligentComments.core.comments.navigation

import com.intelligentComments.core.domain.core.Reference
import com.intelligentComments.core.domain.rd.toRdReference
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rdclient.document.textControlId
import com.jetbrains.rider.projectView.solution

class CommentsNavigationHost(private val project: Project) : LifetimedService() {
  private val model = project.solution.rdCommentsModel
  private val logger = getLogger<CommentsNavigationHost>()


  fun performNavigation(reference: Reference, editor: Editor) {
    val rdReference = reference.toRdReference(project)
    val textControlId = editor.textControlId
    if (textControlId == null) {
      logger.error("Failed to get textControlId for $editor")
      return
    }

    val resolveRequest = RdReferenceResolveRequest(rdReference, textControlId)
    val request = RdNavigationRequest(resolveRequest)
    model.performNavigation.start(serviceLifetime, request)
  }
}