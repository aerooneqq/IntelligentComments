package com.intelligentComments.core.domain.core

import java.nio.file.Path

interface Reference : UniqueEntity

interface CodeEntityReference : Reference {
  val rawMemberName: String
}

interface ExternalReference : Reference {

}

interface HttpLinkReference : ExternalReference {
  val rawLink: String
}