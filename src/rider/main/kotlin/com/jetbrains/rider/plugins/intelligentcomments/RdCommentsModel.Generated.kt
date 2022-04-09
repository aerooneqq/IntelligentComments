@file:Suppress("EXPERIMENTAL_API_USAGE","EXPERIMENTAL_UNSIGNED_LITERALS","PackageDirectoryMismatch","UnusedImport","unused","LocalVariableName","CanBeVal","PropertyName","EnumEntryName","ClassName","ObjectPropertyName","UnnecessaryVariable","SpellCheckingInspection")
package com.jetbrains.rd.ide.model

import com.jetbrains.rd.framework.*
import com.jetbrains.rd.framework.base.*
import com.jetbrains.rd.framework.impl.*

import com.jetbrains.rd.util.lifetime.*
import com.jetbrains.rd.util.reactive.*
import com.jetbrains.rd.util.string.*
import com.jetbrains.rd.util.*
import kotlin.reflect.KClass



/**
 * #### Generated from [RdComment.kt:11]
 */
class RdCommentsModel private constructor(
    private val _highlightCode: RdCall<RdCodeHighlightingRequest, RdHighlightedText?>,
    private val _requestClickDoc: RdCall<RdCommentClickDocRequest, Int?>,
    private val _performNavigation: RdCall<RdNavigationRequest, Unit>,
    private val _resolveReference: RdCall<RdReferenceResolveRequest, RdResolveResult>,
    private val _namedEntitiesChange: RdSignal<RdFileNames>,
    private val _evaluate: RdCall<Int, Boolean>
) : RdExtBase() {
    //companion
    
    companion object : ISerializersOwner {
        
        override fun registerSerializersCore(serializers: ISerializers)  {
            serializers.register(RdCommentFoldingModel)
            serializers.register(RdDocumentComments)
            serializers.register(RdInlineReferenceComment)
            serializers.register(RdDisableInspectionComment)
            serializers.register(RdToDoComment)
            serializers.register(RdHackComment)
            serializers.register(RdInvalidComment)
            serializers.register(RdGroupOfLineComments)
            serializers.register(RdDocComment)
            serializers.register(RdMultilineComment)
            serializers.register(RdDefaultSegmentWithContent)
            serializers.register(RdIntelligentCommentContent)
            serializers.register(RdContentSegments)
            serializers.register(RdParam)
            serializers.register(RdTypeParam)
            serializers.register(RdSummarySegment)
            serializers.register(RdRemarksSegment)
            serializers.register(RdParagraphSegment)
            serializers.register(RdReturnSegment)
            serializers.register(RdExampleSegment)
            serializers.register(RdValueSegment)
            serializers.register(RdCodeContentSegment)
            serializers.register(RdSeeAlsoMemberContentSegment)
            serializers.register(RdSeeAlsoLinkContentSegment)
            serializers.register(RdExceptionsSegment)
            serializers.register(RdTextSegment)
            serializers.register(RdImageSegment)
            serializers.register(ListKind.marshaller)
            serializers.register(RdListSegment)
            serializers.register(RdListItem)
            serializers.register(RdTableSegment)
            serializers.register(RdTableRow)
            serializers.register(RdTableCell)
            serializers.register(RdTableCellProperties)
            serializers.register(RdHorizontalAlignment.marshaller)
            serializers.register(RdVerticalAlignment.marshaller)
            serializers.register(RdTextInvariant)
            serializers.register(RdNamedEntityReference)
            serializers.register(RdProxyReference)
            serializers.register(RdHttpLinkReference)
            serializers.register(RdFileReference)
            serializers.register(RdXmlDocCodeEntityReference)
            serializers.register(RdSandboxCodeEntityReference)
            serializers.register(RdLangWordReference)
            serializers.register(RdReferenceContentSegment)
            serializers.register(RdInlineReferenceContentSegment)
            serializers.register(RdHighlightedText)
            serializers.register(RdTextHighlighter)
            serializers.register(RdSquiggles)
            serializers.register(RdSquigglesKind.marshaller)
            serializers.register(RdTextAttributes)
            serializers.register(RdBackgroundStyle)
            serializers.register(RdColor)
            serializers.register(RdUnderlineTextAnimation)
            serializers.register(RdForegroundColorAnimation)
            serializers.register(RdPredefinedForegroundColorAnimation)
            serializers.register(RdFontStyle.marshaller)
            serializers.register(RdToDoTextContentSegment)
            serializers.register(RdToDoContentSegment)
            serializers.register(RdTicketContentSegment)
            serializers.register(RdHackTextContentSegment)
            serializers.register(RdHackContentSegment)
            serializers.register(RdCodeHighlightingRequest)
            serializers.register(RdCommentClickDocRequest)
            serializers.register(RdReferenceResolveRequest)
            serializers.register(RdReferenceNavigationRequest)
            serializers.register(RdSourceFileId)
            serializers.register(RdFileOffsetNavigationRequest)
            serializers.register(RdNameKind.marshaller)
            serializers.register(RdInvalidResolveResult)
            serializers.register(RdNamedEntityResolveResult)
            serializers.register(RdWebResourceResolveResult)
            serializers.register(RdFileInfo)
            serializers.register(RdHackItem)
            serializers.register(RdTodoItem)
            serializers.register(RdInvariantItem)
            serializers.register(RdFileNames)
            serializers.register(RdComment_Unknown)
            serializers.register(RdCommentWithOneTextSegment_Unknown)
            serializers.register(RdCommentWithOneContentSegments_Unknown)
            serializers.register(RdContentSegment_Unknown)
            serializers.register(RdSegmentWithContent_Unknown)
            serializers.register(RdParam_Unknown)
            serializers.register(RdSeeAlsoContentSegment_Unknown)
            serializers.register(RdInvariant_Unknown)
            serializers.register(RdReference_Unknown)
            serializers.register(RdExternalReference_Unknown)
            serializers.register(RdCodeEntityReference_Unknown)
            serializers.register(RdTextAnimation_Unknown)
            serializers.register(RdContentSegmentWithOptionalName_Unknown)
            serializers.register(RdNavigationRequest_Unknown)
            serializers.register(RdResolveResult_Unknown)
            serializers.register(RdNamedEntityItem_Unknown)
        }
        
        
        
        private val __RdHighlightedTextNullableSerializer = RdHighlightedText.nullable()
        private val __IntNullableSerializer = FrameworkMarshallers.Int.nullable()
        
        const val serializationHash = -8769098907990976963L
        
    }
    override val serializersOwner: ISerializersOwner get() = RdCommentsModel
    override val serializationHash: Long get() = RdCommentsModel.serializationHash
    
    //fields
    val highlightCode: IRdCall<RdCodeHighlightingRequest, RdHighlightedText?> get() = _highlightCode
    val requestClickDoc: IRdCall<RdCommentClickDocRequest, Int?> get() = _requestClickDoc
    val performNavigation: IRdCall<RdNavigationRequest, Unit> get() = _performNavigation
    val resolveReference: IRdCall<RdReferenceResolveRequest, RdResolveResult> get() = _resolveReference
    val namedEntitiesChange: ISignal<RdFileNames> get() = _namedEntitiesChange
    val evaluate: IRdCall<Int, Boolean> get() = _evaluate
    //methods
    //initializer
    init {
        bindableChildren.add("highlightCode" to _highlightCode)
        bindableChildren.add("requestClickDoc" to _requestClickDoc)
        bindableChildren.add("performNavigation" to _performNavigation)
        bindableChildren.add("resolveReference" to _resolveReference)
        bindableChildren.add("namedEntitiesChange" to _namedEntitiesChange)
        bindableChildren.add("evaluate" to _evaluate)
    }
    
    //secondary constructor
    internal constructor(
    ) : this(
        RdCall<RdCodeHighlightingRequest, RdHighlightedText?>(RdCodeHighlightingRequest, __RdHighlightedTextNullableSerializer),
        RdCall<RdCommentClickDocRequest, Int?>(RdCommentClickDocRequest, __IntNullableSerializer),
        RdCall<RdNavigationRequest, Unit>(AbstractPolymorphic(RdNavigationRequest), FrameworkMarshallers.Void),
        RdCall<RdReferenceResolveRequest, RdResolveResult>(RdReferenceResolveRequest, AbstractPolymorphic(RdResolveResult)),
        RdSignal<RdFileNames>(RdFileNames),
        RdCall<Int, Boolean>(FrameworkMarshallers.Int, FrameworkMarshallers.Bool)
    )
    
    //equals trait
    //hash code trait
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCommentsModel (")
        printer.indent {
            print("highlightCode = "); _highlightCode.print(printer); println()
            print("requestClickDoc = "); _requestClickDoc.print(printer); println()
            print("performNavigation = "); _performNavigation.print(printer); println()
            print("resolveReference = "); _resolveReference.print(printer); println()
            print("namedEntitiesChange = "); _namedEntitiesChange.print(printer); println()
            print("evaluate = "); _evaluate.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    override fun deepClone(): RdCommentsModel   {
        return RdCommentsModel(
            _highlightCode.deepClonePolymorphic(),
            _requestClickDoc.deepClonePolymorphic(),
            _performNavigation.deepClonePolymorphic(),
            _resolveReference.deepClonePolymorphic(),
            _namedEntitiesChange.deepClonePolymorphic(),
            _evaluate.deepClonePolymorphic()
        )
    }
    //contexts
}
val Solution.rdCommentsModel get() = getOrCreateExtension("rdCommentsModel", ::RdCommentsModel)



/**
 * #### Generated from [RdComment.kt:123]
 */
enum class ListKind {
    Bullet, 
    Number;
    
    companion object {
        val marshaller = FrameworkMarshallers.enum<ListKind>()
        
    }
}


/**
 * #### Generated from [RdComment.kt:258]
 */
data class RdBackgroundStyle (
    val backgroundColor: RdColor,
    val roundedRect: Boolean,
    val leftRightMargin: Int
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdBackgroundStyle> {
        override val _type: KClass<RdBackgroundStyle> = RdBackgroundStyle::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdBackgroundStyle  {
            val backgroundColor = RdColor.read(ctx, buffer)
            val roundedRect = buffer.readBool()
            val leftRightMargin = buffer.readInt()
            return RdBackgroundStyle(backgroundColor, roundedRect, leftRightMargin)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdBackgroundStyle)  {
            RdColor.write(ctx, buffer, value.backgroundColor)
            buffer.writeBool(value.roundedRect)
            buffer.writeInt(value.leftRightMargin)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdBackgroundStyle
        
        if (backgroundColor != other.backgroundColor) return false
        if (roundedRect != other.roundedRect) return false
        if (leftRightMargin != other.leftRightMargin) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + backgroundColor.hashCode()
        __r = __r*31 + roundedRect.hashCode()
        __r = __r*31 + leftRightMargin.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdBackgroundStyle (")
        printer.indent {
            print("backgroundColor = "); backgroundColor.print(printer); println()
            print("roundedRect = "); roundedRect.print(printer); println()
            print("leftRightMargin = "); leftRightMargin.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:92]
 */
class RdCodeContentSegment (
    val code: RdHighlightedText,
    val highlightingRequestId: Int
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdCodeContentSegment> {
        override val _type: KClass<RdCodeContentSegment> = RdCodeContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdCodeContentSegment  {
            val code = RdHighlightedText.read(ctx, buffer)
            val highlightingRequestId = buffer.readInt()
            return RdCodeContentSegment(code, highlightingRequestId)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdCodeContentSegment)  {
            RdHighlightedText.write(ctx, buffer, value.code)
            buffer.writeInt(value.highlightingRequestId)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdCodeContentSegment
        
        if (code != other.code) return false
        if (highlightingRequestId != other.highlightingRequestId) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + code.hashCode()
        __r = __r*31 + highlightingRequestId.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCodeContentSegment (")
        printer.indent {
            print("code = "); code.print(printer); println()
            print("highlightingRequestId = "); highlightingRequestId.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:202]
 */
abstract class RdCodeEntityReference (
    rawValue: String
) : RdReference (
    rawValue
) {
    //companion
    
    companion object : IAbstractDeclaration<RdCodeEntityReference> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdCodeEntityReference  {
            val objectStartPosition = buffer.position
            val rawValue = buffer.readString()
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdCodeEntityReference_Unknown(rawValue, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdCodeEntityReference_Unknown (
    rawValue: String,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdCodeEntityReference (
    rawValue
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdCodeEntityReference_Unknown> {
        override val _type: KClass<RdCodeEntityReference_Unknown> = RdCodeEntityReference_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdCodeEntityReference_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdCodeEntityReference_Unknown)  {
            buffer.writeString(value.rawValue)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdCodeEntityReference_Unknown
        
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCodeEntityReference_Unknown (")
        printer.indent {
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:309]
 */
data class RdCodeHighlightingRequest (
    val id: Int
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdCodeHighlightingRequest> {
        override val _type: KClass<RdCodeHighlightingRequest> = RdCodeHighlightingRequest::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdCodeHighlightingRequest  {
            val id = buffer.readInt()
            return RdCodeHighlightingRequest(id)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdCodeHighlightingRequest)  {
            buffer.writeInt(value.id)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdCodeHighlightingRequest
        
        if (id != other.id) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + id.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCodeHighlightingRequest (")
        printer.indent {
            print("id = "); id.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:264]
 */
data class RdColor (
    val hex: String
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdColor> {
        override val _type: KClass<RdColor> = RdColor::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdColor  {
            val hex = buffer.readString()
            return RdColor(hex)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdColor)  {
            buffer.writeString(value.hex)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdColor
        
        if (hex != other.hex) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + hex.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdColor (")
        printer.indent {
            print("hex = "); hex.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:21]
 */
abstract class RdComment (
    val range: RdTextRange
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdComment> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdComment  {
            val objectStartPosition = buffer.position
            val range = RdTextRange.read(ctx, buffer)
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdComment_Unknown(range, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:313]
 */
data class RdCommentClickDocRequest (
    val resolveRequest: RdReferenceResolveRequest
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdCommentClickDocRequest> {
        override val _type: KClass<RdCommentClickDocRequest> = RdCommentClickDocRequest::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdCommentClickDocRequest  {
            val resolveRequest = RdReferenceResolveRequest.read(ctx, buffer)
            return RdCommentClickDocRequest(resolveRequest)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdCommentClickDocRequest)  {
            RdReferenceResolveRequest.write(ctx, buffer, value.resolveRequest)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdCommentClickDocRequest
        
        if (resolveRequest != other.resolveRequest) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + resolveRequest.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCommentClickDocRequest (")
        printer.indent {
            print("resolveRequest = "); resolveRequest.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:12]
 */
class RdCommentFoldingModel (
    val commentIdentifier: Int,
    val comment: RdComment,
    layer: Int,
    isExactRange: Boolean,
    documentVersion: AbstractDocumentVersion,
    isGreedyToLeft: Boolean,
    isGreedyToRight: Boolean,
    isThinErrorStripeMark: Boolean,
    textToHighlight: String?,
    textAttributesKey: com.jetbrains.ide.model.highlighterRegistration.TextAttributesKeyModel?,
    id: Long,
    properties: com.jetbrains.ide.model.highlighterRegistration.HighlighterProperties,
    start: Int,
    end: Int
) : HighlighterModel (
    layer,
    isExactRange,
    documentVersion,
    isGreedyToLeft,
    isGreedyToRight,
    isThinErrorStripeMark,
    textToHighlight,
    textAttributesKey,
    id,
    properties,
    start,
    end
) {
    //companion
    
    companion object : IMarshaller<RdCommentFoldingModel> {
        override val _type: KClass<RdCommentFoldingModel> = RdCommentFoldingModel::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdCommentFoldingModel  {
            val layer = buffer.readInt()
            val isExactRange = buffer.readBool()
            val documentVersion = ctx.serializers.readPolymorphic<AbstractDocumentVersion>(ctx, buffer, AbstractDocumentVersion)
            val isGreedyToLeft = buffer.readBool()
            val isGreedyToRight = buffer.readBool()
            val isThinErrorStripeMark = buffer.readBool()
            val textToHighlight = buffer.readNullable { buffer.readString() }
            val textAttributesKey = buffer.readNullable { ctx.readInterned(buffer, "Protocol") { _, _ -> ctx.serializers.readPolymorphic<com.jetbrains.ide.model.highlighterRegistration.TextAttributesKeyModel>(ctx, buffer, com.jetbrains.ide.model.highlighterRegistration.TextAttributesKeyModel) } }
            val id = buffer.readLong()
            val properties = ctx.readInterned(buffer, "Protocol") { _, _ -> com.jetbrains.ide.model.highlighterRegistration.HighlighterProperties.read(ctx, buffer) }
            val start = buffer.readInt()
            val end = buffer.readInt()
            val commentIdentifier = buffer.readInt()
            val comment = ctx.serializers.readPolymorphic<RdComment>(ctx, buffer, RdComment)
            return RdCommentFoldingModel(commentIdentifier, comment, layer, isExactRange, documentVersion, isGreedyToLeft, isGreedyToRight, isThinErrorStripeMark, textToHighlight, textAttributesKey, id, properties, start, end)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdCommentFoldingModel)  {
            buffer.writeInt(value.layer)
            buffer.writeBool(value.isExactRange)
            ctx.serializers.writePolymorphic(ctx, buffer, value.documentVersion)
            buffer.writeBool(value.isGreedyToLeft)
            buffer.writeBool(value.isGreedyToRight)
            buffer.writeBool(value.isThinErrorStripeMark)
            buffer.writeNullable(value.textToHighlight) { buffer.writeString(it) }
            buffer.writeNullable(value.textAttributesKey) { ctx.writeInterned(buffer, it, "Protocol") { _, _, internedValue -> ctx.serializers.writePolymorphic(ctx, buffer, internedValue) } }
            buffer.writeLong(value.id)
            ctx.writeInterned(buffer, value.properties, "Protocol") { _, _, internedValue -> com.jetbrains.ide.model.highlighterRegistration.HighlighterProperties.write(ctx, buffer, internedValue) }
            buffer.writeInt(value.start)
            buffer.writeInt(value.end)
            buffer.writeInt(value.commentIdentifier)
            ctx.serializers.writePolymorphic(ctx, buffer, value.comment)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdCommentFoldingModel
        
        if (commentIdentifier != other.commentIdentifier) return false
        if (comment != other.comment) return false
        if (layer != other.layer) return false
        if (isExactRange != other.isExactRange) return false
        if (documentVersion != other.documentVersion) return false
        if (isGreedyToLeft != other.isGreedyToLeft) return false
        if (isGreedyToRight != other.isGreedyToRight) return false
        if (isThinErrorStripeMark != other.isThinErrorStripeMark) return false
        if (textToHighlight != other.textToHighlight) return false
        if (textAttributesKey != other.textAttributesKey) return false
        if (id != other.id) return false
        if (properties != other.properties) return false
        if (start != other.start) return false
        if (end != other.end) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + commentIdentifier.hashCode()
        __r = __r*31 + comment.hashCode()
        __r = __r*31 + layer.hashCode()
        __r = __r*31 + isExactRange.hashCode()
        __r = __r*31 + documentVersion.hashCode()
        __r = __r*31 + isGreedyToLeft.hashCode()
        __r = __r*31 + isGreedyToRight.hashCode()
        __r = __r*31 + isThinErrorStripeMark.hashCode()
        __r = __r*31 + if (textToHighlight != null) textToHighlight.hashCode() else 0
        __r = __r*31 + if (textAttributesKey != null) textAttributesKey.hashCode() else 0
        __r = __r*31 + id.hashCode()
        __r = __r*31 + properties.hashCode()
        __r = __r*31 + start.hashCode()
        __r = __r*31 + end.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCommentFoldingModel (")
        printer.indent {
            print("commentIdentifier = "); commentIdentifier.print(printer); println()
            print("comment = "); comment.print(printer); println()
            print("layer = "); layer.print(printer); println()
            print("isExactRange = "); isExactRange.print(printer); println()
            print("documentVersion = "); documentVersion.print(printer); println()
            print("isGreedyToLeft = "); isGreedyToLeft.print(printer); println()
            print("isGreedyToRight = "); isGreedyToRight.print(printer); println()
            print("isThinErrorStripeMark = "); isThinErrorStripeMark.print(printer); println()
            print("textToHighlight = "); textToHighlight.print(printer); println()
            print("textAttributesKey = "); textAttributesKey.print(printer); println()
            print("id = "); id.print(printer); println()
            print("properties = "); properties.print(printer); println()
            print("start = "); start.print(printer); println()
            print("end = "); end.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:36]
 */
abstract class RdCommentWithOneContentSegments (
    val content: RdDefaultSegmentWithContent,
    range: RdTextRange
) : RdComment (
    range
) {
    //companion
    
    companion object : IAbstractDeclaration<RdCommentWithOneContentSegments> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdCommentWithOneContentSegments  {
            val objectStartPosition = buffer.position
            val content = RdDefaultSegmentWithContent.read(ctx, buffer)
            val range = RdTextRange.read(ctx, buffer)
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdCommentWithOneContentSegments_Unknown(content, range, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdCommentWithOneContentSegments_Unknown (
    content: RdDefaultSegmentWithContent,
    range: RdTextRange,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdCommentWithOneContentSegments (
    content,
    range
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdCommentWithOneContentSegments_Unknown> {
        override val _type: KClass<RdCommentWithOneContentSegments_Unknown> = RdCommentWithOneContentSegments_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdCommentWithOneContentSegments_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdCommentWithOneContentSegments_Unknown)  {
            RdDefaultSegmentWithContent.write(ctx, buffer, value.content)
            RdTextRange.write(ctx, buffer, value.range)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdCommentWithOneContentSegments_Unknown
        
        if (content != other.content) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCommentWithOneContentSegments_Unknown (")
        printer.indent {
            print("content = "); content.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:29]
 */
abstract class RdCommentWithOneTextSegment (
    val text: RdTextSegment,
    range: RdTextRange
) : RdComment (
    range
) {
    //companion
    
    companion object : IAbstractDeclaration<RdCommentWithOneTextSegment> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdCommentWithOneTextSegment  {
            val objectStartPosition = buffer.position
            val text = RdTextSegment.read(ctx, buffer)
            val range = RdTextRange.read(ctx, buffer)
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdCommentWithOneTextSegment_Unknown(text, range, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdCommentWithOneTextSegment_Unknown (
    text: RdTextSegment,
    range: RdTextRange,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdCommentWithOneTextSegment (
    text,
    range
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdCommentWithOneTextSegment_Unknown> {
        override val _type: KClass<RdCommentWithOneTextSegment_Unknown> = RdCommentWithOneTextSegment_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdCommentWithOneTextSegment_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdCommentWithOneTextSegment_Unknown)  {
            RdTextSegment.write(ctx, buffer, value.text)
            RdTextRange.write(ctx, buffer, value.range)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdCommentWithOneTextSegment_Unknown
        
        if (text != other.text) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + text.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCommentWithOneTextSegment_Unknown (")
        printer.indent {
            print("text = "); text.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


class RdComment_Unknown (
    range: RdTextRange,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdComment (
    range
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdComment_Unknown> {
        override val _type: KClass<RdComment_Unknown> = RdComment_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdComment_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdComment_Unknown)  {
            RdTextRange.write(ctx, buffer, value.range)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdComment_Unknown
        
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdComment_Unknown (")
        printer.indent {
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:59]
 */
abstract class RdContentSegment (
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdContentSegment> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdContentSegment  {
            val objectStartPosition = buffer.position
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdContentSegment_Unknown(unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:284]
 */
abstract class RdContentSegmentWithOptionalName (
    val name: RdHighlightedText? = null
) : RdContentSegment (
) {
    //companion
    
    companion object : IAbstractDeclaration<RdContentSegmentWithOptionalName> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdContentSegmentWithOptionalName  {
            val objectStartPosition = buffer.position
            val name = buffer.readNullable { RdHighlightedText.read(ctx, buffer) }
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdContentSegmentWithOptionalName_Unknown(name, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdContentSegmentWithOptionalName_Unknown (
    name: RdHighlightedText? = null,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdContentSegmentWithOptionalName (
    name
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdContentSegmentWithOptionalName_Unknown> {
        override val _type: KClass<RdContentSegmentWithOptionalName_Unknown> = RdContentSegmentWithOptionalName_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdContentSegmentWithOptionalName_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdContentSegmentWithOptionalName_Unknown)  {
            buffer.writeNullable(value.name) { RdHighlightedText.write(ctx, buffer, it) }
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdContentSegmentWithOptionalName_Unknown
        
        if (name != other.name) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + if (name != null) name.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdContentSegmentWithOptionalName_Unknown (")
        printer.indent {
            print("name = "); name.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


class RdContentSegment_Unknown (
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdContentSegment (
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdContentSegment_Unknown> {
        override val _type: KClass<RdContentSegment_Unknown> = RdContentSegment_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdContentSegment_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdContentSegment_Unknown)  {
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdContentSegment_Unknown
        
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdContentSegment_Unknown (")
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:70]
 */
data class RdContentSegments (
    val content: List<RdContentSegment>
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdContentSegments> {
        override val _type: KClass<RdContentSegments> = RdContentSegments::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdContentSegments  {
            val content = buffer.readList { ctx.serializers.readPolymorphic<RdContentSegment>(ctx, buffer, RdContentSegment) }
            return RdContentSegments(content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdContentSegments)  {
            buffer.writeList(value.content) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdContentSegments
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdContentSegments (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:65]
 */
class RdDefaultSegmentWithContent (
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdDefaultSegmentWithContent> {
        override val _type: KClass<RdDefaultSegmentWithContent> = RdDefaultSegmentWithContent::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdDefaultSegmentWithContent  {
            val content = RdContentSegments.read(ctx, buffer)
            return RdDefaultSegmentWithContent(content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdDefaultSegmentWithContent)  {
            RdContentSegments.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdDefaultSegmentWithContent
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdDefaultSegmentWithContent (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:33]
 */
class RdDisableInspectionComment (
    text: RdTextSegment,
    range: RdTextRange
) : RdCommentWithOneTextSegment (
    text,
    range
) {
    //companion
    
    companion object : IMarshaller<RdDisableInspectionComment> {
        override val _type: KClass<RdDisableInspectionComment> = RdDisableInspectionComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdDisableInspectionComment  {
            val text = RdTextSegment.read(ctx, buffer)
            val range = RdTextRange.read(ctx, buffer)
            return RdDisableInspectionComment(text, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdDisableInspectionComment)  {
            RdTextSegment.write(ctx, buffer, value.text)
            RdTextRange.write(ctx, buffer, value.range)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdDisableInspectionComment
        
        if (text != other.text) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + text.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdDisableInspectionComment (")
        printer.indent {
            print("text = "); text.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:52]
 */
class RdDocComment (
    val content: RdIntelligentCommentContent? = null,
    range: RdTextRange
) : RdComment (
    range
) {
    //companion
    
    companion object : IMarshaller<RdDocComment> {
        override val _type: KClass<RdDocComment> = RdDocComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdDocComment  {
            val range = RdTextRange.read(ctx, buffer)
            val content = buffer.readNullable { RdIntelligentCommentContent.read(ctx, buffer) }
            return RdDocComment(content, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdDocComment)  {
            RdTextRange.write(ctx, buffer, value.range)
            buffer.writeNullable(value.content) { RdIntelligentCommentContent.write(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdDocComment
        
        if (content != other.content) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + if (content != null) content.hashCode() else 0
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdDocComment (")
        printer.indent {
            print("content = "); content.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:17]
 */
data class RdDocumentComments (
    val comments: List<RdComment>
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdDocumentComments> {
        override val _type: KClass<RdDocumentComments> = RdDocumentComments::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdDocumentComments  {
            val comments = buffer.readList { ctx.serializers.readPolymorphic<RdComment>(ctx, buffer, RdComment) }
            return RdDocumentComments(comments)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdDocumentComments)  {
            buffer.writeList(value.comments) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdDocumentComments
        
        if (comments != other.comments) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + comments.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdDocumentComments (")
        printer.indent {
            print("comments = "); comments.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:88]
 */
class RdExampleSegment (
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdExampleSegment> {
        override val _type: KClass<RdExampleSegment> = RdExampleSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdExampleSegment  {
            val content = RdContentSegments.read(ctx, buffer)
            return RdExampleSegment(content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdExampleSegment)  {
            RdContentSegments.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdExampleSegment
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdExampleSegment (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:109]
 */
class RdExceptionsSegment (
    val name: RdHighlightedText,
    val exceptionReference: RdReference? = null,
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdExceptionsSegment> {
        override val _type: KClass<RdExceptionsSegment> = RdExceptionsSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdExceptionsSegment  {
            val content = RdContentSegments.read(ctx, buffer)
            val name = RdHighlightedText.read(ctx, buffer)
            val exceptionReference = buffer.readNullable { ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference) }
            return RdExceptionsSegment(name, exceptionReference, content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdExceptionsSegment)  {
            RdContentSegments.write(ctx, buffer, value.content)
            RdHighlightedText.write(ctx, buffer, value.name)
            buffer.writeNullable(value.exceptionReference) { ctx.serializers.writePolymorphic(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdExceptionsSegment
        
        if (name != other.name) return false
        if (exceptionReference != other.exceptionReference) return false
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + if (exceptionReference != null) exceptionReference.hashCode() else 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdExceptionsSegment (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("exceptionReference = "); exceptionReference.print(printer); println()
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:192]
 */
abstract class RdExternalReference (
    rawValue: String
) : RdReference (
    rawValue
) {
    //companion
    
    companion object : IAbstractDeclaration<RdExternalReference> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdExternalReference  {
            val objectStartPosition = buffer.position
            val rawValue = buffer.readString()
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdExternalReference_Unknown(rawValue, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdExternalReference_Unknown (
    rawValue: String,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdExternalReference (
    rawValue
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdExternalReference_Unknown> {
        override val _type: KClass<RdExternalReference_Unknown> = RdExternalReference_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdExternalReference_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdExternalReference_Unknown)  {
            buffer.writeString(value.rawValue)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdExternalReference_Unknown
        
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdExternalReference_Unknown (")
        printer.indent {
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:360]
 */
data class RdFileInfo (
    val sourceFileId: RdSourceFileId,
    val id: Long,
    val name: String
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdFileInfo> {
        override val _type: KClass<RdFileInfo> = RdFileInfo::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdFileInfo  {
            val sourceFileId = RdSourceFileId.read(ctx, buffer)
            val id = buffer.readLong()
            val name = buffer.readString()
            return RdFileInfo(sourceFileId, id, name)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdFileInfo)  {
            RdSourceFileId.write(ctx, buffer, value.sourceFileId)
            buffer.writeLong(value.id)
            buffer.writeString(value.name)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdFileInfo
        
        if (sourceFileId != other.sourceFileId) return false
        if (id != other.id) return false
        if (name != other.name) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + sourceFileId.hashCode()
        __r = __r*31 + id.hashCode()
        __r = __r*31 + name.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdFileInfo (")
        printer.indent {
            print("sourceFileId = "); sourceFileId.print(printer); println()
            print("id = "); id.print(printer); println()
            print("name = "); name.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:375]
 */
data class RdFileNames (
    val nameKind: RdNameKind,
    val `file`: RdFileInfo,
    val entities: List<RdNamedEntityItem>
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdFileNames> {
        override val _type: KClass<RdFileNames> = RdFileNames::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdFileNames  {
            val nameKind = buffer.readEnum<RdNameKind>()
            val `file` = RdFileInfo.read(ctx, buffer)
            val entities = buffer.readList { ctx.serializers.readPolymorphic<RdNamedEntityItem>(ctx, buffer, RdNamedEntityItem) }
            return RdFileNames(nameKind, `file`, entities)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdFileNames)  {
            buffer.writeEnum(value.nameKind)
            RdFileInfo.write(ctx, buffer, value.`file`)
            buffer.writeList(value.entities) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdFileNames
        
        if (nameKind != other.nameKind) return false
        if (`file` != other.`file`) return false
        if (entities != other.entities) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + nameKind.hashCode()
        __r = __r*31 + `file`.hashCode()
        __r = __r*31 + entities.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdFileNames (")
        printer.indent {
            print("nameKind = "); nameKind.print(printer); println()
            print("file = "); `file`.print(printer); println()
            print("entities = "); entities.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:334]
 */
class RdFileOffsetNavigationRequest (
    val sourceFileId: RdSourceFileId,
    val offset: Int
) : RdNavigationRequest (
) {
    //companion
    
    companion object : IMarshaller<RdFileOffsetNavigationRequest> {
        override val _type: KClass<RdFileOffsetNavigationRequest> = RdFileOffsetNavigationRequest::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdFileOffsetNavigationRequest  {
            val sourceFileId = RdSourceFileId.read(ctx, buffer)
            val offset = buffer.readInt()
            return RdFileOffsetNavigationRequest(sourceFileId, offset)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdFileOffsetNavigationRequest)  {
            RdSourceFileId.write(ctx, buffer, value.sourceFileId)
            buffer.writeInt(value.offset)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdFileOffsetNavigationRequest
        
        if (sourceFileId != other.sourceFileId) return false
        if (offset != other.offset) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + sourceFileId.hashCode()
        __r = __r*31 + offset.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdFileOffsetNavigationRequest (")
        printer.indent {
            print("sourceFileId = "); sourceFileId.print(printer); println()
            print("offset = "); offset.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:198]
 */
class RdFileReference (
    val path: String,
    rawValue: String
) : RdExternalReference (
    rawValue
) {
    //companion
    
    companion object : IMarshaller<RdFileReference> {
        override val _type: KClass<RdFileReference> = RdFileReference::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdFileReference  {
            val rawValue = buffer.readString()
            val path = buffer.readString()
            return RdFileReference(path, rawValue)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdFileReference)  {
            buffer.writeString(value.rawValue)
            buffer.writeString(value.path)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdFileReference
        
        if (path != other.path) return false
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + path.hashCode()
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdFileReference (")
        printer.indent {
            print("path = "); path.print(printer); println()
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:278]
 */
enum class RdFontStyle {
    Regular, 
    Bold, 
    Italic;
    
    companion object {
        val marshaller = FrameworkMarshallers.enum<RdFontStyle>()
        
    }
}


/**
 * #### Generated from [RdComment.kt:270]
 */
class RdForegroundColorAnimation (
    val hoveredColor: RdColor
) : RdTextAnimation (
) {
    //companion
    
    companion object : IMarshaller<RdForegroundColorAnimation> {
        override val _type: KClass<RdForegroundColorAnimation> = RdForegroundColorAnimation::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdForegroundColorAnimation  {
            val hoveredColor = RdColor.read(ctx, buffer)
            return RdForegroundColorAnimation(hoveredColor)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdForegroundColorAnimation)  {
            RdColor.write(ctx, buffer, value.hoveredColor)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdForegroundColorAnimation
        
        if (hoveredColor != other.hoveredColor) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + hoveredColor.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdForegroundColorAnimation (")
        printer.indent {
            print("hoveredColor = "); hoveredColor.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:49]
 */
class RdGroupOfLineComments (
    text: RdTextSegment,
    range: RdTextRange
) : RdCommentWithOneTextSegment (
    text,
    range
) {
    //companion
    
    companion object : IMarshaller<RdGroupOfLineComments> {
        override val _type: KClass<RdGroupOfLineComments> = RdGroupOfLineComments::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdGroupOfLineComments  {
            val text = RdTextSegment.read(ctx, buffer)
            val range = RdTextRange.read(ctx, buffer)
            return RdGroupOfLineComments(text, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdGroupOfLineComments)  {
            RdTextSegment.write(ctx, buffer, value.text)
            RdTextRange.write(ctx, buffer, value.range)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdGroupOfLineComments
        
        if (text != other.text) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + text.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdGroupOfLineComments (")
        printer.indent {
            print("text = "); text.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:43]
 */
class RdHackComment (
    content: RdDefaultSegmentWithContent,
    range: RdTextRange
) : RdCommentWithOneContentSegments (
    content,
    range
) {
    //companion
    
    companion object : IMarshaller<RdHackComment> {
        override val _type: KClass<RdHackComment> = RdHackComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdHackComment  {
            val content = RdDefaultSegmentWithContent.read(ctx, buffer)
            val range = RdTextRange.read(ctx, buffer)
            return RdHackComment(content, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdHackComment)  {
            RdDefaultSegmentWithContent.write(ctx, buffer, value.content)
            RdTextRange.write(ctx, buffer, value.range)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdHackComment
        
        if (content != other.content) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdHackComment (")
        printer.indent {
            print("content = "); content.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:305]
 */
class RdHackContentSegment (
    val description: RdDefaultSegmentWithContent,
    name: RdHighlightedText? = null
) : RdContentSegmentWithOptionalName (
    name
) {
    //companion
    
    companion object : IMarshaller<RdHackContentSegment> {
        override val _type: KClass<RdHackContentSegment> = RdHackContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdHackContentSegment  {
            val name = buffer.readNullable { RdHighlightedText.read(ctx, buffer) }
            val description = RdDefaultSegmentWithContent.read(ctx, buffer)
            return RdHackContentSegment(description, name)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdHackContentSegment)  {
            buffer.writeNullable(value.name) { RdHighlightedText.write(ctx, buffer, it) }
            RdDefaultSegmentWithContent.write(ctx, buffer, value.description)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdHackContentSegment
        
        if (description != other.description) return false
        if (name != other.name) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + description.hashCode()
        __r = __r*31 + if (name != null) name.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdHackContentSegment (")
        printer.indent {
            print("description = "); description.print(printer); println()
            print("name = "); name.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:372]
 */
class RdHackItem (
    name: String,
    presentation: String,
    documentOffset: Int?
) : RdNamedEntityItem (
    name,
    presentation,
    documentOffset
) {
    //companion
    
    companion object : IMarshaller<RdHackItem> {
        override val _type: KClass<RdHackItem> = RdHackItem::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdHackItem  {
            val name = buffer.readString()
            val presentation = buffer.readString()
            val documentOffset = buffer.readNullable { buffer.readInt() }
            return RdHackItem(name, presentation, documentOffset)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdHackItem)  {
            buffer.writeString(value.name)
            buffer.writeString(value.presentation)
            buffer.writeNullable(value.documentOffset) { buffer.writeInt(it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdHackItem
        
        if (name != other.name) return false
        if (presentation != other.presentation) return false
        if (documentOffset != other.documentOffset) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + presentation.hashCode()
        __r = __r*31 + if (documentOffset != null) documentOffset.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdHackItem (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("presentation = "); presentation.print(printer); println()
            print("documentOffset = "); documentOffset.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:301]
 */
class RdHackTextContentSegment (
    val text: RdHighlightedText
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdHackTextContentSegment> {
        override val _type: KClass<RdHackTextContentSegment> = RdHackTextContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdHackTextContentSegment  {
            val text = RdHighlightedText.read(ctx, buffer)
            return RdHackTextContentSegment(text)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdHackTextContentSegment)  {
            RdHighlightedText.write(ctx, buffer, value.text)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdHackTextContentSegment
        
        if (text != other.text) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + text.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdHackTextContentSegment (")
        printer.indent {
            print("text = "); text.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:225]
 */
data class RdHighlightedText (
    val text: String,
    val highlighters: List<RdTextHighlighter>? = null
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdHighlightedText> {
        override val _type: KClass<RdHighlightedText> = RdHighlightedText::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdHighlightedText  {
            val text = buffer.readString()
            val highlighters = buffer.readNullable { buffer.readList { RdTextHighlighter.read(ctx, buffer) } }
            return RdHighlightedText(text, highlighters)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdHighlightedText)  {
            buffer.writeString(value.text)
            buffer.writeNullable(value.highlighters) { buffer.writeList(it) { v -> RdTextHighlighter.write(ctx, buffer, v) } }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdHighlightedText
        
        if (text != other.text) return false
        if (highlighters != other.highlighters) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + text.hashCode()
        __r = __r*31 + if (highlighters != null) highlighters.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdHighlightedText (")
        printer.indent {
            print("text = "); text.print(printer); println()
            print("highlighters = "); highlighters.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:159]
 */
enum class RdHorizontalAlignment {
    Center, 
    Left, 
    Right;
    
    companion object {
        val marshaller = FrameworkMarshallers.enum<RdHorizontalAlignment>()
        
    }
}


/**
 * #### Generated from [RdComment.kt:194]
 */
class RdHttpLinkReference (
    val displayName: String,
    rawValue: String
) : RdExternalReference (
    rawValue
) {
    //companion
    
    companion object : IMarshaller<RdHttpLinkReference> {
        override val _type: KClass<RdHttpLinkReference> = RdHttpLinkReference::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdHttpLinkReference  {
            val rawValue = buffer.readString()
            val displayName = buffer.readString()
            return RdHttpLinkReference(displayName, rawValue)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdHttpLinkReference)  {
            buffer.writeString(value.rawValue)
            buffer.writeString(value.displayName)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdHttpLinkReference
        
        if (displayName != other.displayName) return false
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + displayName.hashCode()
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdHttpLinkReference (")
        printer.indent {
            print("displayName = "); displayName.print(printer); println()
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:118]
 */
class RdImageSegment (
    val sourceReference: RdReference,
    val description: RdHighlightedText
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdImageSegment> {
        override val _type: KClass<RdImageSegment> = RdImageSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdImageSegment  {
            val sourceReference = ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference)
            val description = RdHighlightedText.read(ctx, buffer)
            return RdImageSegment(sourceReference, description)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdImageSegment)  {
            ctx.serializers.writePolymorphic(ctx, buffer, value.sourceReference)
            RdHighlightedText.write(ctx, buffer, value.description)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdImageSegment
        
        if (sourceReference != other.sourceReference) return false
        if (description != other.description) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + sourceReference.hashCode()
        __r = __r*31 + description.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdImageSegment (")
        printer.indent {
            print("sourceReference = "); sourceReference.print(printer); println()
            print("description = "); description.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:25]
 */
class RdInlineReferenceComment (
    val referenceContentSegment: RdInlineReferenceContentSegment,
    range: RdTextRange
) : RdComment (
    range
) {
    //companion
    
    companion object : IMarshaller<RdInlineReferenceComment> {
        override val _type: KClass<RdInlineReferenceComment> = RdInlineReferenceComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdInlineReferenceComment  {
            val range = RdTextRange.read(ctx, buffer)
            val referenceContentSegment = RdInlineReferenceContentSegment.read(ctx, buffer)
            return RdInlineReferenceComment(referenceContentSegment, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdInlineReferenceComment)  {
            RdTextRange.write(ctx, buffer, value.range)
            RdInlineReferenceContentSegment.write(ctx, buffer, value.referenceContentSegment)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdInlineReferenceComment
        
        if (referenceContentSegment != other.referenceContentSegment) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + referenceContentSegment.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdInlineReferenceComment (")
        printer.indent {
            print("referenceContentSegment = "); referenceContentSegment.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:220]
 */
class RdInlineReferenceContentSegment (
    val nameText: RdHighlightedText,
    val description: RdHighlightedText? = null
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdInlineReferenceContentSegment> {
        override val _type: KClass<RdInlineReferenceContentSegment> = RdInlineReferenceContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdInlineReferenceContentSegment  {
            val nameText = RdHighlightedText.read(ctx, buffer)
            val description = buffer.readNullable { RdHighlightedText.read(ctx, buffer) }
            return RdInlineReferenceContentSegment(nameText, description)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdInlineReferenceContentSegment)  {
            RdHighlightedText.write(ctx, buffer, value.nameText)
            buffer.writeNullable(value.description) { RdHighlightedText.write(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdInlineReferenceContentSegment
        
        if (nameText != other.nameText) return false
        if (description != other.description) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + nameText.hashCode()
        __r = __r*31 + if (description != null) description.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdInlineReferenceContentSegment (")
        printer.indent {
            print("nameText = "); nameText.print(printer); println()
            print("description = "); description.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:67]
 */
class RdIntelligentCommentContent (
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdIntelligentCommentContent> {
        override val _type: KClass<RdIntelligentCommentContent> = RdIntelligentCommentContent::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdIntelligentCommentContent  {
            val content = RdContentSegments.read(ctx, buffer)
            return RdIntelligentCommentContent(content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdIntelligentCommentContent)  {
            RdContentSegments.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdIntelligentCommentContent
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdIntelligentCommentContent (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:46]
 */
class RdInvalidComment (
    text: RdTextSegment,
    range: RdTextRange
) : RdCommentWithOneTextSegment (
    text,
    range
) {
    //companion
    
    companion object : IMarshaller<RdInvalidComment> {
        override val _type: KClass<RdInvalidComment> = RdInvalidComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdInvalidComment  {
            val text = RdTextSegment.read(ctx, buffer)
            val range = RdTextRange.read(ctx, buffer)
            return RdInvalidComment(text, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdInvalidComment)  {
            RdTextSegment.write(ctx, buffer, value.text)
            RdTextRange.write(ctx, buffer, value.range)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdInvalidComment
        
        if (text != other.text) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + text.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdInvalidComment (")
        printer.indent {
            print("text = "); text.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:347]
 */
class RdInvalidResolveResult (
    val error: RdHighlightedText?
) : RdResolveResult (
) {
    //companion
    
    companion object : IMarshaller<RdInvalidResolveResult> {
        override val _type: KClass<RdInvalidResolveResult> = RdInvalidResolveResult::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdInvalidResolveResult  {
            val error = buffer.readNullable { RdHighlightedText.read(ctx, buffer) }
            return RdInvalidResolveResult(error)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdInvalidResolveResult)  {
            buffer.writeNullable(value.error) { RdHighlightedText.write(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdInvalidResolveResult
        
        if (error != other.error) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + if (error != null) error.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdInvalidResolveResult (")
        printer.indent {
            print("error = "); error.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:171]
 */
abstract class RdInvariant (
) : RdContentSegment (
) {
    //companion
    
    companion object : IAbstractDeclaration<RdInvariant> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdInvariant  {
            val objectStartPosition = buffer.position
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdInvariant_Unknown(unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:374]
 */
class RdInvariantItem (
    name: String,
    presentation: String,
    documentOffset: Int?
) : RdNamedEntityItem (
    name,
    presentation,
    documentOffset
) {
    //companion
    
    companion object : IMarshaller<RdInvariantItem> {
        override val _type: KClass<RdInvariantItem> = RdInvariantItem::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdInvariantItem  {
            val name = buffer.readString()
            val presentation = buffer.readString()
            val documentOffset = buffer.readNullable { buffer.readInt() }
            return RdInvariantItem(name, presentation, documentOffset)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdInvariantItem)  {
            buffer.writeString(value.name)
            buffer.writeString(value.presentation)
            buffer.writeNullable(value.documentOffset) { buffer.writeInt(it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdInvariantItem
        
        if (name != other.name) return false
        if (presentation != other.presentation) return false
        if (documentOffset != other.documentOffset) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + presentation.hashCode()
        __r = __r*31 + if (documentOffset != null) documentOffset.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdInvariantItem (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("presentation = "); presentation.print(printer); println()
            print("documentOffset = "); documentOffset.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


class RdInvariant_Unknown (
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdInvariant (
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdInvariant_Unknown> {
        override val _type: KClass<RdInvariant_Unknown> = RdInvariant_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdInvariant_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdInvariant_Unknown)  {
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdInvariant_Unknown
        
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdInvariant_Unknown (")
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:212]
 */
class RdLangWordReference (
    rawValue: String
) : RdReference (
    rawValue
) {
    //companion
    
    companion object : IMarshaller<RdLangWordReference> {
        override val _type: KClass<RdLangWordReference> = RdLangWordReference::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdLangWordReference  {
            val rawValue = buffer.readString()
            return RdLangWordReference(rawValue)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdLangWordReference)  {
            buffer.writeString(value.rawValue)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdLangWordReference
        
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdLangWordReference (")
        printer.indent {
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:134]
 */
data class RdListItem (
    val header: RdContentSegments? = null,
    val description: RdContentSegments? = null
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdListItem> {
        override val _type: KClass<RdListItem> = RdListItem::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdListItem  {
            val header = buffer.readNullable { RdContentSegments.read(ctx, buffer) }
            val description = buffer.readNullable { RdContentSegments.read(ctx, buffer) }
            return RdListItem(header, description)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdListItem)  {
            buffer.writeNullable(value.header) { RdContentSegments.write(ctx, buffer, it) }
            buffer.writeNullable(value.description) { RdContentSegments.write(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdListItem
        
        if (header != other.header) return false
        if (description != other.description) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + if (header != null) header.hashCode() else 0
        __r = __r*31 + if (description != null) description.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdListItem (")
        printer.indent {
            print("header = "); header.print(printer); println()
            print("description = "); description.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:128]
 */
class RdListSegment (
    val listKind: ListKind,
    val listContent: List<RdListItem>,
    val header: RdHighlightedText? = null
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdListSegment> {
        override val _type: KClass<RdListSegment> = RdListSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdListSegment  {
            val listKind = buffer.readEnum<ListKind>()
            val listContent = buffer.readList { RdListItem.read(ctx, buffer) }
            val header = buffer.readNullable { RdHighlightedText.read(ctx, buffer) }
            return RdListSegment(listKind, listContent, header)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdListSegment)  {
            buffer.writeEnum(value.listKind)
            buffer.writeList(value.listContent) { v -> RdListItem.write(ctx, buffer, v) }
            buffer.writeNullable(value.header) { RdHighlightedText.write(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdListSegment
        
        if (listKind != other.listKind) return false
        if (listContent != other.listContent) return false
        if (header != other.header) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + listKind.hashCode()
        __r = __r*31 + listContent.hashCode()
        __r = __r*31 + if (header != null) header.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdListSegment (")
        printer.indent {
            print("listKind = "); listKind.print(printer); println()
            print("listContent = "); listContent.print(printer); println()
            print("header = "); header.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:56]
 */
class RdMultilineComment (
    text: RdTextSegment,
    range: RdTextRange
) : RdCommentWithOneTextSegment (
    text,
    range
) {
    //companion
    
    companion object : IMarshaller<RdMultilineComment> {
        override val _type: KClass<RdMultilineComment> = RdMultilineComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdMultilineComment  {
            val text = RdTextSegment.read(ctx, buffer)
            val range = RdTextRange.read(ctx, buffer)
            return RdMultilineComment(text, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdMultilineComment)  {
            RdTextSegment.write(ctx, buffer, value.text)
            RdTextRange.write(ctx, buffer, value.range)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdMultilineComment
        
        if (text != other.text) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + text.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdMultilineComment (")
        printer.indent {
            print("text = "); text.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:339]
 */
enum class RdNameKind {
    Invariant, 
    Hack, 
    Todo;
    
    companion object {
        val marshaller = FrameworkMarshallers.enum<RdNameKind>()
        
    }
}


/**
 * #### Generated from [RdComment.kt:366]
 */
abstract class RdNamedEntityItem (
    val name: String,
    val presentation: String,
    val documentOffset: Int?
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdNamedEntityItem> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdNamedEntityItem  {
            val objectStartPosition = buffer.position
            val name = buffer.readString()
            val presentation = buffer.readString()
            val documentOffset = buffer.readNullable { buffer.readInt() }
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdNamedEntityItem_Unknown(name, presentation, documentOffset, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdNamedEntityItem_Unknown (
    name: String,
    presentation: String,
    documentOffset: Int?,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdNamedEntityItem (
    name,
    presentation,
    documentOffset
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdNamedEntityItem_Unknown> {
        override val _type: KClass<RdNamedEntityItem_Unknown> = RdNamedEntityItem_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdNamedEntityItem_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdNamedEntityItem_Unknown)  {
            buffer.writeString(value.name)
            buffer.writeString(value.presentation)
            buffer.writeNullable(value.documentOffset) { buffer.writeInt(it) }
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdNamedEntityItem_Unknown
        
        if (name != other.name) return false
        if (presentation != other.presentation) return false
        if (documentOffset != other.documentOffset) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + presentation.hashCode()
        __r = __r*31 + if (documentOffset != null) documentOffset.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdNamedEntityItem_Unknown (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("presentation = "); presentation.print(printer); println()
            print("documentOffset = "); documentOffset.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:183]
 */
class RdNamedEntityReference (
    val nameKind: RdNameKind,
    val name: String,
    rawValue: String
) : RdReference (
    rawValue
) {
    //companion
    
    companion object : IMarshaller<RdNamedEntityReference> {
        override val _type: KClass<RdNamedEntityReference> = RdNamedEntityReference::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdNamedEntityReference  {
            val rawValue = buffer.readString()
            val nameKind = buffer.readEnum<RdNameKind>()
            val name = buffer.readString()
            return RdNamedEntityReference(nameKind, name, rawValue)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdNamedEntityReference)  {
            buffer.writeString(value.rawValue)
            buffer.writeEnum(value.nameKind)
            buffer.writeString(value.name)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdNamedEntityReference
        
        if (nameKind != other.nameKind) return false
        if (name != other.name) return false
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + nameKind.hashCode()
        __r = __r*31 + name.hashCode()
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdNamedEntityReference (")
        printer.indent {
            print("nameKind = "); nameKind.print(printer); println()
            print("name = "); name.print(printer); println()
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:351]
 */
class RdNamedEntityResolveResult (
    val nameKind: RdNameKind,
    val segment: RdContentSegment?
) : RdResolveResult (
) {
    //companion
    
    companion object : IMarshaller<RdNamedEntityResolveResult> {
        override val _type: KClass<RdNamedEntityResolveResult> = RdNamedEntityResolveResult::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdNamedEntityResolveResult  {
            val nameKind = buffer.readEnum<RdNameKind>()
            val segment = buffer.readNullable { ctx.serializers.readPolymorphic<RdContentSegment>(ctx, buffer, RdContentSegment) }
            return RdNamedEntityResolveResult(nameKind, segment)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdNamedEntityResolveResult)  {
            buffer.writeEnum(value.nameKind)
            buffer.writeNullable(value.segment) { ctx.serializers.writePolymorphic(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdNamedEntityResolveResult
        
        if (nameKind != other.nameKind) return false
        if (segment != other.segment) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + nameKind.hashCode()
        __r = __r*31 + if (segment != null) segment.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdNamedEntityResolveResult (")
        printer.indent {
            print("nameKind = "); nameKind.print(printer); println()
            print("segment = "); segment.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:322]
 */
abstract class RdNavigationRequest (
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdNavigationRequest> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdNavigationRequest  {
            val objectStartPosition = buffer.position
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdNavigationRequest_Unknown(unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdNavigationRequest_Unknown (
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdNavigationRequest (
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdNavigationRequest_Unknown> {
        override val _type: KClass<RdNavigationRequest_Unknown> = RdNavigationRequest_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdNavigationRequest_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdNavigationRequest_Unknown)  {
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdNavigationRequest_Unknown
        
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdNavigationRequest_Unknown (")
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:84]
 */
class RdParagraphSegment (
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdParagraphSegment> {
        override val _type: KClass<RdParagraphSegment> = RdParagraphSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdParagraphSegment  {
            val content = RdContentSegments.read(ctx, buffer)
            return RdParagraphSegment(content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdParagraphSegment)  {
            RdContentSegments.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdParagraphSegment
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdParagraphSegment (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:74]
 */
open class RdParam (
    val name: RdHighlightedText,
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdParam>, IAbstractDeclaration<RdParam> {
        override val _type: KClass<RdParam> = RdParam::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdParam  {
            val content = RdContentSegments.read(ctx, buffer)
            val name = RdHighlightedText.read(ctx, buffer)
            return RdParam(name, content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdParam)  {
            RdContentSegments.write(ctx, buffer, value.content)
            RdHighlightedText.write(ctx, buffer, value.name)
        }
        
        
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdParam  {
            val objectStartPosition = buffer.position
            val name = RdHighlightedText.read(ctx, buffer)
            val content = RdContentSegments.read(ctx, buffer)
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdParam_Unknown(name, content, unknownId, unknownBytes)
        }
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdParam
        
        if (name != other.name) return false
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdParam (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


class RdParam_Unknown (
    name: RdHighlightedText,
    content: RdContentSegments,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdParam (
    name,
    content
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdParam_Unknown> {
        override val _type: KClass<RdParam_Unknown> = RdParam_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdParam_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdParam_Unknown)  {
            RdHighlightedText.write(ctx, buffer, value.name)
            RdContentSegments.write(ctx, buffer, value.content)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdParam_Unknown
        
        if (name != other.name) return false
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdParam_Unknown (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:274]
 */
class RdPredefinedForegroundColorAnimation (
    val key: String
) : RdTextAnimation (
) {
    //companion
    
    companion object : IMarshaller<RdPredefinedForegroundColorAnimation> {
        override val _type: KClass<RdPredefinedForegroundColorAnimation> = RdPredefinedForegroundColorAnimation::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdPredefinedForegroundColorAnimation  {
            val key = buffer.readString()
            return RdPredefinedForegroundColorAnimation(key)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdPredefinedForegroundColorAnimation)  {
            buffer.writeString(value.key)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdPredefinedForegroundColorAnimation
        
        if (key != other.key) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + key.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdPredefinedForegroundColorAnimation (")
        printer.indent {
            print("key = "); key.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:188]
 */
class RdProxyReference (
    val realReferenceId: Int,
    rawValue: String
) : RdReference (
    rawValue
) {
    //companion
    
    companion object : IMarshaller<RdProxyReference> {
        override val _type: KClass<RdProxyReference> = RdProxyReference::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdProxyReference  {
            val rawValue = buffer.readString()
            val realReferenceId = buffer.readInt()
            return RdProxyReference(realReferenceId, rawValue)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdProxyReference)  {
            buffer.writeString(value.rawValue)
            buffer.writeInt(value.realReferenceId)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdProxyReference
        
        if (realReferenceId != other.realReferenceId) return false
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + realReferenceId.hashCode()
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdProxyReference (")
        printer.indent {
            print("realReferenceId = "); realReferenceId.print(printer); println()
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:179]
 */
abstract class RdReference (
    val rawValue: String
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdReference> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdReference  {
            val objectStartPosition = buffer.position
            val rawValue = buffer.readString()
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdReference_Unknown(rawValue, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:214]
 */
class RdReferenceContentSegment (
    val reference: RdReference,
    val name: RdHighlightedText,
    val description: RdDefaultSegmentWithContent
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdReferenceContentSegment> {
        override val _type: KClass<RdReferenceContentSegment> = RdReferenceContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdReferenceContentSegment  {
            val reference = ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference)
            val name = RdHighlightedText.read(ctx, buffer)
            val description = RdDefaultSegmentWithContent.read(ctx, buffer)
            return RdReferenceContentSegment(reference, name, description)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdReferenceContentSegment)  {
            ctx.serializers.writePolymorphic(ctx, buffer, value.reference)
            RdHighlightedText.write(ctx, buffer, value.name)
            RdDefaultSegmentWithContent.write(ctx, buffer, value.description)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdReferenceContentSegment
        
        if (reference != other.reference) return false
        if (name != other.name) return false
        if (description != other.description) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + reference.hashCode()
        __r = __r*31 + name.hashCode()
        __r = __r*31 + description.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdReferenceContentSegment (")
        printer.indent {
            print("reference = "); reference.print(printer); println()
            print("name = "); name.print(printer); println()
            print("description = "); description.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:325]
 */
class RdReferenceNavigationRequest (
    val resolveRequest: RdReferenceResolveRequest
) : RdNavigationRequest (
) {
    //companion
    
    companion object : IMarshaller<RdReferenceNavigationRequest> {
        override val _type: KClass<RdReferenceNavigationRequest> = RdReferenceNavigationRequest::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdReferenceNavigationRequest  {
            val resolveRequest = RdReferenceResolveRequest.read(ctx, buffer)
            return RdReferenceNavigationRequest(resolveRequest)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdReferenceNavigationRequest)  {
            RdReferenceResolveRequest.write(ctx, buffer, value.resolveRequest)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdReferenceNavigationRequest
        
        if (resolveRequest != other.resolveRequest) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + resolveRequest.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdReferenceNavigationRequest (")
        printer.indent {
            print("resolveRequest = "); resolveRequest.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:317]
 */
data class RdReferenceResolveRequest (
    val reference: RdReference,
    val textControlId: TextControlId
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdReferenceResolveRequest> {
        override val _type: KClass<RdReferenceResolveRequest> = RdReferenceResolveRequest::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdReferenceResolveRequest  {
            val reference = ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference)
            val textControlId = TextControlId.read(ctx, buffer)
            return RdReferenceResolveRequest(reference, textControlId)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdReferenceResolveRequest)  {
            ctx.serializers.writePolymorphic(ctx, buffer, value.reference)
            TextControlId.write(ctx, buffer, value.textControlId)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdReferenceResolveRequest
        
        if (reference != other.reference) return false
        if (textControlId != other.textControlId) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + reference.hashCode()
        __r = __r*31 + textControlId.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdReferenceResolveRequest (")
        printer.indent {
            print("reference = "); reference.print(printer); println()
            print("textControlId = "); textControlId.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


class RdReference_Unknown (
    rawValue: String,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdReference (
    rawValue
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdReference_Unknown> {
        override val _type: KClass<RdReference_Unknown> = RdReference_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdReference_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdReference_Unknown)  {
            buffer.writeString(value.rawValue)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdReference_Unknown
        
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdReference_Unknown (")
        printer.indent {
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:82]
 */
class RdRemarksSegment (
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdRemarksSegment> {
        override val _type: KClass<RdRemarksSegment> = RdRemarksSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdRemarksSegment  {
            val content = RdContentSegments.read(ctx, buffer)
            return RdRemarksSegment(content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdRemarksSegment)  {
            RdContentSegments.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdRemarksSegment
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdRemarksSegment (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:345]
 */
abstract class RdResolveResult (
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdResolveResult> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdResolveResult  {
            val objectStartPosition = buffer.position
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdResolveResult_Unknown(unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdResolveResult_Unknown (
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdResolveResult (
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdResolveResult_Unknown> {
        override val _type: KClass<RdResolveResult_Unknown> = RdResolveResult_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdResolveResult_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdResolveResult_Unknown)  {
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdResolveResult_Unknown
        
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdResolveResult_Unknown (")
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:86]
 */
class RdReturnSegment (
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdReturnSegment> {
        override val _type: KClass<RdReturnSegment> = RdReturnSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdReturnSegment  {
            val content = RdContentSegments.read(ctx, buffer)
            return RdReturnSegment(content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdReturnSegment)  {
            RdContentSegments.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdReturnSegment
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdReturnSegment (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:206]
 */
class RdSandboxCodeEntityReference (
    val sandboxFileId: String,
    val originalDocumentId: RdDocumentId?,
    val range: RdTextRange,
    rawValue: String
) : RdCodeEntityReference (
    rawValue
) {
    //companion
    
    companion object : IMarshaller<RdSandboxCodeEntityReference> {
        override val _type: KClass<RdSandboxCodeEntityReference> = RdSandboxCodeEntityReference::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdSandboxCodeEntityReference  {
            val rawValue = buffer.readString()
            val sandboxFileId = buffer.readString()
            val originalDocumentId = buffer.readNullable { ctx.serializers.readPolymorphic<RdDocumentId>(ctx, buffer, RdDocumentId) }
            val range = RdTextRange.read(ctx, buffer)
            return RdSandboxCodeEntityReference(sandboxFileId, originalDocumentId, range, rawValue)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdSandboxCodeEntityReference)  {
            buffer.writeString(value.rawValue)
            buffer.writeString(value.sandboxFileId)
            buffer.writeNullable(value.originalDocumentId) { ctx.serializers.writePolymorphic(ctx, buffer, it) }
            RdTextRange.write(ctx, buffer, value.range)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdSandboxCodeEntityReference
        
        if (sandboxFileId != other.sandboxFileId) return false
        if (originalDocumentId != other.originalDocumentId) return false
        if (range != other.range) return false
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + sandboxFileId.hashCode()
        __r = __r*31 + if (originalDocumentId != null) originalDocumentId.hashCode() else 0
        __r = __r*31 + range.hashCode()
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdSandboxCodeEntityReference (")
        printer.indent {
            print("sandboxFileId = "); sandboxFileId.print(printer); println()
            print("originalDocumentId = "); originalDocumentId.print(printer); println()
            print("range = "); range.print(printer); println()
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:97]
 */
abstract class RdSeeAlsoContentSegment (
    val description: RdHighlightedText
) : RdContentSegment (
) {
    //companion
    
    companion object : IAbstractDeclaration<RdSeeAlsoContentSegment> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdSeeAlsoContentSegment  {
            val objectStartPosition = buffer.position
            val description = RdHighlightedText.read(ctx, buffer)
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdSeeAlsoContentSegment_Unknown(description, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdSeeAlsoContentSegment_Unknown (
    description: RdHighlightedText,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdSeeAlsoContentSegment (
    description
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdSeeAlsoContentSegment_Unknown> {
        override val _type: KClass<RdSeeAlsoContentSegment_Unknown> = RdSeeAlsoContentSegment_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdSeeAlsoContentSegment_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdSeeAlsoContentSegment_Unknown)  {
            RdHighlightedText.write(ctx, buffer, value.description)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdSeeAlsoContentSegment_Unknown
        
        if (description != other.description) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + description.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdSeeAlsoContentSegment_Unknown (")
        printer.indent {
            print("description = "); description.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:105]
 */
class RdSeeAlsoLinkContentSegment (
    val reference: RdReference,
    description: RdHighlightedText
) : RdSeeAlsoContentSegment (
    description
) {
    //companion
    
    companion object : IMarshaller<RdSeeAlsoLinkContentSegment> {
        override val _type: KClass<RdSeeAlsoLinkContentSegment> = RdSeeAlsoLinkContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdSeeAlsoLinkContentSegment  {
            val description = RdHighlightedText.read(ctx, buffer)
            val reference = ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference)
            return RdSeeAlsoLinkContentSegment(reference, description)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdSeeAlsoLinkContentSegment)  {
            RdHighlightedText.write(ctx, buffer, value.description)
            ctx.serializers.writePolymorphic(ctx, buffer, value.reference)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdSeeAlsoLinkContentSegment
        
        if (reference != other.reference) return false
        if (description != other.description) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + reference.hashCode()
        __r = __r*31 + description.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdSeeAlsoLinkContentSegment (")
        printer.indent {
            print("reference = "); reference.print(printer); println()
            print("description = "); description.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:101]
 */
class RdSeeAlsoMemberContentSegment (
    val reference: RdReference,
    description: RdHighlightedText
) : RdSeeAlsoContentSegment (
    description
) {
    //companion
    
    companion object : IMarshaller<RdSeeAlsoMemberContentSegment> {
        override val _type: KClass<RdSeeAlsoMemberContentSegment> = RdSeeAlsoMemberContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdSeeAlsoMemberContentSegment  {
            val description = RdHighlightedText.read(ctx, buffer)
            val reference = ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference)
            return RdSeeAlsoMemberContentSegment(reference, description)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdSeeAlsoMemberContentSegment)  {
            RdHighlightedText.write(ctx, buffer, value.description)
            ctx.serializers.writePolymorphic(ctx, buffer, value.reference)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdSeeAlsoMemberContentSegment
        
        if (reference != other.reference) return false
        if (description != other.description) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + reference.hashCode()
        __r = __r*31 + description.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdSeeAlsoMemberContentSegment (")
        printer.indent {
            print("reference = "); reference.print(printer); println()
            print("description = "); description.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:61]
 */
abstract class RdSegmentWithContent (
    val content: RdContentSegments
) : RdContentSegment (
) {
    //companion
    
    companion object : IAbstractDeclaration<RdSegmentWithContent> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdSegmentWithContent  {
            val objectStartPosition = buffer.position
            val content = RdContentSegments.read(ctx, buffer)
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdSegmentWithContent_Unknown(content, unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdSegmentWithContent_Unknown (
    content: RdContentSegments,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdSegmentWithContent (
    content
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdSegmentWithContent_Unknown> {
        override val _type: KClass<RdSegmentWithContent_Unknown> = RdSegmentWithContent_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdSegmentWithContent_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdSegmentWithContent_Unknown)  {
            RdContentSegments.write(ctx, buffer, value.content)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdSegmentWithContent_Unknown
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdSegmentWithContent_Unknown (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:329]
 */
data class RdSourceFileId (
    val lWord: ULong,
    val hWord: ULong
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdSourceFileId> {
        override val _type: KClass<RdSourceFileId> = RdSourceFileId::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdSourceFileId  {
            val lWord = buffer.readULong()
            val hWord = buffer.readULong()
            return RdSourceFileId(lWord, hWord)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdSourceFileId)  {
            buffer.writeULong(value.lWord)
            buffer.writeULong(value.hWord)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdSourceFileId
        
        if (lWord != other.lWord) return false
        if (hWord != other.hWord) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + lWord.hashCode()
        __r = __r*31 + hWord.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdSourceFileId (")
        printer.indent {
            print("lWord = "); lWord.print(printer); println()
            print("hWord = "); hWord.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:242]
 */
data class RdSquiggles (
    val kind: RdSquigglesKind,
    val colorKey: String
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdSquiggles> {
        override val _type: KClass<RdSquiggles> = RdSquiggles::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdSquiggles  {
            val kind = buffer.readEnum<RdSquigglesKind>()
            val colorKey = buffer.readString()
            return RdSquiggles(kind, colorKey)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdSquiggles)  {
            buffer.writeEnum(value.kind)
            buffer.writeString(value.colorKey)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdSquiggles
        
        if (kind != other.kind) return false
        if (colorKey != other.colorKey) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + kind.hashCode()
        __r = __r*31 + colorKey.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdSquiggles (")
        printer.indent {
            print("kind = "); kind.print(printer); println()
            print("colorKey = "); colorKey.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:247]
 */
enum class RdSquigglesKind {
    Wave, 
    Dotted;
    
    companion object {
        val marshaller = FrameworkMarshallers.enum<RdSquigglesKind>()
        
    }
}


/**
 * #### Generated from [RdComment.kt:80]
 */
class RdSummarySegment (
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdSummarySegment> {
        override val _type: KClass<RdSummarySegment> = RdSummarySegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdSummarySegment  {
            val content = RdContentSegments.read(ctx, buffer)
            return RdSummarySegment(content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdSummarySegment)  {
            RdContentSegments.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdSummarySegment
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdSummarySegment (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:148]
 */
data class RdTableCell (
    val content: RdContentSegments,
    val properties: RdTableCellProperties? = null
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdTableCell> {
        override val _type: KClass<RdTableCell> = RdTableCell::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTableCell  {
            val content = RdContentSegments.read(ctx, buffer)
            val properties = buffer.readNullable { RdTableCellProperties.read(ctx, buffer) }
            return RdTableCell(content, properties)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTableCell)  {
            RdContentSegments.write(ctx, buffer, value.content)
            buffer.writeNullable(value.properties) { RdTableCellProperties.write(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTableCell
        
        if (content != other.content) return false
        if (properties != other.properties) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        __r = __r*31 + if (properties != null) properties.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTableCell (")
        printer.indent {
            print("content = "); content.print(printer); println()
            print("properties = "); properties.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:153]
 */
data class RdTableCellProperties (
    val horizontalAlignment: RdHorizontalAlignment,
    val verticalAlignment: RdVerticalAlignment,
    val isHeader: Boolean
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdTableCellProperties> {
        override val _type: KClass<RdTableCellProperties> = RdTableCellProperties::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTableCellProperties  {
            val horizontalAlignment = buffer.readEnum<RdHorizontalAlignment>()
            val verticalAlignment = buffer.readEnum<RdVerticalAlignment>()
            val isHeader = buffer.readBool()
            return RdTableCellProperties(horizontalAlignment, verticalAlignment, isHeader)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTableCellProperties)  {
            buffer.writeEnum(value.horizontalAlignment)
            buffer.writeEnum(value.verticalAlignment)
            buffer.writeBool(value.isHeader)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTableCellProperties
        
        if (horizontalAlignment != other.horizontalAlignment) return false
        if (verticalAlignment != other.verticalAlignment) return false
        if (isHeader != other.isHeader) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + horizontalAlignment.hashCode()
        __r = __r*31 + verticalAlignment.hashCode()
        __r = __r*31 + isHeader.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTableCellProperties (")
        printer.indent {
            print("horizontalAlignment = "); horizontalAlignment.print(printer); println()
            print("verticalAlignment = "); verticalAlignment.print(printer); println()
            print("isHeader = "); isHeader.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:144]
 */
data class RdTableRow (
    val cells: List<RdTableCell>
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdTableRow> {
        override val _type: KClass<RdTableRow> = RdTableRow::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTableRow  {
            val cells = buffer.readList { RdTableCell.read(ctx, buffer) }
            return RdTableRow(cells)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTableRow)  {
            buffer.writeList(value.cells) { v -> RdTableCell.write(ctx, buffer, v) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTableRow
        
        if (cells != other.cells) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + cells.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTableRow (")
        printer.indent {
            print("cells = "); cells.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:139]
 */
class RdTableSegment (
    val rows: List<RdTableRow>,
    val header: RdHighlightedText? = null
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdTableSegment> {
        override val _type: KClass<RdTableSegment> = RdTableSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTableSegment  {
            val rows = buffer.readList { RdTableRow.read(ctx, buffer) }
            val header = buffer.readNullable { RdHighlightedText.read(ctx, buffer) }
            return RdTableSegment(rows, header)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTableSegment)  {
            buffer.writeList(value.rows) { v -> RdTableRow.write(ctx, buffer, v) }
            buffer.writeNullable(value.header) { RdHighlightedText.write(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTableSegment
        
        if (rows != other.rows) return false
        if (header != other.header) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + rows.hashCode()
        __r = __r*31 + if (header != null) header.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTableSegment (")
        printer.indent {
            print("rows = "); rows.print(printer); println()
            print("header = "); header.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:268]
 */
abstract class RdTextAnimation (
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdTextAnimation> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdTextAnimation  {
            val objectStartPosition = buffer.position
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdTextAnimation_Unknown(unknownId, unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    //deepClone
    //contexts
}


class RdTextAnimation_Unknown (
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdTextAnimation (
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdTextAnimation_Unknown> {
        override val _type: KClass<RdTextAnimation_Unknown> = RdTextAnimation_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTextAnimation_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTextAnimation_Unknown)  {
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTextAnimation_Unknown
        
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTextAnimation_Unknown (")
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:252]
 */
data class RdTextAttributes (
    val fontStyle: RdFontStyle? = null,
    val underline: Boolean? = null,
    val fontWeight: Float? = null
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdTextAttributes> {
        override val _type: KClass<RdTextAttributes> = RdTextAttributes::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTextAttributes  {
            val fontStyle = buffer.readNullable { buffer.readEnum<RdFontStyle>() }
            val underline = buffer.readNullable { buffer.readBool() }
            val fontWeight = buffer.readNullable { buffer.readFloat() }
            return RdTextAttributes(fontStyle, underline, fontWeight)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTextAttributes)  {
            buffer.writeNullable(value.fontStyle) { buffer.writeEnum(it) }
            buffer.writeNullable(value.underline) { buffer.writeBool(it) }
            buffer.writeNullable(value.fontWeight) { buffer.writeFloat(it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTextAttributes
        
        if (fontStyle != other.fontStyle) return false
        if (underline != other.underline) return false
        if (fontWeight != other.fontWeight) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + if (fontStyle != null) fontStyle.hashCode() else 0
        __r = __r*31 + if (underline != null) underline.hashCode() else 0
        __r = __r*31 + if (fontWeight != null) fontWeight.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTextAttributes (")
        printer.indent {
            print("fontStyle = "); fontStyle.print(printer); println()
            print("underline = "); underline.print(printer); println()
            print("fontWeight = "); fontWeight.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:230]
 */
data class RdTextHighlighter (
    val key: String,
    val startOffset: Int,
    val endOffset: Int,
    val attributes: RdTextAttributes,
    val backgroundStyle: RdBackgroundStyle? = null,
    val animation: RdTextAnimation? = null,
    val references: List<RdReference>? = null,
    val isResharperHighlighter: Boolean? = null,
    val errorSquiggles: RdSquiggles? = null
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdTextHighlighter> {
        override val _type: KClass<RdTextHighlighter> = RdTextHighlighter::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTextHighlighter  {
            val key = buffer.readString()
            val startOffset = buffer.readInt()
            val endOffset = buffer.readInt()
            val attributes = RdTextAttributes.read(ctx, buffer)
            val backgroundStyle = buffer.readNullable { RdBackgroundStyle.read(ctx, buffer) }
            val animation = buffer.readNullable { ctx.serializers.readPolymorphic<RdTextAnimation>(ctx, buffer, RdTextAnimation) }
            val references = buffer.readNullable { buffer.readList { ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference) } }
            val isResharperHighlighter = buffer.readNullable { buffer.readBool() }
            val errorSquiggles = buffer.readNullable { RdSquiggles.read(ctx, buffer) }
            return RdTextHighlighter(key, startOffset, endOffset, attributes, backgroundStyle, animation, references, isResharperHighlighter, errorSquiggles)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTextHighlighter)  {
            buffer.writeString(value.key)
            buffer.writeInt(value.startOffset)
            buffer.writeInt(value.endOffset)
            RdTextAttributes.write(ctx, buffer, value.attributes)
            buffer.writeNullable(value.backgroundStyle) { RdBackgroundStyle.write(ctx, buffer, it) }
            buffer.writeNullable(value.animation) { ctx.serializers.writePolymorphic(ctx, buffer, it) }
            buffer.writeNullable(value.references) { buffer.writeList(it) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) } }
            buffer.writeNullable(value.isResharperHighlighter) { buffer.writeBool(it) }
            buffer.writeNullable(value.errorSquiggles) { RdSquiggles.write(ctx, buffer, it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTextHighlighter
        
        if (key != other.key) return false
        if (startOffset != other.startOffset) return false
        if (endOffset != other.endOffset) return false
        if (attributes != other.attributes) return false
        if (backgroundStyle != other.backgroundStyle) return false
        if (animation != other.animation) return false
        if (references != other.references) return false
        if (isResharperHighlighter != other.isResharperHighlighter) return false
        if (errorSquiggles != other.errorSquiggles) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + key.hashCode()
        __r = __r*31 + startOffset.hashCode()
        __r = __r*31 + endOffset.hashCode()
        __r = __r*31 + attributes.hashCode()
        __r = __r*31 + if (backgroundStyle != null) backgroundStyle.hashCode() else 0
        __r = __r*31 + if (animation != null) animation.hashCode() else 0
        __r = __r*31 + if (references != null) references.hashCode() else 0
        __r = __r*31 + if (isResharperHighlighter != null) isResharperHighlighter.hashCode() else 0
        __r = __r*31 + if (errorSquiggles != null) errorSquiggles.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTextHighlighter (")
        printer.indent {
            print("key = "); key.print(printer); println()
            print("startOffset = "); startOffset.print(printer); println()
            print("endOffset = "); endOffset.print(printer); println()
            print("attributes = "); attributes.print(printer); println()
            print("backgroundStyle = "); backgroundStyle.print(printer); println()
            print("animation = "); animation.print(printer); println()
            print("references = "); references.print(printer); println()
            print("isResharperHighlighter = "); isResharperHighlighter.print(printer); println()
            print("errorSquiggles = "); errorSquiggles.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:173]
 */
class RdTextInvariant (
    val name: RdHighlightedText,
    val description: RdDefaultSegmentWithContent
) : RdInvariant (
) {
    //companion
    
    companion object : IMarshaller<RdTextInvariant> {
        override val _type: KClass<RdTextInvariant> = RdTextInvariant::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTextInvariant  {
            val name = RdHighlightedText.read(ctx, buffer)
            val description = RdDefaultSegmentWithContent.read(ctx, buffer)
            return RdTextInvariant(name, description)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTextInvariant)  {
            RdHighlightedText.write(ctx, buffer, value.name)
            RdDefaultSegmentWithContent.write(ctx, buffer, value.description)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTextInvariant
        
        if (name != other.name) return false
        if (description != other.description) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + description.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTextInvariant (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("description = "); description.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:114]
 */
class RdTextSegment (
    val text: RdHighlightedText
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdTextSegment> {
        override val _type: KClass<RdTextSegment> = RdTextSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTextSegment  {
            val text = RdHighlightedText.read(ctx, buffer)
            return RdTextSegment(text)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTextSegment)  {
            RdHighlightedText.write(ctx, buffer, value.text)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTextSegment
        
        if (text != other.text) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + text.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTextSegment (")
        printer.indent {
            print("text = "); text.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:296]
 */
class RdTicketContentSegment (
    val source: RdReference,
    val content: RdDefaultSegmentWithContent
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdTicketContentSegment> {
        override val _type: KClass<RdTicketContentSegment> = RdTicketContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTicketContentSegment  {
            val source = ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference)
            val content = RdDefaultSegmentWithContent.read(ctx, buffer)
            return RdTicketContentSegment(source, content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTicketContentSegment)  {
            ctx.serializers.writePolymorphic(ctx, buffer, value.source)
            RdDefaultSegmentWithContent.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTicketContentSegment
        
        if (source != other.source) return false
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + source.hashCode()
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTicketContentSegment (")
        printer.indent {
            print("source = "); source.print(printer); println()
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:40]
 */
class RdToDoComment (
    content: RdDefaultSegmentWithContent,
    range: RdTextRange
) : RdCommentWithOneContentSegments (
    content,
    range
) {
    //companion
    
    companion object : IMarshaller<RdToDoComment> {
        override val _type: KClass<RdToDoComment> = RdToDoComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdToDoComment  {
            val content = RdDefaultSegmentWithContent.read(ctx, buffer)
            val range = RdTextRange.read(ctx, buffer)
            return RdToDoComment(content, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdToDoComment)  {
            RdDefaultSegmentWithContent.write(ctx, buffer, value.content)
            RdTextRange.write(ctx, buffer, value.range)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdToDoComment
        
        if (content != other.content) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdToDoComment (")
        printer.indent {
            print("content = "); content.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:292]
 */
class RdToDoContentSegment (
    val content: RdDefaultSegmentWithContent,
    name: RdHighlightedText? = null
) : RdContentSegmentWithOptionalName (
    name
) {
    //companion
    
    companion object : IMarshaller<RdToDoContentSegment> {
        override val _type: KClass<RdToDoContentSegment> = RdToDoContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdToDoContentSegment  {
            val name = buffer.readNullable { RdHighlightedText.read(ctx, buffer) }
            val content = RdDefaultSegmentWithContent.read(ctx, buffer)
            return RdToDoContentSegment(content, name)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdToDoContentSegment)  {
            buffer.writeNullable(value.name) { RdHighlightedText.write(ctx, buffer, it) }
            RdDefaultSegmentWithContent.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdToDoContentSegment
        
        if (content != other.content) return false
        if (name != other.name) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        __r = __r*31 + if (name != null) name.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdToDoContentSegment (")
        printer.indent {
            print("content = "); content.print(printer); println()
            print("name = "); name.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:288]
 */
class RdToDoTextContentSegment (
    val text: RdHighlightedText
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdToDoTextContentSegment> {
        override val _type: KClass<RdToDoTextContentSegment> = RdToDoTextContentSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdToDoTextContentSegment  {
            val text = RdHighlightedText.read(ctx, buffer)
            return RdToDoTextContentSegment(text)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdToDoTextContentSegment)  {
            RdHighlightedText.write(ctx, buffer, value.text)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdToDoTextContentSegment
        
        if (text != other.text) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + text.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdToDoTextContentSegment (")
        printer.indent {
            print("text = "); text.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:373]
 */
class RdTodoItem (
    name: String,
    presentation: String,
    documentOffset: Int?
) : RdNamedEntityItem (
    name,
    presentation,
    documentOffset
) {
    //companion
    
    companion object : IMarshaller<RdTodoItem> {
        override val _type: KClass<RdTodoItem> = RdTodoItem::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTodoItem  {
            val name = buffer.readString()
            val presentation = buffer.readString()
            val documentOffset = buffer.readNullable { buffer.readInt() }
            return RdTodoItem(name, presentation, documentOffset)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTodoItem)  {
            buffer.writeString(value.name)
            buffer.writeString(value.presentation)
            buffer.writeNullable(value.documentOffset) { buffer.writeInt(it) }
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTodoItem
        
        if (name != other.name) return false
        if (presentation != other.presentation) return false
        if (documentOffset != other.documentOffset) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + presentation.hashCode()
        __r = __r*31 + if (documentOffset != null) documentOffset.hashCode() else 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTodoItem (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("presentation = "); presentation.print(printer); println()
            print("documentOffset = "); documentOffset.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:78]
 */
class RdTypeParam (
    name: RdHighlightedText,
    content: RdContentSegments
) : RdParam (
    name,
    content
) {
    //companion
    
    companion object : IMarshaller<RdTypeParam> {
        override val _type: KClass<RdTypeParam> = RdTypeParam::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTypeParam  {
            val name = RdHighlightedText.read(ctx, buffer)
            val content = RdContentSegments.read(ctx, buffer)
            return RdTypeParam(name, content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTypeParam)  {
            RdHighlightedText.write(ctx, buffer, value.name)
            RdContentSegments.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdTypeParam
        
        if (name != other.name) return false
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTypeParam (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:269]
 */
class RdUnderlineTextAnimation (
) : RdTextAnimation (
) {
    //companion
    
    companion object : IMarshaller<RdUnderlineTextAnimation> {
        override val _type: KClass<RdUnderlineTextAnimation> = RdUnderlineTextAnimation::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdUnderlineTextAnimation  {
            return RdUnderlineTextAnimation()
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdUnderlineTextAnimation)  {
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdUnderlineTextAnimation
        
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdUnderlineTextAnimation (")
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:90]
 */
class RdValueSegment (
    content: RdContentSegments
) : RdSegmentWithContent (
    content
) {
    //companion
    
    companion object : IMarshaller<RdValueSegment> {
        override val _type: KClass<RdValueSegment> = RdValueSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdValueSegment  {
            val content = RdContentSegments.read(ctx, buffer)
            return RdValueSegment(content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdValueSegment)  {
            RdContentSegments.write(ctx, buffer, value.content)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdValueSegment
        
        if (content != other.content) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + content.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdValueSegment (")
        printer.indent {
            print("content = "); content.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:165]
 */
enum class RdVerticalAlignment {
    Center, 
    Top, 
    Bottom;
    
    companion object {
        val marshaller = FrameworkMarshallers.enum<RdVerticalAlignment>()
        
    }
}


/**
 * #### Generated from [RdComment.kt:356]
 */
class RdWebResourceResolveResult (
    val link: String
) : RdResolveResult (
) {
    //companion
    
    companion object : IMarshaller<RdWebResourceResolveResult> {
        override val _type: KClass<RdWebResourceResolveResult> = RdWebResourceResolveResult::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdWebResourceResolveResult  {
            val link = buffer.readString()
            return RdWebResourceResolveResult(link)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdWebResourceResolveResult)  {
            buffer.writeString(value.link)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdWebResourceResolveResult
        
        if (link != other.link) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + link.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdWebResourceResolveResult (")
        printer.indent {
            print("link = "); link.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:204]
 */
class RdXmlDocCodeEntityReference (
    rawValue: String
) : RdCodeEntityReference (
    rawValue
) {
    //companion
    
    companion object : IMarshaller<RdXmlDocCodeEntityReference> {
        override val _type: KClass<RdXmlDocCodeEntityReference> = RdXmlDocCodeEntityReference::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdXmlDocCodeEntityReference  {
            val rawValue = buffer.readString()
            return RdXmlDocCodeEntityReference(rawValue)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdXmlDocCodeEntityReference)  {
            buffer.writeString(value.rawValue)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    override fun equals(other: Any?): Boolean  {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        
        other as RdXmlDocCodeEntityReference
        
        if (rawValue != other.rawValue) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + rawValue.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdXmlDocCodeEntityReference (")
        printer.indent {
            print("rawValue = "); rawValue.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}
