package com.intelligentComments.core.problemsView.tree

import com.intellij.util.ui.tree.AbstractTreeModel
import com.jetbrains.rd.util.addUnique
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import com.jetbrains.rd.util.reactive.AddRemove
import com.jetbrains.rider.model.SolutionAnalysisErrorWithIgnore
import com.jetbrains.rider.model.SolutionAnalysisErrors
import com.jetbrains.rider.model.SolutionAnalysisModel
import javax.swing.tree.TreePath

internal class IntelligentCommentsTreeModel(
  model: SolutionAnalysisModel,
  private val lifetime: Lifetime
) : AbstractTreeModel() {
  private val fileWatchingLifetimes = mutableMapOf<String, LifetimeDefinition>()
  private val files = ListWithSet<String, FileTreeModel>()

  val filesCount
    get() = files.size


  init {
    model.fileErrors.adviseAddRemove(lifetime) { args, _, value ->
      when (args) {
        AddRemove.Add -> {
          handleFileAddition(value)
        }
        AddRemove.Remove -> {
          handleFileDeletion(value)
        }
        else -> {
          throw IllegalArgumentException(args.toString())
        }
      }
    }
  }

  private fun handleFileAddition(value: SolutionAnalysisErrors) {
    val fileName = value.fileName
    if (files.contains(fileName)) return

    val fileWatchingLifetimeDef = lifetime.createNested()
    val fileWatchingLifetime = fileWatchingLifetimeDef.lifetime
    val fileModel = FileTreeModel(fileName, value)

    fileModel.errorsChanged.advise(fileWatchingLifetime) {
      dispatchNodeChanges(TreePath(arrayOf(root, fileModel)), it)
    }

    fun addFileIfNeededAndUpdateErrors(errors: List<IntelligentCommentErrorTreeModel>) {
      val isVisible = errors.isNotEmpty()
      files.add(fileName, fileModel, isVisible)

      val path = TreePath(arrayOf(root))
      if (isVisible) {
        val dto = files.makeVisibleIfNeeded(fileName)
        if (dto != null) dispatchNodeChanges(path, dto)
      } else {
        val dtos = files.makeInvisibleIfNeeded(fileName)
        for (dto in dtos) {
          dispatchNodeChanges(path, dto)
        }
      }

      fileModel.updateErrors(errors)
    }

    addFileIfNeededAndUpdateErrors(selectIntelligentCommentsErrors(fileModel, value.errors.valueOrNull?.errors ?: emptyList()))
    fileWatchingLifetimes.addUnique(fileWatchingLifetime, fileName, fileWatchingLifetimeDef)

    value.requireUpdates.set(true)
    value.errors.advise(fileWatchingLifetime) {
      val errors = selectIntelligentCommentsErrors(fileModel, it.errors)
      addFileIfNeededAndUpdateErrors(errors)
    }
  }

  private fun dispatchNodeChanges(path: TreePath, dto: NodesChangeDto) {
    when (dto.nodeChangeKind) {
      NodeChangeKind.Add -> {
        treeNodesInserted(path, dto.changedIndices, dto.changedNodes)
      }
      NodeChangeKind.Remove -> {
        treeNodesRemoved(path, dto.changedIndices, dto.changedNodes)
      }
      NodeChangeKind.Change -> {
        treeNodesRemoved(path, dto.changedIndices, dto.changedNodes)
      }
      else -> throw IllegalArgumentException(dto.nodeChangeKind.toString())
    }
  }

  private fun handleFileDeletion(value: SolutionAnalysisErrors) {
    deleteFile(value.fileName)
  }

  private fun deleteFile(fileName: String) {
    val (fileModel, index) = files.remove(fileName) ?: return
    treeNodesRemoved(TreePath(root), intArrayOf(index), arrayOf(fileModel))
    fileWatchingLifetimes[fileName]?.terminate()
  }

  private fun selectIntelligentCommentsErrors(
    parentModel: FileTreeModel,
    errors: Collection<SolutionAnalysisErrorWithIgnore>
  ): List<IntelligentCommentErrorTreeModel> {
    return errors.mapNotNull { it.toIntelligentCommentErrorTreeModel(parentModel) }
  }

  operator fun get(index: Int) = files[index]
  operator fun get(model: FileTreeModel) = files[model]
  override fun getRoot(): Any {
    return this
  }

  override fun getChild(parent: Any?, index: Int): Any {
    return when(parent) {
      is IntelligentCommentsTreeModel -> parent[index]
      is FileTreeModel -> parent[index]
      else -> throw IllegalArgumentException(parent?.javaClass?.name)
    }
  }

  override fun getChildCount(parent: Any?): Int {
    return when(parent) {
      is IntelligentCommentsTreeModel -> parent.filesCount
      is FileTreeModel -> parent.errorsCount
      is IntelligentCommentErrorTreeModel -> 0
      else -> throw IllegalArgumentException(parent?.javaClass?.name)
    }
  }

  override fun isLeaf(node: Any?): Boolean {
    return node is IntelligentCommentReferenceErrorTreeModel
  }

  override fun getIndexOfChild(parent: Any?, child: Any?): Int {
    if (parent is IntelligentCommentsTreeModel && child is FileTreeModel) {
      return parent[child]
    }

    if (parent is FileTreeModel && child is IntelligentCommentErrorTreeModel) {
      return parent[child]
    }

    throw IllegalArgumentException(parent?.javaClass?.name + "::" + child?.javaClass?.name)
  }
}