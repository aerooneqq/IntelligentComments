package com.intelligentcomments.core.comments.codeHighlighting

import com.intelligentcomments.core.changes.ChangeManager
import com.intelligentcomments.core.changes.CodeHighlightersChange
import com.intelligentcomments.core.domain.core.CodeSegment
import com.intelligentcomments.core.domain.core.HighlightedText
import com.intelligentcomments.core.domain.core.tryFindComment
import com.intelligentcomments.core.domain.rd.toIdeaHighlightedText
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdCodeHighlightingRequest
import com.jetbrains.rd.ide.model.rdCommentsModel
import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rider.projectView.solution

class CodeFragmentHighlightingHost(private val project: Project) : LifetimedService() {
  private val model = project.solution.rdCommentsModel
  private val changeManager = ChangeManager.getInstance()


  fun requestFullHighlighting(
    codeSegment: CodeSegment,
    id: Int,
    handleNewText: (HighlightedText) -> Unit
  ) {
    val request = RdCodeHighlightingRequest(id)

    val task = model.highlightCode.start(serviceLifetime, request)
    task.result.advise(serviceLifetime) {
      val newText = it.unwrap()
      if (newText != null) {
        val ideaText = newText.toIdeaHighlightedText(project, codeSegment)
        val comment = tryFindComment(codeSegment)
        if (comment != null) {
          handleNewText(ideaText)
          changeManager.dispatch(CodeHighlightersChange(comment.identifier, codeSegment.id, ideaText))
        }
      }
    }
  }
}