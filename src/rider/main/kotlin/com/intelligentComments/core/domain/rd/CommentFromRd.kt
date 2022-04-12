package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.comments.RiderCommentsCreator
import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*

abstract class CommentFromRd(
  project: Project,
  rangeMarker: RangeMarker,
  final override val correspondingHighlighter: RangeHighlighter
) : UniqueEntityImpl(), CommentBase {
  protected val commentsCreator = project.service<RiderCommentsCreator>()

  final override val parent: Parentable? = null
  final override val identifier: CommentIdentifier = CommentIdentifier.create(project, rangeMarker)

  abstract override fun recreate(editor: Editor): CommentBase
}

class DocCommentFromRd(
  private val rdDocComment: RdDocComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker,
) : CommentFromRd(project, rangeMarker, highlighter), DocComment {
  override val content: IntelligentCommentContent = IntelligentCommentContentFromRd(
    rdDocComment.content ?: RdIntelligentCommentContent(RdContentSegments(emptyList())),
    this,
    project
  )

  override fun isValid(): Boolean {
    return content.content.segments.isNotEmpty() && identifier.isValid
  }

  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createDocComment(rdDocComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

abstract class CommentWithOneTextSegmentFromRd(
  rdComment: RdCommentWithOneTextSegment,
  project: Project,
  rangeMarker: RangeMarker,
  highlighter: RangeHighlighter,
  ) : CommentFromRd(project, rangeMarker, highlighter), CommentWithOneTextSegment {
  final override val text = ContentSegmentFromRd.getFrom(rdComment.text, this, project) as TextContentSegment
}

class GroupOfLineCommentsFromRd(
  private val rdComment: RdGroupOfLineComments,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneTextSegmentFromRd(rdComment, project, rangeMarker, highlighter), GroupOfLineComments {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createGroupOfLinesComment(rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class MultilineCommentFromRd(
  private val rdMultilineComment: RdMultilineComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneTextSegmentFromRd(rdMultilineComment, project, rangeMarker, highlighter), MultilineComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createMultilineComments(rdMultilineComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class InvalidCommentFromRd(
  private val rdInvalidComment: RdInvalidComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneTextSegmentFromRd(rdInvalidComment, project, rangeMarker, highlighter), InvalidComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createInvalidComment(rdInvalidComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class DisablingCommentFromRd(
  private val rdDisableInspectionComment: RdDisableInspectionComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneTextSegmentFromRd(rdDisableInspectionComment, project, rangeMarker, highlighter), DisablingInspectionsComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createDisablingInspectionsComment(rdDisableInspectionComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class InlineReferenceCommentFromRd(
  private val rdComment: RdInlineReferenceComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentFromRd(project, rangeMarker, highlighter), InlineReferenceComment {
  override val text: TextContentSegment


  init {
    val parent = this
    text = object : UniqueEntityImpl(), TextContentSegment {
      override val highlightedText: HighlightedText
      override val parent: Parentable = parent

      init {
        val segment = rdComment.referenceContentSegment
        var text = segment.nameText.toIdeaHighlightedText(project, this)
        val description = segment.description
        if (description != null) {
          text = text.mergeWith(description.toIdeaHighlightedText(project, this))
        }

        highlightedText = text
      }
    }
  }


  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createInlineReferenceComment(rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

abstract class CommentWithOneContentSegmentsFromRd(
  rdComment: RdCommentWithOneContentSegments,
  project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentFromRd(project, rangeMarker, highlighter), CommentWithOneContentSegments {
  final override val content: ContentSegments

  init {
    content = ContentSegmentsFromRd(rdComment.content.content, this, project)
  }
}

class ToDoInlineCommentFromRd(
  private val rdComment: RdInlineToDoComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneContentSegmentsFromRd(rdComment, project, highlighter, rangeMarker), ToDoInlineComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createToDoComment(rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class HackInlineCommentFromRd(
  private val rdComment: RdInlineHackComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneContentSegmentsFromRd(rdComment, project, highlighter, rangeMarker), HackInlineComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createHackComment(rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class InvariantInlineCommentFromRd(
  private val rdComment: RdInlineInvariantComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneContentSegmentsFromRd(rdComment, project, highlighter, rangeMarker), InvariantInlineComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createInvariantComment(rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}