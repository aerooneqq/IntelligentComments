package com.intelligentComments.core.comments.resolver

import com.intelligentComments.core.domain.core.Reference
import com.intelligentComments.core.domain.rd.toRdReference
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdReferenceResolveRequest
import com.jetbrains.rd.ide.model.RdResolveResult
import com.jetbrains.rd.ide.model.rdCommentsModel
import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rdclient.document.textControlId
import com.jetbrains.rider.projectView.solution

class FrontendReferenceResolverHost(private val project: Project) : LifetimedService() {
  private val model = project.solution.rdCommentsModel

  fun resolveInvariantReference(
    reference: Reference,
    editor: Editor,
    actionWithResult: (RdResolveResult) -> Unit
  ) {
    val textControlId = editor.textControlId ?: return
    val resolveRequest = RdReferenceResolveRequest(reference.toRdReference(project), textControlId)

    model.resolveReference.start(serviceLifetime, resolveRequest).result.advise(serviceLifetime) {
      actionWithResult(it.unwrap())
    }
  }
}