package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.`return`.ReturnUiModel
import com.intelligentComments.ui.comments.model.content.exceptions.ExceptionUiModel
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.paragraphs.ParagraphUiModel
import com.intelligentComments.ui.comments.model.content.params.ParameterUiModel
import com.intelligentComments.ui.comments.model.content.remarks.RemarksUiModel
import com.intelligentComments.ui.comments.model.content.table.TableContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

abstract class ContentSegmentUiModel(
  project: Project,
  private val segment: ContentSegment
) : UiInteractionModelBase(project) {
  companion object {
    fun getFrom(project: Project, segment: ContentSegment): ContentSegmentUiModel {
      return when (segment) {
        is TextContentSegment -> TextContentSegmentUiModel(project, segment)
        is ListContentSegment -> ListContentSegmentUiModel(project, segment)
        is ImageContentSegment -> ImageContentSegmentUiModel(project, segment)
        is TableContentSegment -> TableContentSegmentUiModel(project, segment)
        is ParagraphContentSegment -> ParagraphUiModel(project, segment)
        is ParameterSegment -> ParameterUiModel(project, segment)
        is ReturnSegment -> ReturnUiModel(project, segment)
        is RemarksSegment -> RemarksUiModel(project, segment)
        is ExceptionSegment -> ExceptionUiModel(project, segment)
        else -> throw IllegalArgumentException(segment.toString())
      }
    }
  }

  override fun hashCode(): Int = segment.hashCode() % HashUtil.mod
  override fun equals(other: Any?): Boolean = other is ContentSegmentUiModel && other.hashCode() == hashCode()
}