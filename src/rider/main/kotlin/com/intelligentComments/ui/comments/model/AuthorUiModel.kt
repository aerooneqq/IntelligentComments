package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.CommentAuthor
import com.intellij.openapi.project.Project

open class AuthorUiModel(project: Project,
                         author: CommentAuthor) : UiInteractionModelBase(project) {
    companion object {
        fun getFrom(project: Project, author: CommentAuthor): AuthorUiModel = AuthorUiModel(project, author)
    }

    val name = author.name
    val date = author.date
}