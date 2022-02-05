package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.core.domain.core.GroupOfLineComments
import com.intelligentComments.core.domain.core.MultilineComment
import com.intelligentComments.core.domain.impl.ContentProcessingStrategyImpl
import com.intelligentComments.core.domain.rd.DocCommentFromRd
import com.intelligentComments.core.domain.rd.GroupOfLineCommentsFromRd
import com.intelligentComments.core.domain.rd.MultilineCommentFromRd
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.project.Project
import com.intellij.util.application
import com.jetbrains.rd.ide.model.RdComment
import com.jetbrains.rd.ide.model.RdDocComment
import com.jetbrains.rd.ide.model.RdGroupOfLineComments
import com.jetbrains.rd.ide.model.RdMultilineComment
import com.jetbrains.rd.platform.util.getLogger

class RiderCommentsCreator {
  companion object {
    private val logger = getLogger<RiderCommentsCreator>()
  }


  fun tryCreateComment(
    rdComment: RdComment,
    editor: Editor,
    commentRange: RangeMarker
  ) : CommentBase? {
    application.assertIsDispatchThread()
    return when(rdComment) {
      is RdDocComment -> createDocComment(rdComment, editor.project ?: return null, commentRange)
      is RdGroupOfLineComments -> createGroupOfLinesComment(rdComment, editor.project ?: return null, commentRange)
      is RdMultilineComment -> createMultilineComments(rdComment, editor.project ?: return null, commentRange)
      else -> throw IllegalArgumentException(rdComment.javaClass.name)
    }
  }

  fun createDocComment(
    rdDocComment: RdDocComment,
    project: Project,
    commentRange: RangeMarker
  ) : DocComment {
    application.assertIsDispatchThread()
    val docComment = DocCommentFromRd(rdDocComment, project, commentRange)
    val segmentsPreprocessingStrategy = project.service<ContentProcessingStrategyImpl>()
    docComment.content.content.processSegments(segmentsPreprocessingStrategy)
    return docComment
  }

  fun createGroupOfLinesComment(
    rdGroupOfLineComments: RdGroupOfLineComments,
    project: Project,
    commentRange: RangeMarker
  ) : GroupOfLineComments {
    application.assertIsDispatchThread()
    return GroupOfLineCommentsFromRd(rdGroupOfLineComments, project, commentRange)
  }

  fun createMultilineComments(
    rdMultilineComment: RdMultilineComment,
    project: Project,
    commentRange: RangeMarker
  ) : MultilineComment {
    application.assertIsDispatchThread()
    return MultilineCommentFromRd(rdMultilineComment, project, commentRange)
  }
}