package com.intelligentcomments.core.comments.states

data class EditorId(val moniker: String, val tabOrder: Int = 0) {
  companion object {
    val emptyInstance = EditorId("", 0)
  }
}