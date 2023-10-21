package com.intelligentcomments.core.markup

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdMarkupModel
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rdclient.daemon.IProtocolHighlighterModelHandler
import com.jetbrains.rdclient.daemon.IProtocolHighlighterModelSupport
import com.jetbrains.rdclient.daemon.highlighters.MarkupListenerManager

class DocCommentsFoldingHighlightersSupport(private val project: Project) : IProtocolHighlighterModelSupport {
  override fun createHandler(
    lifetime: Lifetime,
    project: Project?,
    markupModel: RdMarkupModel,
    editor: Editor
  ): IProtocolHighlighterModelHandler? {
    return null
  }

  override fun createHandler(
    lifetime: Lifetime,
    project: Project?,
    markupModel: RdMarkupModel,
    document: Document
  ): IProtocolHighlighterModelHandler? {
    if (project == null) return null

    val manager = MarkupListenerManager.getInstance()
    manager.attachAggregator(lifetime, DocCommentsFoldingAggregator(project, document))
    return DocCommentsFoldingHandler()
  }
}