package com.intelligentComments.core.domain.core

interface InvariantSegment : ContentSegment

interface TextInvariantSegment : InvariantSegment {
  val name: HighlightedText
  val description: HighlightedText
}