package com.intelligentComments.ui.comments.model

import com.intelligentComments.core.domain.core.IntelligentComment
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import com.intellij.util.ui.UIUtil
import java.awt.Cursor
import javax.swing.Icon

class IntelligentCommentUiModel(project: Project,
                                val comment: IntelligentComment) : UiInteractionModelBase(project) {
    private val myAuthors = mutableListOf<AuthorUiModel>()
    private val myReferences = mutableListOf<ReferenceUiModel>()
    private val myInvariants = mutableListOf<InvariantUiModel>()
    private val myTodos = mutableListOf<ToDoUiModel>()
    private val myHacks = mutableListOf<HackUiModel>()

    val authorsSection: SectionUiModel<AuthorUiModel>
    val contentSection: SectionWithHeaderUiModel<ContentSegmentUiModel>
    val referencesSection: SectionWithHeaderUiModel<ReferenceUiModel>
    val invariantsSection: SectionWithHeaderUiModel<InvariantUiModel>
    val todosSection: SectionWithHeaderUiModel<ToDoUiModel>
    val hacksSection: SectionWithHeaderUiModel<HackUiModel>


    init {
        for (author in comment.allAuthors) myAuthors.add(AuthorUiModel.getFrom(project, author))
        val content = IntelligentCommentContentUiModel(project, comment.content)
        for (reference in comment.references) myReferences.add(ReferenceUiModel.getFrom(project, reference))
        for (invariant in comment.invariants) myInvariants.add(InvariantUiModel.getFrom(project, invariant))
        for (todo in comment.todos) myTodos.add(ToDoUiModel.getFrom(project, todo))
        for (hack in comment.hacks) myHacks.add(HackUiModel.getFrom(project, hack))

        authorsSection = SectionUiModel(project, myAuthors)
        contentSection = getSectionHeaderUiModel(content.segments, AllIcons.FileTypes.Text, "Content")
        referencesSection = getSectionHeaderUiModel(myReferences, AllIcons.FileTypes.Java, "References")
        invariantsSection = getSectionHeaderUiModel(myInvariants, AllIcons.Nodes.Interface, "Invariants")
        todosSection = getSectionHeaderUiModel(myTodos, AllIcons.General.TodoImportant, "ToDos")
        hacksSection = getSectionHeaderUiModel(myHacks, AllIcons.General.Locate, "Hacks")
    }

    private fun <T : UiInteractionModelBase> getSectionHeaderUiModel(items: Collection<T>,
                                                                     icon: Icon,
                                                                     name: String) : SectionWithHeaderUiModel<T> {
        val headerTextInfo = getHeaderInfo(name)
        return SectionWithHeaderUiModel(project, items, icon, headerTextInfo)
    }

    private fun getHeaderInfo(name: String): HeaderTextInfo {
        return HeaderTextInfo("$name: ", "$name (click to expand)")
    }

    override fun handleMouseIn(e: EditorMouseEvent): Boolean {
        UIUtil.setCursor(e.editor.contentComponent, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))
        return false
    }

    override fun handleMouseOut(e: EditorMouseEvent): Boolean {
        UIUtil.setCursor(e.editor.contentComponent, Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR))
        return false
    }

    override fun handleClick(e: EditorMouseEvent): Boolean {
        UIUtil.setCursor(e.editor.contentComponent, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))
        return false
    }
}