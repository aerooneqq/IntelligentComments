package model.rider

import com.jetbrains.rd.generator.nova.*
import com.jetbrains.rider.model.nova.ide.SolutionModel
import com.jetbrains.rider.model.nova.ide.SolutionModel.RdDocumentId
import com.jetbrains.rider.model.nova.ide.SolutionModel.RdTextRange
import com.jetbrains.rider.model.nova.ide.SolutionModel.TextControlId


@Suppress("unused")
object RdCommentsModel : Ext(SolutionModel.Solution) {
  val RdCommentFoldingModel = structdef extends SolutionModel.HighlighterModel {
    field("CommentIdentifier", PredefinedType.int)
    field("Comment", RdComment)
  }

  val RdDocumentComments = structdef {
    field("Comments", immutableList(RdComment))
  }

  val RdComment = basestruct {
    field("Range", RdTextRange)
  }

  val RdInlineReferenceComment = structdef extends RdComment {
    field("ReferenceContentSegment", RdInlineReferenceContentSegment)
  }

  val RdCommentWithOneTextSegment = basestruct extends RdComment {
    field("Text", RdTextSegment)
  }

  val RdDisableInspectionComment = structdef extends RdCommentWithOneTextSegment {
  }
  
  val RdToDoComment = structdef extends RdComment {
    field("ToDo", RdToDoContentSegment)
  }

  val RdInvalidComment = structdef extends RdCommentWithOneTextSegment {
  }

  val RdGroupOfLineComments = structdef extends RdCommentWithOneTextSegment {
  }

  val RdDocComment = structdef extends RdComment {
    field("Content", RdIntelligentCommentContent.nullable).optional
  }

  val RdMultilineComment = structdef extends RdCommentWithOneTextSegment {
  }

  val RdContentSegment = basestruct { }

  val RdSegmentWithContent = basestruct extends RdContentSegment {
    field("Content", RdContentSegments)
  }

  val RdDefaultSegmentWithContent = structdef extends RdSegmentWithContent { }

  val RdIntelligentCommentContent = structdef extends RdSegmentWithContent {
  }

  val RdContentSegments = structdef {
    field("Content", immutableList(RdContentSegment))
  }

  val RdParam = openstruct extends RdSegmentWithContent {
    field("Name", RdHighlightedText)
  }

  val RdTypeParam = structdef extends RdParam { }

  val RdSummarySegment = structdef extends RdSegmentWithContent { }

  val RdRemarksSegment = structdef extends RdSegmentWithContent { }

  val RdParagraphSegment = structdef extends RdSegmentWithContent { }

  val RdReturnSegment = structdef extends RdSegmentWithContent { }

  val RdExampleSegment = structdef extends RdSegmentWithContent { }

  val RdValueSegment = structdef extends RdSegmentWithContent { }

  val RdCodeContentSegment = structdef extends RdContentSegment {
    field("Code", RdHighlightedText)
    field("HighlightingRequestId", PredefinedType.int)
  }

  val RdSeeAlsoContentSegment = basestruct extends RdContentSegment {
    field("Description", RdHighlightedText)
  }

  val RdSeeAlsoMemberContentSegment = structdef extends RdSeeAlsoContentSegment {
    field("Reference", RdReference)
  }

  val RdSeeAlsoLinkContentSegment = structdef extends RdSeeAlsoContentSegment {
    field("Reference", RdReference)
  }

  val RdExceptionsSegment = structdef extends RdSegmentWithContent {
    field("Name", RdHighlightedText)
    field("ExceptionReference", RdReference.nullable).optional
  }

  val RdTextSegment = structdef extends RdContentSegment {
    field("Text", RdHighlightedText)
  }

  val RdImageSegment = structdef extends RdContentSegment {
    field("SourceReference", RdReference)
    field("Description", RdHighlightedText)
  }

  val ListKind = enum {
    + "Bullet"
    + "Number"
  }

  val RdListSegment = structdef extends RdContentSegment {
    field("ListKind", ListKind)
    field("ListContent", immutableList(RdListItem))
    field("Header", RdHighlightedText.nullable).optional
  }

  val RdListItem = structdef {
    field("Header", RdContentSegments.nullable).optional
    field("Description", RdContentSegments.nullable).optional
  }

  val RdTableSegment = structdef extends RdContentSegment {
    field("Rows", immutableList(RdTableRow))
    field("Header", RdHighlightedText.nullable).optional
  }

  val RdTableRow = structdef {
    field("Cells", immutableList(RdTableCell))
  }

  val RdTableCell = structdef {
    field("Content", RdContentSegments)
    field("Properties", RdTableCellProperties.nullable).optional
  }

  val RdTableCellProperties = structdef {
    field("HorizontalAlignment", RdHorizontalAlignment)
    field("VerticalAlignment", RdVerticalAlignment)
    field("IsHeader", PredefinedType.bool)
  }

  val RdHorizontalAlignment = enum {
    +"Center"
    +"Left"
    +"Right"
  }

  val RdVerticalAlignment = enum {
    +"Center"
    +"Top"
    +"Bottom"
  }

  val RdInvariant = basestruct extends RdContentSegment { }

  val RdTextInvariant = structdef extends RdInvariant {
    field("Name", RdHighlightedText)
    field("Description", RdDefaultSegmentWithContent)
    call("Evaluate", PredefinedType.int, PredefinedType.bool)
  }

  val RdReference = basestruct {
    field("RawValue", PredefinedType.string)
  }

  val RdInvariantReference = structdef extends RdReference {
    field("InvariantName", PredefinedType.string)
  }

  val RdProxyReference = structdef extends RdReference {
    field("RealReferenceId", PredefinedType.int)
  }

  val RdExternalReference = basestruct extends RdReference { }

  val RdHttpLinkReference = structdef extends RdExternalReference { }

  val RdFileReference = structdef extends RdExternalReference {
    field("Path", PredefinedType.string)
  }

  val RdCodeEntityReference = basestruct extends RdReference { }

  val RdXmlDocCodeEntityReference = structdef extends RdCodeEntityReference { }

  val RdSandboxCodeEntityReference = structdef extends RdCodeEntityReference {
    field("SandboxFileId", PredefinedType.string)
    field("OriginalDocumentId", RdDocumentId.nullable)
    field("Range", RdTextRange)
  }

  val RdLangWordReference = structdef extends RdReference { }

  val RdReferenceContentSegment = structdef extends RdContentSegment {
    field("Reference", RdReference)
    field("Name", RdHighlightedText)
    field("Description", RdDefaultSegmentWithContent)
  }

  val RdInlineReferenceContentSegment = structdef extends RdContentSegment {
    field("NameText", RdHighlightedText)
    field("Description", RdHighlightedText.nullable).optional
  }

  val RdHighlightedText = structdef {
    field("Text", PredefinedType.string)
    field("Highlighters", immutableList(RdTextHighlighter).nullable).optional
  }

  val RdTextHighlighter = structdef {
    field("Key", PredefinedType.string)
    field("StartOffset", PredefinedType.int)
    field("EndOffset", PredefinedType.int)
    field("Attributes", RdTextAttributes)
    field("BackgroundStyle", RdBackgroundStyle.nullable).optional
    field("Animation", RdTextAnimation.nullable).optional
    field("References", immutableList(RdReference).nullable).optional
    field("IsResharperHighlighter", PredefinedType.bool.nullable).optional
    field("ErrorSquiggles", RdSquiggles.nullable).optional
  }

  val RdSquiggles = structdef {
    field("Kind", RdSquigglesKind)
    field("ColorKey", PredefinedType.string)
  }

  val RdSquigglesKind = enum {
    + "Wave"
    + "Dotted"
  }

  val RdTextAttributes = structdef {
    field("FontStyle", RdFontStyle.nullable).optional
    field("Underline", PredefinedType.bool.nullable).optional
    field("FontWeight", PredefinedType.float.nullable).optional
  }

  val RdBackgroundStyle = structdef {
    field("BackgroundColor", RdColor)
    field("RoundedRect", PredefinedType.bool)
    field("LeftRightMargin", PredefinedType.int)
  }

  val RdColor = structdef {
    field("Hex", PredefinedType.string)
  }

  val RdTextAnimation = basestruct { }
  val RdUnderlineTextAnimation = structdef extends RdTextAnimation { }
  val RdForegroundColorAnimation = structdef extends RdTextAnimation {
    field("HoveredColor", RdColor)
  }

  val RdPredefinedForegroundColorAnimation = structdef extends RdTextAnimation {
    field("Key", PredefinedType.string)
  }

  val RdFontStyle = enum {
    + "Regular"
    + "Bold"
    + "Italic"
  }

  val RdToDo = basestruct {
    field("Text", RdHighlightedText)
    field("BlockingReferences", immutableList(RdReference))
  }

  val RdToDoWithTickets = structdef extends RdToDo {
    field("Tickets", immutableList(RdTicket))
  }

  val RdToDoContentSegment = structdef extends RdContentSegment {
    field("ToDo", RdToDoWithTickets)
  }

  val RdTicket = structdef {
    field("Url", PredefinedType.string)
    field("ShortName", PredefinedType.string)
  }

  val RdHack = basestruct {
    field("Name", PredefinedType.string)
    field("Description", RdContentSegments)
    field("BlockingReferences", immutableList(RdReference))
  }

  val RdHackWithTickets = structdef extends RdHack {
    field("Tickets", immutableList(RdTicket))
  }

  val RdHackContentSegment = structdef extends RdContentSegment {
    field("Hack", RdHackWithTickets)
  }

  val RdCodeHighlightingRequest = structdef {
    field("Id", PredefinedType.int)
  }

  val RdCommentClickDocRequest = structdef {
    field("ResolveRequest", RdReferenceResolveRequest)
  }

  val RdReferenceResolveRequest = structdef {
    field("Reference", RdReference)
    field("TextControlId", TextControlId)
  }

  val RdNavigationRequest = structdef {
    field("ResolveRequest", RdReferenceResolveRequest)
  }

  val RdResolveResult = basestruct { }

  val RdInvalidResolveResult = structdef extends RdResolveResult {
    field("Error", RdHighlightedText.nullable)
  }

  val RdInvariantResolveResult = structdef extends RdResolveResult {
    field("Invariant", RdTextInvariant)
  }

  init {
    call("HighlightCode", RdCodeHighlightingRequest, RdHighlightedText.nullable)
    call("RequestClickDoc", RdCommentClickDocRequest, PredefinedType.int.nullable)
    call("PerformNavigation", RdNavigationRequest, PredefinedType.void)
    call("ResolveReference", RdReferenceResolveRequest, RdResolveResult)
  }
}