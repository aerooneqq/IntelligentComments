package com.intelligentComments.core.namesToolWindow.tree

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.util.ui.tree.AbstractTreeModel
import com.jetbrains.rd.ide.model.*
import icons.ReSharperIcons
import javax.swing.JTree
import javax.swing.tree.TreePath


internal abstract class NameTreeModel {
  companion object {
    fun create(entity: RdNamedEntityItem) = when (entity) {
      is RdTodoItem -> TodoNameTreeModel(entity)
      is RdHackItem -> HackNameTreeModel(entity)
      is RdInvariantItem -> InvariantNameTreeModel(entity)
      else -> throw IllegalArgumentException(entity.javaClass.name)
    }
  }

  abstract val entity: RdNamedEntityItem
}

internal class TodoNameTreeModel(override val entity: RdTodoItem) : NameTreeModel()
internal class HackNameTreeModel(override val entity: RdHackItem) : NameTreeModel()
internal class InvariantNameTreeModel(override val entity: RdInvariantItem) : NameTreeModel()

internal class FileTreeModel(val rdModel: RdFileNames) {
  private val names = mutableListOf<NameTreeModel>()

  init {
    names.addAll(rdModel.entities.map { NameTreeModel.create(it) })
  }

  val allNames: List<NameTreeModel>
    get() = names

  val size
    get() = names.size


  operator fun get(index: Int): NameTreeModel {
    return names[index]
  }

  operator fun get(model: NameTreeModel): Int {
    return names.indexOf(model)
  }
}
internal class NamesTree : AbstractTreeModel() {
  private val files = mutableListOf<FileTreeModel>()

  val size
    get() = files.size



  fun addOrUpdate(model: FileTreeModel) {
    val fileModelIndex = files.indexOfFirst { it.rdModel.file.id == model.rdModel.file.id }
    if (fileModelIndex == -1) {
      files.add(model)
      treeNodesInserted(TreePath(root), IntArray(1) { files.size - 1 }, arrayOf(model))
      treeNodesInserted(TreePath(arrayOf(root, model)), IntArray(model.size) { it }, arrayOf(model.allNames))
      return
    }

    val oldModel = files[fileModelIndex]
    treeNodesRemoved(TreePath(arrayOf(root, oldModel)), IntArray(oldModel.size) { it }, arrayOf(oldModel.allNames))

    files[fileModelIndex] = model
    treeNodesInserted(TreePath(arrayOf(root, model)), IntArray(model.size) { it }, arrayOf())
  }

  operator fun get(index: Int): FileTreeModel {
    return files[index]
  }

  operator fun get(model: FileTreeModel): Int {
    return files.indexOf(model)
  }

  override fun getRoot(): Any {
    return this
  }

  override fun getChild(parent: Any?, index: Int): Any {
    return when(parent) {
      is NamesTree -> parent[index]
      is FileTreeModel -> parent[index]
      else -> throw IllegalArgumentException(parent?.javaClass?.name)
    }
  }

  override fun getChildCount(parent: Any?): Int {
    return when(parent) {
      is NamesTree -> parent.size
      is FileTreeModel -> parent.size
      else -> throw IllegalArgumentException(parent?.javaClass?.name)
    }
  }

  override fun isLeaf(node: Any?): Boolean {
    return node is NameTreeModel
  }

  override fun getIndexOfChild(parent: Any?, child: Any?): Int {
    if (parent is NamesTree && child is FileTreeModel) {
      return parent[child]
    }

    if (parent is FileTreeModel && child is NameTreeModel) {
      return parent[child]
    }

    val childAndParent = "${parent?.javaClass?.name}::${child?.javaClass?.name}"
    throw IllegalArgumentException(childAndParent)
  }
}

internal class NameCellRenderer(private val project: Project) : ColoredTreeCellRenderer() {
  override fun customizeCellRenderer(
    tree: JTree,
    value: Any?,
    selected: Boolean,
    expanded: Boolean,
    isLeaf: Boolean,
    rowIndex: Int,
    focused: Boolean
  ) {
    when (value) {
      is NamesTree -> {
        icon = ReSharperIcons.ProjectModel.SolutionFolder
        append(project.name)
      }

      is FileTreeModel -> {
        icon = ReSharperIcons.PsiCSharp.Csharp
        append(value.rdModel.file.name)
      }

      is TodoNameTreeModel -> {
        icon = AllIcons.General.TodoDefault
        append(value.entity.presentation)
      }
    }
  }
}