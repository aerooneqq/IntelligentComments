package com.intelligentComments.core.domain.core

interface Invariant : UniqueEntity

interface TextInvariant : Invariant {
  val text: String
}

interface TextInvariantContentSegment : ContentSegment {
  val invariant: TextInvariant
}