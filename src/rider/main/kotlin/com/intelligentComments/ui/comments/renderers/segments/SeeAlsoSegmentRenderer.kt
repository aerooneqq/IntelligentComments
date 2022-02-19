package com.intelligentComments.ui.comments.renderers.segments

import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoLinkUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoMemberUiModel
import com.intelligentComments.ui.comments.model.content.seeAlso.SeeAlsoUiModel

abstract class SeeAlsoSegmentRenderer(
  seeAlsoUiModel: SeeAlsoUiModel
) : LeftTextHeaderAndRightContentRenderer(seeAlsoUiModel.header, listOf(seeAlsoUiModel.description)) {
  companion object {
    fun getFor(seeAlsoUiModel: SeeAlsoUiModel): SeeAlsoSegmentRenderer {
      //ToDo: seems useless
      return when(seeAlsoUiModel) {
        is SeeAlsoLinkUiModel -> SeeAlsoLinkSegmentRenderer(seeAlsoUiModel)
        is SeeAlsoMemberUiModel -> SeeAlsoMemberSegmentRenderer(seeAlsoUiModel)
        else -> throw IllegalArgumentException(seeAlsoUiModel.javaClass.name)
      }
    }
  }
}

class SeeAlsoLinkSegmentRenderer(seeAlso: SeeAlsoLinkUiModel) : SeeAlsoSegmentRenderer(seeAlso)
class SeeAlsoMemberSegmentRenderer(seeAlso: SeeAlsoMemberUiModel) : SeeAlsoSegmentRenderer(seeAlso)