package com.intelligentComments.core.domain.core

import com.intelligentComments.core.utils.DocumentUtils
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project


data class CommentIdentifier(val moniker: String, val commentIdentifier: Int) {
  companion object {
    val emptyInstance = CommentIdentifier("", 0)

    fun create(document: Document, project: Project, rangeHash: Int): CommentIdentifier {
      val moniker = DocumentUtils.tryGetMoniker(document, project) ?: return emptyInstance
      return CommentIdentifier(moniker, rangeHash)
    }
  }
}

interface CommentBase : UniqueEntity {
  val highlighter: RangeHighlighter
  val commentIdentifier: CommentIdentifier
}