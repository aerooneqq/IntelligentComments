package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.IntelligentComment
import com.intelligentComments.ui.comments.model.authors.AuthorUiModel
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.hacks.HackUiModel
import com.intelligentComments.ui.comments.model.invariants.AddNewInvariantUiModel
import com.intelligentComments.ui.comments.model.invariants.InvariantUiModel
import com.intelligentComments.ui.comments.model.references.ReferenceUiModel
import com.intelligentComments.ui.comments.model.sections.HeaderTextInfo
import com.intelligentComments.ui.comments.model.sections.SectionUiModel
import com.intelligentComments.ui.comments.model.sections.SectionWithHeaderUiModel
import com.intelligentComments.ui.comments.model.todo.ToDoUiModel
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

  val authorsSection: SectionUiModel<AuthorUiModel>
  override val contentSection: SectionWithHeaderUiModel<ContentSegmentUiModel>
  val referencesSection: SectionWithHeaderUiModel<ReferenceUiModel>
  val invariantsSection: SectionWithHeaderUiModel<InvariantUiModel>
  val todosSection: SectionWithHeaderUiModel<ToDoUiModel>
  val hacksSection: SectionWithHeaderUiModel<HackUiModel>


  init {
    val authors = mutableListOf<AuthorUiModel>()
    val references = mutableListOf<ReferenceUiModel>()
    val invariants = mutableListOf<InvariantUiModel>()
    val todos = mutableListOf<ToDoUiModel>()
    val hacks = mutableListOf<HackUiModel>()
    val content = IntelligentCommentContentUiModel(project, this, comment.content)

    for (author in comment.allAuthors) authors.add(AuthorUiModel.getFrom(project, this, author))
    for (reference in comment.references) references.add(ReferenceUiModel.getFrom(project, reference))

    for (invariant in comment.invariants) invariants.add(InvariantUiModel.getFrom(project, this, invariant))
    invariants.add(AddNewInvariantUiModel(project, this))

    for (todo in comment.todos) todos.add(ToDoUiModel.getFrom(project, this, todo))
    for (hack in comment.hacks) hacks.add(HackUiModel.getFrom(project, this, hack))

    authorsSection = SectionUiModel(project, this, authors)
    contentSection = getSectionHeaderUiModel(content.segments, AllIcons.FileTypes.Text, "Content")
    referencesSection = getSectionHeaderUiModel(references, AllIcons.FileTypes.Java, "References")
    invariantsSection = getSectionHeaderUiModel(invariants, AllIcons.Nodes.Interface, "Invariants")
    todosSection = getSectionHeaderUiModel(todos, AllIcons.General.TodoImportant, "ToDos")
    hacksSection = getSectionHeaderUiModel(hacks, AllIcons.General.Locate, "Hacks")
  }


  private fun <T : UiInteractionModelBase> getSectionHeaderUiModel(
    items: Collection<T>,
    icon: Icon,
    name: String
  ): SectionWithHeaderUiModel<T> {
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
      authorsSection.calculateStateHash(),
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