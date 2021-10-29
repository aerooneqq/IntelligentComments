package model.rider

import com.jetbrains.rd.generator.nova.*
import com.jetbrains.rider.model.nova.ide.SolutionModel

@Suppress("unused")
object RdCommentsModel : Ext(SolutionModel.Solution) {
    val RdDocumentCommentsModel = classdef {
        list("Comments", RdComment)
    }

    val RdComment = baseclass {
        field("Offset", PredefinedType.int)
        field("DocumentId", SolutionModel.RdDocumentId)
    }

    val RdIntelligentComment = classdef extends RdComment {
        list("Authors", RdIntelligentCommentAuthor)
        property("Date", PredefinedType.dateTime)
        property("Content", RdIntelligentCommentContent)

        list("Invariants", RdInvariant)
        list("References", RdReference)
        list("ToDos", RdToDo)
        list("Hacks", RdHack)
    }

    val RdIntelligentCommentAuthor = structdef {
        field("Name", PredefinedType.string)
        field("Date", PredefinedType.dateTime)
    }

    val RdIntelligentCommentContent = classdef {
        field("Content", RdContentSegments)
    }

    val RdContentSegment = basestruct { }

    val RdContentSegments = structdef {
        field("Content", immutableList(RdContentSegment))
    }

    val RdTextSegment = structdef extends RdContentSegment {
        field("Text", RdHighlightedText)
    }

    val RdImageSegment = basestruct extends RdContentSegment {
        field("Description", RdHighlightedText)
    }

    val RdFileBasedImageSegment = structdef extends RdImageSegment {
        field("Path", PredefinedType.string)
    }

    val RdListSegment = structdef extends RdContentSegment {
        field("ListContent", immutableList(RdContentSegments))
        field("Header", RdHighlightedText)
    }

    val RdTableSegment = structdef extends RdContentSegment {
        field("Header", RdHighlightedText)
        field("Rows", immutableList(RdTableRow))
    }

    val RdTableRow = structdef {
        field("Cells", immutableList(RdTableCell))
    }

    val RdTableCell = structdef {
        field("Content", RdContentSegments)
        field("Properties", RdTableCellProperties.nullable).optional
    }

    val RdTableCellProperties = structdef {
        field("HorizontalAlignment", RdHorizontalAlignment)
        field("VerticalAlignment", RdVerticalAlignment)
        field("IsHeader", PredefinedType.bool)
    }

    val RdHorizontalAlignment = enum {
        + "Center"
        + "Left"
        + "Right"
    }

    val RdVerticalAlignment = enum {
        + "Center"
        + "Top"
        + "Bottom"
    }

    val RdInvariant = basestruct { }

    val RdTextInvariant = structdef extends RdInvariant {
        field("Text", PredefinedType.string)
        call("Evaluate", PredefinedType.int, PredefinedType.bool)
    }

    val RdReference = basestruct {
        field("ReferenceName", PredefinedType.string)
    }

    val RdFileBasedReference = basestruct extends RdReference {
        field("FilePath", PredefinedType.string)
    }

    val RdDependencyReference = structdef extends RdFileBasedReference {
        field("DependencyDescription", PredefinedType.string)
    }

    val RdHighlightedText = structdef {
        field("Text", PredefinedType.string)
        field("Highlighters", immutableList(RdTextHighlighter).nullable).optional
    }

    val RdTextHighlighter = structdef {
        field("Key", PredefinedType.string)
        field("StartOffset", PredefinedType.int)
        field("EndOffset", PredefinedType.int)
        field("Attributes", RdTextAttributes)
        field("BackgroundStyle", RdBackgroundStyle.nullable).optional
        field("Animation", RdTextAnimation.nullable).optional
    }

    val RdTextAttributes = structdef {
        field("FontStyle", RdFontStyle.nullable).optional
        field("Underline", PredefinedType.bool.nullable).optional
        field("FontWeight", PredefinedType.float.nullable).optional
    }

    val RdBackgroundStyle = structdef {
        field("BackgroundColor", RdColor)
        field("RoundedRect", PredefinedType.bool)
    }

    val RdColor = structdef {
        field("Hex", PredefinedType.string)
    }

    val RdTextAnimation = basestruct { }
    val RdUnderlineTextAnimation = structdef extends RdTextAnimation { }
    val RdForegroundColorAnimation = structdef extends RdTextAnimation {
        field("HoveredColor", RdColor)
    }

    val RdPredefinedForegroundColorAnimation = structdef extends RdTextAnimation {
        field("Key", PredefinedType.string)
    }

    val RdFontStyle = enum {
        + "Regular"
        + "Bold"
    }

    val RdToDo = basestruct {
        field("Author", RdIntelligentCommentAuthor)
        field("Name", PredefinedType.string)
        field("Description", RdContentSegments)
        field("BlockingReferences", immutableList(RdReference))
    }

    val RdToDoWithTickets = structdef extends RdToDo {
        field("Tickets", immutableList(RdTicket))
    }

    val RdTicket = structdef {
        field("Url", PredefinedType.string)
        field("ShortName", PredefinedType.string)
    }

    val RdHack = basestruct {
        field("Name", PredefinedType.string)
        field("Description", RdContentSegments)
        field("BlockingReferences", immutableList(RdReference))
    }

    val RdHackWithTickets = structdef extends RdHack {
        field("Tickets", immutableList(RdTicket))
    }


    init {
        map("Documents", SolutionModel.RdDocumentId, RdDocumentCommentsModel)
    }
}