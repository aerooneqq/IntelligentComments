package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.IntelligentComment
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.hacks.HackWithTicketsUiModel
import com.intelligentComments.ui.comments.model.invariants.TextInvariantUiModel
import com.intelligentComments.ui.comments.model.references.ReferenceUiModel
import com.intelligentComments.ui.comments.model.sections.HeaderTextInfo
import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.model.todo.ToDoWithTicketsUiModel
import com.intelligentComments.ui.comments.renderers.IntelligentCommentsRenderer
import com.intelligentComments.ui.util.HashUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import com.intellij.util.ui.UIUtil
import java.awt.Cursor
import javax.swing.Icon

class IntelligentCommentUiModel(
  project: Project,
  comment: IntelligentComment
) : CommentUiModelBase(comment, project, null!!) {
  override val renderer = IntelligentCommentsRenderer(this)

  override val contentSection: SectionWithHeaderUiModel
  val referencesSection: SectionWithHeaderUiModel
  val invariantsSection: SectionWithHeaderUiModel
  val todosSection: SectionWithHeaderUiModel
  val hacksSection: SectionWithHeaderUiModel


  init {
    val references = mutableListOf<ReferenceUiModel>()
    val invariants = mutableListOf<TextInvariantUiModel>()
    val todos = mutableListOf<ToDoWithTicketsUiModel>()
    val hacks = mutableListOf<HackWithTicketsUiModel>()
    val content = IntelligentCommentContentUiModel(project, this, comment.content)

    for (reference in comment.references) references.add(ReferenceUiModel(project, this, reference))

    for (invariant in comment.invariants) invariants.add(TextInvariantUiModel(project, this, invariant))

    for (todo in comment.todos) todos.add(ToDoWithTicketsUiModel(project, this, todo))
    for (hack in comment.hacks) hacks.add(HackWithTicketsUiModel(project, this, hack))

    contentSection = getSectionHeaderUiModel(content.segments, AllIcons.FileTypes.Text, "Content")
    referencesSection = getSectionHeaderUiModel(references, AllIcons.FileTypes.Java, "References")
    invariantsSection = getSectionHeaderUiModel(invariants, AllIcons.Nodes.Interface, "Invariants")
    todosSection = getSectionHeaderUiModel(todos, AllIcons.General.TodoImportant, "ToDos")
    hacksSection = getSectionHeaderUiModel(hacks, AllIcons.General.Locate, "Hacks")
  }


  private fun getSectionHeaderUiModel(
    items: Collection<ContentSegmentUiModel>,
    icon: Icon,
    name: String
  ): SectionWithHeaderUiModel {
    val headerTextInfo = getHeaderInfo(name)
    return SectionWithHeaderUiModel(project, this, items, icon, headerTextInfo)
  }

  private fun getHeaderInfo(name: String): HeaderTextInfo {
    return HeaderTextInfo("$name: ", "$name (click to expand)")
  }

  override fun handleMouseInInternal(e: EditorMouseEvent): Boolean {
    UIUtil.setCursor(e.editor.contentComponent, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))
    return false
  }

  override fun handleMouseOutInternal(e: EditorMouseEvent): Boolean {
    UIUtil.setCursor(e.editor.contentComponent, Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR))
    return false
  }

  override fun handleClick(e: EditorMouseEvent): Boolean {
    UIUtil.setCursor(e.editor.contentComponent, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))
    return false
  }


  override fun calculateStateHash(): Int {
    val hashCode = HashUtil.hashCode(
      todosSection.calculateStateHash (),
      hacksSection.calculateStateHash(),
      referencesSection.calculateStateHash(),
      invariantsSection.calculateStateHash(),
      contentSection.calculateStateHash()
    )

    assert(hashCode != 0)
    return hashCode
  }
}