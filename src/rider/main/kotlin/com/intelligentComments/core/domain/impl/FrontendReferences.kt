package com.intelligentComments.core.domain.impl

import com.intelligentComments.core.domain.core.FrontendInvariantReference
import com.intelligentComments.core.domain.core.TextInvariantSegment
import com.intelligentComments.core.domain.core.UniqueEntityImpl

class FrontendInvariantReferenceImpl(
  override val invariant: TextInvariantSegment
) : UniqueEntityImpl(), FrontendInvariantReference {
  override val rawValue: String = invariant.name.text
}