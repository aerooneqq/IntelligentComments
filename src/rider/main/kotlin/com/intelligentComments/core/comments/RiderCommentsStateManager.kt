package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CommentBase
import com.intellij.openapi.project.Project
import com.jetbrains.rdclient.util.idea.LifetimedProjectComponent

data class CommentState(val isInRenderMode: Boolean)

class RiderCommentsStateManager(project: Project) : LifetimedProjectComponent(project) {
  private val states = HashMap<CommentBase, CommentState>()


  fun registerComment(comment: CommentBase): CommentState {
    val state = CommentState(true)
    states[comment] = state
    return state
  }

  fun getStateFor(comment: CommentBase): CommentState? {
    return states[comment]
  }
}