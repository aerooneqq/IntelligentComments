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
    }

    val RdIntelligentCommentAuthor = structdef {
        field("Name", PredefinedType.string)
        field("Date", PredefinedType.dateTime)
    }

    val RdIntelligentCommentContent = classdef {
        list("Segments", RdContentSegment)
    }

    val RdContentSegment = basestruct {
    }

    val RdTextSegment = structdef extends RdContentSegment {
        field("Text", RdHighlightedText)
    }

    val RdInvariant = basestruct {

    }

    val RdTextInvariant = structdef extends RdInvariant {
        field("Text", PredefinedType.string)
        call("Evaluate", PredefinedType.int, PredefinedType.bool)
    }

    val RdReference = basestruct {

    }

    val RdFileBasedReference = basestruct extends RdReference {
        field("FilePath", PredefinedType.string)
    }

    val RdDependencyReference = structdef extends RdFileBasedReference {
        field("ReferenceName", PredefinedType.string)
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

    val RdFontStyle = enum {
        + "Regular"
        + "Bold"
    }

    init {
        map("Documents", SolutionModel.RdDocumentId, RdDocumentCommentsModel)
    }
}