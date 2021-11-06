package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.core.domain.core.IntelligentCommentContent
import com.intelligentComments.core.domain.core.UniqueEntityImpl
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdDocComment

class DocCommentFromRd(private val rdDocComment: RdDocComment, project: Project) : UniqueEntityImpl(), DocComment {
    override val content: IntelligentCommentContent = IntelligentCommentContentFromRd(rdDocComment.content, project)
}