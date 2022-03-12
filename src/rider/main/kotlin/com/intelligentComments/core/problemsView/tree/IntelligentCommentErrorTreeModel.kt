package com.intelligentComments.core.problemsView.tree

import com.jetbrains.rider.model.SolutionAnalysisErrorWithIgnore

internal interface IntelligentCommentErrorTreeModel {
  val parentModel: FileTreeModel
  val originalError: SolutionAnalysisErrorWithIgnore
  val presentationText: String
}

internal abstract class IntelligentCommentErrorTreeModelBase(
  override val parentModel: FileTreeModel,
  override val originalError: SolutionAnalysisErrorWithIgnore,
  override val presentationText: String
) : IntelligentCommentErrorTreeModel

internal class IntelligentCommentReferenceErrorTreeModel(
  parentModel: FileTreeModel,
  originalError: SolutionAnalysisErrorWithIgnore,
  presentationText: String
) : IntelligentCommentErrorTreeModelBase(parentModel, originalError, presentationText)


internal fun SolutionAnalysisErrorWithIgnore.toIntelligentCommentErrorTreeModel(
  parentModel: FileTreeModel
): IntelligentCommentErrorTreeModel? {
  if (this.text.startsWith("Failed to resolve referenceSource")) {
    return IntelligentCommentReferenceErrorTreeModel(parentModel, this, this.text)
  }

  return null
}