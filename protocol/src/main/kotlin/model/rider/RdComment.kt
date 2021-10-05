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

    val RdContentSegment = baseclass {
    }

    val RdTextSegment = classdef extends RdContentSegment {
        property("Text", PredefinedType.string)
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

    init {
        map("Documents", SolutionModel.RdDocumentId, RdDocumentCommentsModel)
    }
}