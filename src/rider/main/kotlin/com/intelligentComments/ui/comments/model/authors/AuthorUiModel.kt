package com.intelligentComments.ui.comments.model.authors

import com.intelligentComments.core.domain.core.CommentAuthor
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

open class AuthorUiModel(
  project: Project,
  private val author: CommentAuthor
) : UiInteractionModelBase(project) {
  companion object {
    fun getFrom(project: Project, author: CommentAuthor): AuthorUiModel = AuthorUiModel(project, author)
  }

  val name = author.name
  val date = author.date

  override fun hashCode(): Int = author.hashCode() % HashUtil.mod
  override fun equals(other: Any?): Boolean = other is AuthorUiModel && other.hashCode() == hashCode()
}