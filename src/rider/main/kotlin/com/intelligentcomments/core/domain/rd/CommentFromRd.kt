package com.intelligentcomments.core.domain.rd

import com.intelligentcomments.core.comments.RiderCommentsCreator
import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.ui.comments.model.CommentUiModelBase
import com.intelligentcomments.ui.comments.model.CommentWithOneContentSegmentsUiModel
import com.intelligentcomments.ui.comments.model.CommentWithOneTextSegmentUiModel
import com.intelligentcomments.ui.comments.model.DocCommentUiModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*

abstract class CommentFromRd(
  protected val project: Project,
  rangeMarker: RangeMarker,
  protected val editor: Editor,
  final override val correspondingHighlighter: RangeHighlighter
) : UniqueEntityImpl(), CommentBase {
  protected val commentsCreator = project.service<RiderCommentsCreator>()

  final override val parent: Parentable? = null
  final override val identifier: CommentIdentifier = CommentIdentifier.create(project, rangeMarker, editor)
  final override val uiModel: CommentUiModelBase
    get() = createUiModelInternal()

  internal abstract fun createUiModelInternal(): CommentUiModelBase
  abstract override fun recreate(editor: Editor): CommentBase
}

class DocCommentFromRd(
  private val rdDocComment: RdDocComment,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker,
) : CommentFromRd(project, rangeMarker, editor, highlighter), DocComment {
  override val content: IntelligentCommentContent = IntelligentCommentContentFromRd(
    rdDocComment.content ?: RdIntelligentCommentContent(RdContentSegments(emptyList())),
    this,
    project
  )

  override fun createUiModelInternal(): CommentUiModelBase {
    return DocCommentUiModel(this, project, editor)
  }

  override fun isValid(): Boolean {
    return content.content.segments.isNotEmpty() && identifier.isValid
  }

  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createDocComment(editor, rdDocComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

abstract class CommentWithOneTextSegmentFromRd(
  rdComment: RdCommentWithOneTextSegment,
  project: Project,
  editor: Editor,
  rangeMarker: RangeMarker,
  highlighter: RangeHighlighter,
  ) : CommentFromRd(project, rangeMarker, editor, highlighter), CommentWithOneTextSegment {
  final override val text = ContentSegmentFromRd.getFrom(rdComment.text, this, project) as TextContentSegment

  final override fun createUiModelInternal(): CommentUiModelBase {
    return CommentWithOneTextSegmentUiModel(this, project, editor)
  }
}

class GroupOfLineCommentsFromRd(
  private val rdComment: RdGroupOfLineComments,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneTextSegmentFromRd(rdComment, project, editor, rangeMarker, highlighter), GroupOfLineComments {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createGroupOfLinesComment(editor, rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class MultilineCommentFromRd(
  private val rdMultilineComment: RdMultilineComment,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneTextSegmentFromRd(rdMultilineComment, project, editor, rangeMarker, highlighter), MultilineComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createMultilineComments(editor, rdMultilineComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class InvalidCommentFromRd(
  private val rdInvalidComment: RdInvalidComment,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneTextSegmentFromRd(rdInvalidComment, project, editor, rangeMarker, highlighter), InvalidComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createInvalidComment(editor, rdInvalidComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class DisablingCommentFromRd(
  private val rdDisableInspectionComment: RdDisableInspectionComment,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneTextSegmentFromRd(rdDisableInspectionComment, project, editor, rangeMarker, highlighter), DisablingInspectionsComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createDisablingInspectionsComment(editor, rdDisableInspectionComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class InlineReferenceCommentFromRd(
  private val rdComment: RdInlineReferenceComment,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentFromRd(project, rangeMarker, editor, highlighter), InlineReferenceComment {
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

  final override fun createUiModelInternal(): CommentUiModelBase {
    return CommentWithOneTextSegmentUiModel(this, project, editor)
  }

  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createInlineReferenceComment(editor, rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

abstract class CommentWithOneContentSegmentsFromRd(
  rdComment: RdCommentWithOneContentSegments,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentFromRd(project, rangeMarker, editor, highlighter), CommentWithOneContentSegments {
  final override val content: ContentSegments

  init {
    content = ContentSegmentsFromRd(rdComment.content.content, this, project)
  }
}

abstract class InlineCommentFromRd(
  rdComment: RdInlineComment,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentWithOneContentSegmentsFromRd(rdComment, project, editor, highlighter, rangeMarker), InlineComment {
  final override fun createUiModelInternal(): CommentUiModelBase {
    return CommentWithOneContentSegmentsUiModel(this, project, editor)
  }
}

class ToDoInlineCommentFromRd(
  private val rdComment: RdInlineToDoComment,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : InlineCommentFromRd(rdComment, project, editor, highlighter, rangeMarker), ToDoInlineComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createToDoComment(editor, rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class HackInlineCommentFromRd(
  private val rdComment: RdInlineHackComment,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : InlineCommentFromRd(rdComment, project, editor, highlighter, rangeMarker), HackInlineComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createHackComment(editor, rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class InvariantInlineCommentFromRd(
  private val rdComment: RdInlineInvariantComment,
  project: Project,
  editor: Editor,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : InlineCommentFromRd(rdComment, project, editor, highlighter, rangeMarker), InvariantInlineComment {
  override fun recreate(editor: Editor): CommentBase {
    return commentsCreator.createInvariantComment(editor, rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}