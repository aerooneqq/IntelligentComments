package com.intelligentComments.ui.util

class HashUtil {
  companion object {
    private const val mod = 1000007

    fun <T> calculateHashFor(items: Collection<T>): Int {
      var hash = 1
      for (item in items) hash *= item.hashCode() % mod
      return hash % mod
    }

    fun hashCode(vararg hashes: Int): Int {
      var result = 1
      for (hash in hashes) {
        result = (result * hash) % mod
      }

      return result
    }
  }
}