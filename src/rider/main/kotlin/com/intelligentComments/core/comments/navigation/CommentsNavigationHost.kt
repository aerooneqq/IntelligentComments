package com.intelligentComments.core.comments.navigation

import com.intelligentComments.core.domain.core.CodeEntityReference
import com.intelligentComments.core.domain.core.ProxyReference
import com.intelligentComments.core.domain.core.Reference
import com.intelligentComments.core.domain.rd.toRdReference
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdCodeEntityReference
import com.jetbrains.rd.ide.model.RdNavigationRequest
import com.jetbrains.rd.ide.model.RdProxyReference
import com.jetbrains.rd.ide.model.rdCommentsModel
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rdclient.document.textControlId
import com.jetbrains.rider.projectView.solution

class CommentsNavigationHost(private val project: Project) : LifetimedService() {
  private val model = project.solution.rdCommentsModel
  private val logger = getLogger<CommentsNavigationHost>()


  fun performNavigation(reference: Reference, editor: Editor) {
    val rdReference = reference.toRdReference(project)
    if (rdReference !is RdCodeEntityReference && rdReference !is RdProxyReference) {
      logger.error("Expected CodeEntityReference or ProxyReference, got ${reference.javaClass.name}")
      return
    }

    val textControlId = editor.textControlId
    if (textControlId == null) {
      logger.error("Failed to get textControlId for $editor")
      return
    }

    val request = RdNavigationRequest(rdReference, textControlId)
    model.performNavigation.start(serviceLifetime, request)
  }
}