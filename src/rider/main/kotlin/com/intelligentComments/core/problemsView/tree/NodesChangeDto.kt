package com.intelligentComments.core.problemsView.tree


enum class NodeChangeKind {
  Add,
  Remove,
  Change
}

internal data class NodesChangeDto(
  val nodeChangeKind: NodeChangeKind,
  val changedIndices: IntArray,
  val changedNodes: Array<Any>
)