package com.intelligentComments.core.domain.core

interface Invariant : ContentSegment

interface TextInvariant : Invariant {
  val name: HighlightedText
  val description: HighlightedText
}