package com.intelligentcomments.core.utils

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager

class DocumentUtils {
  companion object {
    fun tryGetMoniker(document: Document, project: Project): String? {
      return PsiDocumentManager.getInstance(project).getPsiFile(document)?.virtualFile?.path
    }
  }
}