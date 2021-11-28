package com.intelligentComments.core.comments

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.jetbrains.rd.platform.util.application

data class SolutionCommentsState(
  val documentsComments: MutableMap<String, MutableMap<Int, CommentState>>
)

data class CommentState(val isInRenderMode: Boolean)

@State(
  name = "SolutionCommentsState"
)
class RiderCommentsStateManager(project: Project) : PersistentStateComponent<SolutionCommentsState> {
  private val documentManager: PsiDocumentManager = project.service()
  private var states: MutableMap<String, MutableMap<Int, CommentState>> = HashMap()


  override fun getState(): SolutionCommentsState {
    application.assertIsDispatchThread()
    return SolutionCommentsState(states)
  }

  override fun loadState(solutionCommentsState: SolutionCommentsState) {
    application.assertIsDispatchThread()
    states = solutionCommentsState.documentsComments
  }
}