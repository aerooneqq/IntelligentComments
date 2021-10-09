package com.intelligentComments.core.editors

import com.intelligentComments.core.domain.rd.IntelligentCommentFromRd
import com.intelligentComments.ui.colors.Colors
import com.intelligentComments.ui.listeners.CommentMouseListener
import com.intelligentComments.ui.listeners.CommentMouseMoveListener
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.InlayProperties
import com.intellij.openapi.rd.createNestedDisposable
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.util.Date
import com.jetbrains.rd.util.lifetime.Lifetime
import java.awt.font.TextAttribute

class RiderEditorHandler : EditorHandler {
    override fun startMonitoringEditor(editor: Editor, monitoringLifetime: Lifetime) {
        val properties = InlayProperties().apply {
            showAbove(true)
            showWhenFolded(false)
        }

        val comment = RdIntelligentComment(0, RdFileDocumentIdImpl("asdasd", ":asdasd"))
        comment.authors.add(RdIntelligentCommentAuthor("Aero", Date()))
        val content = RdIntelligentCommentContent()

        val text =
"""Известный профессор МТИ Гарольд Абельсон сказал: «Программы нужно писать для того, чтобы их читали люди, и лишь случайно — чтобы их исполняли машины».
Хотя он намеренно преуменьшил важность исполнения кода, однако подчёркивает, что у программ две важные аудитории.
Компиляторы и интерпретаторы игнорируют комментарии и с одинаковой лёгкостью воспринимают все синтаксически корректные программы.
У людей всё иначе. Одни программы нам воспринимать легче, чем другие, и мы ищем комментарии, которые помогут нам разобраться."""


        val highlighter = RdTextHighlighter("text.test.first.color", 0, 200, RdTextAttributes(), backgroundStyle = RdBackgroundStyle(RdColor("#FFFF00"), true), animation = RdUnderlineTextAnimation())
        val highlighter1 = RdTextHighlighter("text.test.second.color", 200, 213, RdTextAttributes(fontWeight = 1000f, fontStyle = RdFontStyle.Bold, underline = true), animation = RdUnderlineTextAnimation())
        val highlighter2 = RdTextHighlighter("text.test.first.color", 213, 234, RdTextAttributes())
        val highlighter3 = RdTextHighlighter("text.test.second.color", 235, 260, RdTextAttributes())
        content.segments.add(RdTextSegment(RdHighlightedText(text, mutableListOf(highlighter, highlighter1, highlighter2, highlighter3))))
        content.segments.add(RdTextSegment(RdHighlightedText(text)))
        content.segments.add(RdTextSegment(RdHighlightedText(text)))


        comment.content.set(content)
        comment.invariants.add(RdTextInvariant("Synchronous"))
        comment.invariants.add(RdTextInvariant("ReadLock"))

        comment.references.add(RdDependencyReference(
                "Aero.Software::Method1",
                """This method depends on the synchronous nature of Method1,""",
                "C:\\Aero\\Software\\FactoryOfBeans.cs"))

        comment.references.add(RdDependencyReference(
                "Aero.Software.Domain.Models.UniqueEntity::ID",
                """This method depends on that ID must be zero if the user is admin
This method depends on the synchronous nature of Method1,""".trimMargin(),
                "C:\\Aero\\Software\\Domain\\Models\\UniqueEntity.cs"))

        comment.references.add(RdDependencyReference(
                "Aero.Software.Domain.Models.UniqueEntity::ID",
                """This method depends on that ID must be zero if the user is admin""",
                "C:\\Aero\\Software\\Domain\\Models\\UniqueEntity.cs"))

        comment.references.add(RdDependencyReference(
                "Aero.Software.Domain.Models.UniqueEntity::ID",
                """This method depends on that ID must be zero if the user is admin""",
                "C:\\Aero\\Software\\Domain\\Models\\UniqueEntity.cs"))

        val intelligentComment = IntelligentCommentFromRd(comment, editor.project!!)
        val inlayRenderer = intelligentComment.getRenderer(editor.project!!)
        val inlay = editor.inlayModel.addBlockElement(0, properties, inlayRenderer)
        
        editor.addEditorMouseMotionListener(CommentMouseMoveListener(inlay!!), monitoringLifetime.createNestedDisposable())
        editor.addEditorMouseListener(CommentMouseListener(inlay), monitoringLifetime.createNestedDisposable())

        inlay.update()
        inlay.repaint()
    }
}