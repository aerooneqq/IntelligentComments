package com.intelligentcomments.core.problemsView.tree

import com.jetbrains.rd.platform.util.getLogger

internal class VisibilityBasedItemsStorage<TId, TValue> {
  companion object {
    private val logger = getLogger<VisibilityBasedItemsStorage<*, *>>()
  }

  private var thread: Thread = Thread.currentThread()
  private val invisibleItems = mutableListOf<TValue>()
  private val visibleItems = mutableListOf<TValue>()
  private val currentVisibleKeys = mutableMapOf<TId, TValue>()
  private val currentInvisibleKeys = mutableMapOf<TId, TValue>()


  val size: Int
    get() {
      assertThreading()
      return visibleItems.size
    }

  operator fun get(index: Int): TValue {
    assertThreading()
    return visibleItems[index]
  }

  operator fun get(value: TValue): Int {
    assertThreading()
    return visibleItems.indexOf(value)
  }

  operator fun get(id: TId): TValue? {
    assertThreading()
    return currentVisibleKeys[id]
  }


  fun add(id: TId, value: TValue, isVisible: Boolean): Boolean {
    assertThreading()
    if (currentVisibleKeys.contains(id) || currentInvisibleKeys.contains(id)) return false

    if (isVisible) {
      currentVisibleKeys[id] = value
      visibleItems.add(value)
    } else {
      currentInvisibleKeys[id] = value
      invisibleItems.add(value)
    }

    return true
  }

  fun makeVisibleIfNeeded(id: TId): NodesChangeDto? {
    assertThreading()
    if (currentVisibleKeys.contains(id)) return null

    val invisibleItem = currentInvisibleKeys[id] ?: return null

    visibleItems.add(invisibleItem)
    currentVisibleKeys[id] = invisibleItem
    return NodesChangeDto(NodeChangeKind.Add, IntArray(visibleItems.size) { it }, arrayOf(invisibleItem))
  }

  fun makeInvisibleIfNeeded(id: TId): List<NodesChangeDto> {
    assertThreading()
    val visibleItem = currentVisibleKeys[id] ?: return emptyList()
    val index = visibleItems.indexOf(visibleItem)

    currentVisibleKeys.remove(id)
    visibleItems.removeAt(index)

    invisibleItems.add(visibleItem)
    currentInvisibleKeys[id] = visibleItem

    val changes = mutableListOf<NodesChangeDto>()
    if (visibleItems.size > 0) {
      changes.add(
        NodesChangeDto(
          NodeChangeKind.Change,
          IntArray(visibleItems.size - index) { it + index },
          visibleItems.subList(index, visibleItems.size).map { it as Any }.toTypedArray()
        )
      )
    }

    changes.add(NodesChangeDto(NodeChangeKind.Remove, IntArray(1) { visibleItems.size }, arrayOf(visibleItem)))
    return changes
  }

  private fun assertThreading() {
    val currentThread = Thread.currentThread()
    if (currentThread != thread) {
      logger.error("Incorrect thread (${currentThread.name}) was used to access, must be ${thread.name}")
    }
  }

  fun contains(id: TId): Boolean {
    assertThreading()
    return currentVisibleKeys.contains(id)
  }

  fun remove(id: TId): Pair<TValue, Int>? {
    assertThreading()
    val invisibleValue = currentInvisibleKeys.remove(id)
    if (invisibleValue != null) {
      invisibleItems.remove(invisibleValue)
    }

    val visibleValue = currentVisibleKeys.remove(id)
    if (visibleValue != null) {
      val index = visibleItems.indexOf(visibleValue)
      visibleItems.remove(visibleValue)
      return Pair(visibleValue, index)
    }

    return null
  }
}