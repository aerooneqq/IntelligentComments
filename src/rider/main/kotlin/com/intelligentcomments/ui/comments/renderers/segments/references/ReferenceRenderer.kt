package com.intelligentcomments.ui.comments.renderers.segments.references

import com.intelligentcomments.ui.comments.model.content.references.ReferenceUiModel
import com.intelligentcomments.ui.comments.renderers.segments.TextRendererBase
import com.intelligentcomments.ui.core.RectangleModelBuildContributor
import com.intelligentcomments.ui.core.Renderer

interface ReferenceRenderer : Renderer, RectangleModelBuildContributor

class ReferencesRendererImpl(model: ReferenceUiModel) : TextRendererBase(model.name), ReferenceRenderer