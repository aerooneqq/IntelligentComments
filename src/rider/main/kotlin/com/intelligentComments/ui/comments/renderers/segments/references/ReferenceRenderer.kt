package com.intelligentComments.ui.comments.renderers.segments.references

import com.intelligentComments.ui.comments.model.content.references.ReferenceUiModel
import com.intelligentComments.ui.comments.renderers.segments.TextRendererBase
import com.intelligentComments.ui.core.RectangleModelBuildContributor
import com.intelligentComments.ui.core.Renderer

interface ReferenceRenderer : Renderer, RectangleModelBuildContributor

class ReferencesRendererImpl(model: ReferenceUiModel) : TextRendererBase(model.name), ReferenceRenderer