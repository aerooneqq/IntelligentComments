package com.intelligentComments.core.editors

import com.intelligentComments.core.domain.rd.IntelligentCommentFromRd
import com.intelligentComments.ui.listeners.CommentMouseListener
import com.intelligentComments.ui.listeners.CommentMouseMoveListener
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.InlayProperties
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.rd.createNestedDisposable
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.util.Date
import com.jetbrains.rd.util.lifetime.Lifetime

class RiderEditorHandler : EditorHandler {
    override fun startMonitoringEditor(editor: Editor, monitoringLifetime: Lifetime) {
        val properties = InlayProperties().apply {
            showAbove(true)
            showWhenFolded(false)
        }

        fun getAuthor() = RdIntelligentCommentAuthor("Aero", Date())
        val authors = mutableListOf(getAuthor())

        val text =
"""Известный профессор МТИ Гарольд Абельсон сказал: «Программы нужно писать для того, чтобы их читали люди, и лишь случайно — чтобы их исполняли машины».
Хотя он намеренно преуменьшил важность исполнения кода, однако подчёркивает, что у программ две важные аудитории.
Компиляторы и интерпретаторы игнорируют комментарии и с одинаковой лёгкостью воспринимают все синтаксически корректные программы.
У людей всё иначе. Одни программы нам воспринимать легче, чем другие, и мы ищем комментарии, которые помогут нам разобраться."""


        val contents = mutableListOf<RdContentSegment>()
        val highlighter = RdTextHighlighter("text.test.first.color", 0, 4, RdTextAttributes(), backgroundStyle = RdBackgroundStyle(RdColor("#FFFF00"), true), animation = RdUnderlineTextAnimation())
        val highlighter1 = RdTextHighlighter("text.test.second.color", 200, 213, RdTextAttributes(fontWeight = 1000f, fontStyle = RdFontStyle.Bold, underline = true), animation = RdUnderlineTextAnimation())
        val highlighter2 = RdTextHighlighter("text.test.first.color", 213, 234, RdTextAttributes())
        val highlighter3 = RdTextHighlighter("text.test.second.color", 235, 260, RdTextAttributes())
        contents.add(RdTextSegment(RdHighlightedText(text)))
        contents.add(RdTextSegment(RdHighlightedText(text)))
        contents.add(RdTextSegment(RdHighlightedText(text)))

        fun getListHeader(text: String): RdHighlightedText {
            val animation = RdPredefinedForegroundColorAnimation("text.default.color.hovered")
            val highlighter = RdTextHighlighter("text.default.color", 0, text.length, RdTextAttributes(), animation = animation)
            return RdHighlightedText(text, mutableListOf(highlighter))
        }

        val listContent1 = RdTextSegment(RdHighlightedText(text))
        val innerListContent = RdTextSegment(RdHighlightedText(text))
        val innerListContent2 = RdTextSegment(RdHighlightedText(text))
        val listContent2 = RdListSegment(mutableListOf(RdContentSegments(mutableListOf(innerListContent, innerListContent2))), getListHeader("Inner list:"))
        val listContent3 = RdTextSegment(RdHighlightedText(text))
        val catPath = "/Users/aero/Desktop/maxresdefault.jpg"
        val listSegment4 = RdFileBasedImageSegment(catPath, RdHighlightedText("This cat is awesome"))
        val listContents = mutableListOf(RdContentSegments(mutableListOf(listContent1)), RdContentSegments(mutableListOf(listContent2)), RdContentSegments(mutableListOf(listSegment4)), RdContentSegments(mutableListOf(listContent3)))
        contents.add(RdListSegment(listContents, getListHeader("List:")))

        val imageSegment = RdFileBasedImageSegment(catPath, RdHighlightedText("This cat is awesome"))
        val imageSegment1 = RdFileBasedImageSegment(catPath, RdHighlightedText("This cat is awesome This cat is awesome This cat is awesome This cat is awesome This cat is awesome This cat is awesome"))
        contents.add(imageSegment)
        contents.add(imageSegment1)

        val tableHeader = RdHighlightedText("My super table!", mutableListOf())
        var idx = 0

        fun getTableCellContent(): RdContentSegments {
            val text = RdTextSegment(RdHighlightedText("asdsadasdaasdasdsdasdad"))
            val listText = RdTextSegment(RdHighlightedText("Super puper puper list"))
            val list = RdListSegment(mutableListOf(RdContentSegments(mutableListOf(listText))), getListHeader("List inside table:"))
            val imageSegment = RdFileBasedImageSegment(catPath, RdHighlightedText("This cat is awesome"))
            val contentList = mutableListOf(text, list)
            if (idx == 0 || idx == 3) contentList.add(imageSegment)
            ++idx

            return RdContentSegments(contentList)
        }

        fun getHeaderCellContent(): RdContentSegments {
            val text = RdTextSegment(RdHighlightedText("Header header!"))
            return RdContentSegments(listOf(text))
        }

        val headerRows = RdTableRow(listOf(RdTableCell(getHeaderCellContent(), properties = RdTableCellProperties(RdHorizontalAlignment.Center, RdVerticalAlignment.Center, true)), RdTableCell(getHeaderCellContent(), properties = RdTableCellProperties(RdHorizontalAlignment.Center, RdVerticalAlignment.Center, true))))
        val rows = RdTableRow(listOf(RdTableCell(getTableCellContent()), RdTableCell(getTableCellContent(), properties = RdTableCellProperties(RdHorizontalAlignment.Right, RdVerticalAlignment.Top, false))))
        val rows1 = RdTableRow(listOf(RdTableCell(getTableCellContent()), RdTableCell(getTableCellContent())))
        val rdTable = RdTableSegment(tableHeader, listOf(headerRows, rows, rows1))
        contents.add(rdTable)

        val contentSegments = RdContentSegments(contents)
        val content = RdIntelligentCommentContent(contentSegments)

        val invariants = mutableListOf<RdInvariant>()
        invariants.add(RdTextInvariant("Synchronous"))
        invariants.add(RdTextInvariant("ReadLock"))

        fun getReference() = RdDependencyReference(
                """This method depends on the synchronous nature of Method1,""",
                "C:\\Aero\\Software\\FactoryOfBeans.cs",
                "Aero.Software::Method1")

        val references = mutableListOf<RdReference>()
        references.add(getReference())

        references.add(RdDependencyReference(
                """This method depends on that ID must be zero if the user is admin
This method depends on the synchronous nature of Method1,""".trimMargin(),
                "C:\\Aero\\Software\\Domain\\Models\\UniqueEntity.cs",
                "Aero.Software.Domain.Models.UniqueEntity::ID"))

        references.add(getReference())
        references.add(getReference())

        fun getTicket(): RdTicket {
            return RdTicket("https://google.com", "RIDER-14321")
        }

        fun getToDo(): RdToDo {
            val tickets = mutableListOf(getTicket(), getTicket(), getTicket(), getTicket())
            val description = RdContentSegments(mutableListOf(RdTextSegment(RdHighlightedText("This code", null))))
            val references = mutableListOf(getReference())
            return RdToDoWithTickets(tickets, getAuthor(), "Uncomment this code after 213", contentSegments, references)
        }

        val todos = mutableListOf<RdToDo>()
        todos.add(getToDo())

        val comment = RdIntelligentComment(authors, Date(), content, invariants, references, todos, null, 0, RdTextRange(0, 0))
    }
}