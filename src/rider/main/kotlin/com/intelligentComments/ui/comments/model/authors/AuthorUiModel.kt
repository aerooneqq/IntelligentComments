package com.intelligentComments.ui.comments.model.authors

import com.intelligentComments.core.domain.core.CommentAuthor
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.renderers.NotSupportedForRenderingError
import com.intelligentComments.ui.core.Renderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class AuthorUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  private val author: CommentAuthor
) : UiInteractionModelBase(project, parent) {
  companion object {
    fun getFrom(
      project: Project,
      parent: UiInteractionModelBase?,
      author: CommentAuthor
    ): AuthorUiModel = AuthorUiModel(project, parent, author)
  }

  val name = author.name
  val date = author.date

  override fun calculateStateHash(): Int = HashUtil.hashCode(author.hashCode())

  override fun createRenderer(): Renderer = throw NotSupportedForRenderingError()
}