package com.intelligentComments.core.utils

import com.intellij.util.application
import com.jetbrains.rd.platform.util.getLogger
import java.util.LinkedList
import java.util.Queue


typealias UpdateAction = ((Unit) -> Unit) -> Unit

class OnePooledActionWithEdtContinuationAtTimeScheduler {
  companion object {
    private val logger = getLogger<OnePooledActionWithEdtContinuationAtTimeScheduler>()
  }

  private var isProcessing = false
  private val queue: Queue<UpdateAction> = LinkedList()


  fun queueUpdate(update: UpdateAction) {
    application.assertIsDispatchThread()

    queue.add(update)
    if (!isProcessing) {
      processQueue()
    }
  }

  private fun processQueue() {
    application.assertIsDispatchThread()
    if (queue.size == 0) {
      isProcessing = false
      return
    }

    val action = queue.poll()
    isProcessing = true

    application.executeOnPooledThread {
      try {
        action {
          processQueue()
        }
      } catch (ex: Exception) {
        logger.error(ex)
      }
    }
  }
}