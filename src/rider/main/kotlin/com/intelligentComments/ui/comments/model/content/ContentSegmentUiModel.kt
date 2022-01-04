package com.intelligentComments.ui.comments.model.content

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.*
import com.intelligentComments.ui.comments.model.UiInteractionModelBase
import com.intelligentComments.ui.comments.model.content.code.CodeSegmentUiModel
import com.intelligentComments.ui.comments.model.content.example.ExampleSegmentUiModel
import com.intelligentComments.ui.comments.model.content.exceptions.ExceptionUiModel
import com.intelligentComments.ui.comments.model.content.exceptions.GroupedExceptionUiModel
import com.intelligentComments.ui.comments.model.content.image.ImageContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.list.ListContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.paragraphs.ParagraphUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedParamsUiModel
import com.intelligentComments.ui.comments.model.content.params.GroupedTypeParamsUiModel
import com.intelligentComments.ui.comments.model.content.params.ParameterUiModel
import com.intelligentComments.ui.comments.model.content.params.TypeParamUiModel
import com.intelligentComments.ui.comments.model.content.remarks.GroupedRemarksUiModel
import com.intelligentComments.ui.comments.model.content.remarks.RemarksUiModel
import com.intelligentComments.ui.comments.model.content.`return`.GroupedReturnUiModel
import com.intelligentComments.ui.comments.model.content.`return`.ReturnUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.GroupedSeeAlsoUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoUiModel
import com.intelligentComments.ui.comments.model.content.summary.GroupedSummaryUiModel
import com.intelligentComments.ui.comments.model.content.summary.SummaryUiModel
import com.intelligentComments.ui.comments.model.content.table.TableContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.text.TextContentSegmentUiModel
import com.intelligentComments.ui.util.HashUtil
import com.intellij.openapi.project.Project

abstract class ContentSegmentUiModel(
  project: Project,
  parent: UiInteractionModelBase?,
  private val segment: ContentSegment
) : UiInteractionModelBase(project, parent) {
  companion object {
    fun getFrom(project: Project, parent: UiInteractionModelBase?, segment: ContentSegment): ContentSegmentUiModel {
      //ToDo: sth is definitely wrong here
      return when (segment) {
        is TextContentSegment -> TextContentSegmentUiModel(project, parent, segment)
        is ListContentSegment -> ListContentSegmentUiModel(project, parent, segment)
        is ImageContentSegment -> ImageContentSegmentUiModel(project, parent, segment)
        is TableContentSegment -> TableContentSegmentUiModel(project, parent, segment)
        is ParagraphContentSegment -> ParagraphUiModel(project, parent, segment)
        is TypeParamSegment -> TypeParamUiModel(project, parent, segment)
        is ParameterSegment -> ParameterUiModel(project, parent, segment)
        is ReturnSegment -> ReturnUiModel(project, parent, segment)
        is RemarksSegment -> RemarksUiModel(project, parent, segment)
        is ExceptionSegment -> ExceptionUiModel(project, parent, segment)
        is SeeAlsoSegment -> SeeAlsoUiModel.getFor(project, parent, segment)
        is GroupedSeeAlsoSegments -> GroupedSeeAlsoUiModel(project, parent, segment)
        is GroupedReturnSegments -> GroupedReturnUiModel(project, parent, segment)
        is GroupedParamSegments -> GroupedParamsUiModel(project, parent, segment)
        is GroupedTypeParamSegments -> GroupedTypeParamsUiModel(project, parent, segment)
        is GroupedExceptionsSegments -> GroupedExceptionUiModel(project, parent, segment)
        is GroupedSummarySegments -> GroupedSummaryUiModel(project, parent, segment)
        is GroupedRemarksSegments -> GroupedRemarksUiModel(project, parent, segment)
        is ExampleContentSegment -> ExampleSegmentUiModel(project, parent, segment)
        is SummaryContentSegment -> SummaryUiModel(project, parent, segment)
        is CodeSegment -> CodeSegmentUiModel(project, parent, segment)
        else -> throw IllegalArgumentException(segment.javaClass.name)
      }
    }
  }


  override fun calculateStateHash(): Int {
    return HashUtil.hashCode(segment.hashCode())
  }
}