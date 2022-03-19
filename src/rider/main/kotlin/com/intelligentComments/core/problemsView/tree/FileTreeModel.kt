package com.intelligentComments.core.problemsView.tree

import com.jetbrains.rd.util.reactive.Signal
import com.jetbrains.rider.model.SolutionAnalysisErrors

internal class FileTreeModel(
  val fileName: String,
  val rdModel: SolutionAnalysisErrors
) {
  private val errors = mutableListOf<IntelligentCommentErrorTreeModel>()

  val errorsChanged = Signal<NodesChangeDto>()
  val icon = rdModel.icon

  val errorsCount
    get() = errors.size

  operator fun get(index: Int) = errors[index]
  operator fun get(model: IntelligentCommentErrorTreeModel) = errors.indexOf(model)

  fun updateErrors(newErrors: Collection<IntelligentCommentErrorTreeModel>) {
    val dto = NodesChangeDto(NodeChangeKind.Remove, IntArray(errorsCount) { it }, errors.toTypedArray())

    errors.clear()
    errorsChanged.fire(dto)

    errors.addAll(newErrors)
    errorsChanged.fire(NodesChangeDto(NodeChangeKind.Add, IntArray(errorsCount) { it }, newErrors.toTypedArray()))
  }

  fun setRequiresUpdate(requiresUpdate: Boolean) {
    rdModel.requireUpdates.set(requiresUpdate)
  }
}