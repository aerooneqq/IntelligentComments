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
 * #### Generated from [RdComment.kt:8]
 */
class RdCommentsModel private constructor(
    private val _evaluate: RdCall<Int, Boolean>
) : RdExtBase() {
    //companion
    
    companion object : ISerializersOwner {
        
        override fun registerSerializersCore(serializers: ISerializers)  {
            serializers.register(RdDocCommentFoldingModel)
            serializers.register(RdDocumentComments)
            serializers.register(RdDocComment)
            serializers.register(RdIntelligentComment)
            serializers.register(RdIntelligentCommentAuthor)
            serializers.register(RdIntelligentCommentContent)
            serializers.register(RdContentSegments)
            serializers.register(RdParam)
            serializers.register(RdTypeParam)
            serializers.register(RdRemarksSegment)
            serializers.register(RdParagraphSegment)
            serializers.register(RdReturnSegment)
            serializers.register(RdExampleSegment)
            serializers.register(RdSeeAlsoMemberContentSegment)
            serializers.register(RdSeeAlsoLinkContentSegment)
            serializers.register(RdExceptionsSegment)
            serializers.register(RdTextSegment)
            serializers.register(RdFileBasedImageSegment)
            serializers.register(RdListSegment)
            serializers.register(RdTableSegment)
            serializers.register(RdTableRow)
            serializers.register(RdTableCell)
            serializers.register(RdTableCellProperties)
            serializers.register(RdHorizontalAlignment.marshaller)
            serializers.register(RdVerticalAlignment.marshaller)
            serializers.register(RdTextInvariant)
            serializers.register(RdHttpLinkReference)
            serializers.register(RdCodeEntityReference)
            serializers.register(RdLangWordReference)
            serializers.register(RdHighlightedText)
            serializers.register(RdTextHighlighter)
            serializers.register(RdTextAttributes)
            serializers.register(RdBackgroundStyle)
            serializers.register(RdColor)
            serializers.register(RdUnderlineTextAnimation)
            serializers.register(RdForegroundColorAnimation)
            serializers.register(RdPredefinedForegroundColorAnimation)
            serializers.register(RdFontStyle.marshaller)
            serializers.register(RdToDoWithTickets)
            serializers.register(RdTicket)
            serializers.register(RdHackWithTickets)
            serializers.register(RdComment_Unknown)
            serializers.register(RdContentSegment_Unknown)
            serializers.register(RdSegmentWithContent_Unknown)
            serializers.register(RdParam_Unknown)
            serializers.register(RdSeeAlsoContentSegment_Unknown)
            serializers.register(RdImageSegment_Unknown)
            serializers.register(RdInvariant_Unknown)
            serializers.register(RdReference_Unknown)
            serializers.register(RdExternalReference_Unknown)
            serializers.register(RdTextAnimation_Unknown)
            serializers.register(RdToDo_Unknown)
            serializers.register(RdHack_Unknown)
        }
        
        
        
        
        const val serializationHash = -9150837645930998285L
        
    }
    override val serializersOwner: ISerializersOwner get() = RdCommentsModel
    override val serializationHash: Long get() = RdCommentsModel.serializationHash
    
    //fields
    val evaluate: IRdCall<Int, Boolean> get() = _evaluate
    //methods
    //initializer
    init {
        bindableChildren.add("evaluate" to _evaluate)
    }
    
    //secondary constructor
    internal constructor(
    ) : this(
        RdCall<Int, Boolean>(FrameworkMarshallers.Int, FrameworkMarshallers.Bool)
    )
    
    //equals trait
    //hash code trait
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCommentsModel (")
        printer.indent {
            print("evaluate = "); _evaluate.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    override fun deepClone(): RdCommentsModel   {
        return RdCommentsModel(
            _evaluate.deepClonePolymorphic()
        )
    }
    //contexts
}
val RdDocumentModel.rdCommentsModel get() = getOrCreateExtension("rdCommentsModel", ::RdCommentsModel)



/**
 * #### Generated from [RdComment.kt:176]
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
 * #### Generated from [RdComment.kt:151]
 */
class RdCodeEntityReference (
    rawValue: String
) : RdReference (
    rawValue
) {
    //companion
    
    companion object : IMarshaller<RdCodeEntityReference> {
        override val _type: KClass<RdCodeEntityReference> = RdCodeEntityReference::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdCodeEntityReference  {
            val rawValue = buffer.readString()
            return RdCodeEntityReference(rawValue)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdCodeEntityReference)  {
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
        
        other as RdCodeEntityReference
        
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
        printer.println("RdCodeEntityReference (")
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
 * #### Generated from [RdComment.kt:182]
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
 * #### Generated from [RdComment.kt:18]
 */
abstract class RdComment (
    val commentIdentifier: Int,
    val range: RdTextRange
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdComment> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdComment  {
            val objectStartPosition = buffer.position
            val commentIdentifier = buffer.readInt()
            val range = RdTextRange.read(ctx, buffer)
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdComment_Unknown(commentIdentifier, range, unknownId, unknownBytes)
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


class RdComment_Unknown (
    commentIdentifier: Int,
    range: RdTextRange,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdComment (
    commentIdentifier,
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
            buffer.writeInt(value.commentIdentifier)
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
        
        if (commentIdentifier != other.commentIdentifier) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + commentIdentifier.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdComment_Unknown (")
        printer.indent {
            print("commentIdentifier = "); commentIdentifier.print(printer); println()
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
 * #### Generated from [RdComment.kt:52]
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
 * #### Generated from [RdComment.kt:23]
 */
class RdDocComment (
    val content: RdIntelligentCommentContent? = null,
    commentIdentifier: Int,
    range: RdTextRange
) : RdComment (
    commentIdentifier,
    range
) {
    //companion
    
    companion object : IMarshaller<RdDocComment> {
        override val _type: KClass<RdDocComment> = RdDocComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdDocComment  {
            val commentIdentifier = buffer.readInt()
            val range = RdTextRange.read(ctx, buffer)
            val content = buffer.readNullable { RdIntelligentCommentContent.read(ctx, buffer) }
            return RdDocComment(content, commentIdentifier, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdDocComment)  {
            buffer.writeInt(value.commentIdentifier)
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
        if (commentIdentifier != other.commentIdentifier) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + if (content != null) content.hashCode() else 0
        __r = __r*31 + commentIdentifier.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdDocComment (")
        printer.indent {
            print("content = "); content.print(printer); println()
            print("commentIdentifier = "); commentIdentifier.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:9]
 */
class RdDocCommentFoldingModel (
    val commentIdentifier: Int,
    val docComment: RdDocComment,
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
    
    companion object : IMarshaller<RdDocCommentFoldingModel> {
        override val _type: KClass<RdDocCommentFoldingModel> = RdDocCommentFoldingModel::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdDocCommentFoldingModel  {
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
            val docComment = RdDocComment.read(ctx, buffer)
            return RdDocCommentFoldingModel(commentIdentifier, docComment, layer, isExactRange, documentVersion, isGreedyToLeft, isGreedyToRight, isThinErrorStripeMark, textToHighlight, textAttributesKey, id, properties, start, end)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdDocCommentFoldingModel)  {
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
            RdDocComment.write(ctx, buffer, value.docComment)
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
        
        other as RdDocCommentFoldingModel
        
        if (commentIdentifier != other.commentIdentifier) return false
        if (docComment != other.docComment) return false
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
        __r = __r*31 + docComment.hashCode()
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
        printer.println("RdDocCommentFoldingModel (")
        printer.indent {
            print("commentIdentifier = "); commentIdentifier.print(printer); println()
            print("docComment = "); docComment.print(printer); println()
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
 * #### Generated from [RdComment.kt:14]
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
 * #### Generated from [RdComment.kt:68]
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
 * #### Generated from [RdComment.kt:82]
 */
class RdExceptionsSegment (
    val name: String,
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
            val name = buffer.readString()
            val exceptionReference = buffer.readNullable { ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference) }
            return RdExceptionsSegment(name, exceptionReference, content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdExceptionsSegment)  {
            RdContentSegments.write(ctx, buffer, value.content)
            buffer.writeString(value.name)
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
 * #### Generated from [RdComment.kt:147]
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
 * #### Generated from [RdComment.kt:95]
 */
class RdFileBasedImageSegment (
    val path: String,
    description: RdHighlightedText
) : RdImageSegment (
    description
) {
    //companion
    
    companion object : IMarshaller<RdFileBasedImageSegment> {
        override val _type: KClass<RdFileBasedImageSegment> = RdFileBasedImageSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdFileBasedImageSegment  {
            val description = RdHighlightedText.read(ctx, buffer)
            val path = buffer.readString()
            return RdFileBasedImageSegment(path, description)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdFileBasedImageSegment)  {
            RdHighlightedText.write(ctx, buffer, value.description)
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
        
        other as RdFileBasedImageSegment
        
        if (path != other.path) return false
        if (description != other.description) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + path.hashCode()
        __r = __r*31 + description.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdFileBasedImageSegment (")
        printer.indent {
            print("path = "); path.print(printer); println()
            print("description = "); description.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:196]
 */
enum class RdFontStyle {
    Regular, 
    Bold;
    
    companion object {
        val marshaller = FrameworkMarshallers.enum<RdFontStyle>()
        
    }
}


/**
 * #### Generated from [RdComment.kt:188]
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
 * #### Generated from [RdComment.kt:217]
 */
abstract class RdHack (
    val name: String,
    val description: RdContentSegments,
    val blockingReferences: List<RdReference>
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdHack> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdHack  {
            val objectStartPosition = buffer.position
            val name = buffer.readString()
            val description = RdContentSegments.read(ctx, buffer)
            val blockingReferences = buffer.readList { ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference) }
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdHack_Unknown(name, description, blockingReferences, unknownId, unknownBytes)
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
 * #### Generated from [RdComment.kt:223]
 */
class RdHackWithTickets (
    val tickets: List<RdTicket>,
    name: String,
    description: RdContentSegments,
    blockingReferences: List<RdReference>
) : RdHack (
    name,
    description,
    blockingReferences
) {
    //companion
    
    companion object : IMarshaller<RdHackWithTickets> {
        override val _type: KClass<RdHackWithTickets> = RdHackWithTickets::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdHackWithTickets  {
            val name = buffer.readString()
            val description = RdContentSegments.read(ctx, buffer)
            val blockingReferences = buffer.readList { ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference) }
            val tickets = buffer.readList { RdTicket.read(ctx, buffer) }
            return RdHackWithTickets(tickets, name, description, blockingReferences)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdHackWithTickets)  {
            buffer.writeString(value.name)
            RdContentSegments.write(ctx, buffer, value.description)
            buffer.writeList(value.blockingReferences) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) }
            buffer.writeList(value.tickets) { v -> RdTicket.write(ctx, buffer, v) }
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
        
        other as RdHackWithTickets
        
        if (tickets != other.tickets) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (blockingReferences != other.blockingReferences) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + tickets.hashCode()
        __r = __r*31 + name.hashCode()
        __r = __r*31 + description.hashCode()
        __r = __r*31 + blockingReferences.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdHackWithTickets (")
        printer.indent {
            print("tickets = "); tickets.print(printer); println()
            print("name = "); name.print(printer); println()
            print("description = "); description.print(printer); println()
            print("blockingReferences = "); blockingReferences.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


class RdHack_Unknown (
    name: String,
    description: RdContentSegments,
    blockingReferences: List<RdReference>,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdHack (
    name,
    description,
    blockingReferences
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdHack_Unknown> {
        override val _type: KClass<RdHack_Unknown> = RdHack_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdHack_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdHack_Unknown)  {
            buffer.writeString(value.name)
            RdContentSegments.write(ctx, buffer, value.description)
            buffer.writeList(value.blockingReferences) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) }
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
        
        other as RdHack_Unknown
        
        if (name != other.name) return false
        if (description != other.description) return false
        if (blockingReferences != other.blockingReferences) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + description.hashCode()
        __r = __r*31 + blockingReferences.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdHack_Unknown (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("description = "); description.print(printer); println()
            print("blockingReferences = "); blockingReferences.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:155]
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
 * #### Generated from [RdComment.kt:124]
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
 * #### Generated from [RdComment.kt:149]
 */
class RdHttpLinkReference (
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
            return RdHttpLinkReference(rawValue)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdHttpLinkReference)  {
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
        
        other as RdHttpLinkReference
        
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
        printer.println("RdHttpLinkReference (")
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
 * #### Generated from [RdComment.kt:91]
 */
abstract class RdImageSegment (
    val description: RdHighlightedText
) : RdContentSegment (
) {
    //companion
    
    companion object : IAbstractDeclaration<RdImageSegment> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdImageSegment  {
            val objectStartPosition = buffer.position
            val description = RdHighlightedText.read(ctx, buffer)
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdImageSegment_Unknown(description, unknownId, unknownBytes)
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


class RdImageSegment_Unknown (
    description: RdHighlightedText,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdImageSegment (
    description
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdImageSegment_Unknown> {
        override val _type: KClass<RdImageSegment_Unknown> = RdImageSegment_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdImageSegment_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdImageSegment_Unknown)  {
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
        
        other as RdImageSegment_Unknown
        
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
        printer.println("RdImageSegment_Unknown (")
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
 * #### Generated from [RdComment.kt:27]
 */
class RdIntelligentComment (
    val authors: List<RdIntelligentCommentAuthor>? = null,
    val date: Date,
    val content: RdIntelligentCommentContent? = null,
    val invariants: List<RdInvariant>? = null,
    val references: List<RdReference>? = null,
    val toDos: List<RdToDo>? = null,
    val hacks: List<RdHack>? = null,
    commentIdentifier: Int,
    range: RdTextRange
) : RdComment (
    commentIdentifier,
    range
) {
    //companion
    
    companion object : IMarshaller<RdIntelligentComment> {
        override val _type: KClass<RdIntelligentComment> = RdIntelligentComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdIntelligentComment  {
            val commentIdentifier = buffer.readInt()
            val range = RdTextRange.read(ctx, buffer)
            val authors = buffer.readNullable { buffer.readList { RdIntelligentCommentAuthor.read(ctx, buffer) } }
            val date = buffer.readDateTime()
            val content = buffer.readNullable { RdIntelligentCommentContent.read(ctx, buffer) }
            val invariants = buffer.readNullable { buffer.readList { ctx.serializers.readPolymorphic<RdInvariant>(ctx, buffer, RdInvariant) } }
            val references = buffer.readNullable { buffer.readList { ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference) } }
            val toDos = buffer.readNullable { buffer.readList { ctx.serializers.readPolymorphic<RdToDo>(ctx, buffer, RdToDo) } }
            val hacks = buffer.readNullable { buffer.readList { ctx.serializers.readPolymorphic<RdHack>(ctx, buffer, RdHack) } }
            return RdIntelligentComment(authors, date, content, invariants, references, toDos, hacks, commentIdentifier, range)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdIntelligentComment)  {
            buffer.writeInt(value.commentIdentifier)
            RdTextRange.write(ctx, buffer, value.range)
            buffer.writeNullable(value.authors) { buffer.writeList(it) { v -> RdIntelligentCommentAuthor.write(ctx, buffer, v) } }
            buffer.writeDateTime(value.date)
            buffer.writeNullable(value.content) { RdIntelligentCommentContent.write(ctx, buffer, it) }
            buffer.writeNullable(value.invariants) { buffer.writeList(it) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) } }
            buffer.writeNullable(value.references) { buffer.writeList(it) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) } }
            buffer.writeNullable(value.toDos) { buffer.writeList(it) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) } }
            buffer.writeNullable(value.hacks) { buffer.writeList(it) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) } }
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
        
        other as RdIntelligentComment
        
        if (authors != other.authors) return false
        if (date != other.date) return false
        if (content != other.content) return false
        if (invariants != other.invariants) return false
        if (references != other.references) return false
        if (toDos != other.toDos) return false
        if (hacks != other.hacks) return false
        if (commentIdentifier != other.commentIdentifier) return false
        if (range != other.range) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + if (authors != null) authors.hashCode() else 0
        __r = __r*31 + date.hashCode()
        __r = __r*31 + if (content != null) content.hashCode() else 0
        __r = __r*31 + if (invariants != null) invariants.hashCode() else 0
        __r = __r*31 + if (references != null) references.hashCode() else 0
        __r = __r*31 + if (toDos != null) toDos.hashCode() else 0
        __r = __r*31 + if (hacks != null) hacks.hashCode() else 0
        __r = __r*31 + commentIdentifier.hashCode()
        __r = __r*31 + range.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdIntelligentComment (")
        printer.indent {
            print("authors = "); authors.print(printer); println()
            print("date = "); date.print(printer); println()
            print("content = "); content.print(printer); println()
            print("invariants = "); invariants.print(printer); println()
            print("references = "); references.print(printer); println()
            print("toDos = "); toDos.print(printer); println()
            print("hacks = "); hacks.print(printer); println()
            print("commentIdentifier = "); commentIdentifier.print(printer); println()
            print("range = "); range.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:38]
 */
data class RdIntelligentCommentAuthor (
    val name: String,
    val date: Date
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdIntelligentCommentAuthor> {
        override val _type: KClass<RdIntelligentCommentAuthor> = RdIntelligentCommentAuthor::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdIntelligentCommentAuthor  {
            val name = buffer.readString()
            val date = buffer.readDateTime()
            return RdIntelligentCommentAuthor(name, date)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdIntelligentCommentAuthor)  {
            buffer.writeString(value.name)
            buffer.writeDateTime(value.date)
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
        
        other as RdIntelligentCommentAuthor
        
        if (name != other.name) return false
        if (date != other.date) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + name.hashCode()
        __r = __r*31 + date.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdIntelligentCommentAuthor (")
        printer.indent {
            print("name = "); name.print(printer); println()
            print("date = "); date.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:49]
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
 * #### Generated from [RdComment.kt:136]
 */
abstract class RdInvariant (
) : IPrintable {
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
 * #### Generated from [RdComment.kt:153]
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
 * #### Generated from [RdComment.kt:99]
 */
class RdListSegment (
    val listContent: List<RdContentSegments>,
    val header: RdHighlightedText
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdListSegment> {
        override val _type: KClass<RdListSegment> = RdListSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdListSegment  {
            val listContent = buffer.readList { RdContentSegments.read(ctx, buffer) }
            val header = RdHighlightedText.read(ctx, buffer)
            return RdListSegment(listContent, header)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdListSegment)  {
            buffer.writeList(value.listContent) { v -> RdContentSegments.write(ctx, buffer, v) }
            RdHighlightedText.write(ctx, buffer, value.header)
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
        
        if (listContent != other.listContent) return false
        if (header != other.header) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + listContent.hashCode()
        __r = __r*31 + header.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdListSegment (")
        printer.indent {
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
 * #### Generated from [RdComment.kt:64]
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
 * #### Generated from [RdComment.kt:56]
 */
open class RdParam (
    val name: String,
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
            val name = buffer.readString()
            return RdParam(name, content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdParam)  {
            RdContentSegments.write(ctx, buffer, value.content)
            buffer.writeString(value.name)
        }
        
        
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdParam  {
            val objectStartPosition = buffer.position
            val name = buffer.readString()
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
    name: String,
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
            buffer.writeString(value.name)
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
 * #### Generated from [RdComment.kt:192]
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
 * #### Generated from [RdComment.kt:143]
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
 * #### Generated from [RdComment.kt:62]
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
 * #### Generated from [RdComment.kt:66]
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
 * #### Generated from [RdComment.kt:70]
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
 * #### Generated from [RdComment.kt:78]
 */
class RdSeeAlsoLinkContentSegment (
    val reference: RdExternalReference,
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
            val reference = ctx.serializers.readPolymorphic<RdExternalReference>(ctx, buffer, RdExternalReference)
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
 * #### Generated from [RdComment.kt:74]
 */
class RdSeeAlsoMemberContentSegment (
    val reference: RdCodeEntityReference,
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
            val reference = RdCodeEntityReference.read(ctx, buffer)
            return RdSeeAlsoMemberContentSegment(reference, description)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdSeeAlsoMemberContentSegment)  {
            RdHighlightedText.write(ctx, buffer, value.description)
            RdCodeEntityReference.write(ctx, buffer, value.reference)
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
 * #### Generated from [RdComment.kt:45]
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
 * #### Generated from [RdComment.kt:113]
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
 * #### Generated from [RdComment.kt:118]
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
 * #### Generated from [RdComment.kt:109]
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
 * #### Generated from [RdComment.kt:104]
 */
class RdTableSegment (
    val header: RdHighlightedText,
    val rows: List<RdTableRow>
) : RdContentSegment (
) {
    //companion
    
    companion object : IMarshaller<RdTableSegment> {
        override val _type: KClass<RdTableSegment> = RdTableSegment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTableSegment  {
            val header = RdHighlightedText.read(ctx, buffer)
            val rows = buffer.readList { RdTableRow.read(ctx, buffer) }
            return RdTableSegment(header, rows)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTableSegment)  {
            RdHighlightedText.write(ctx, buffer, value.header)
            buffer.writeList(value.rows) { v -> RdTableRow.write(ctx, buffer, v) }
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
        
        if (header != other.header) return false
        if (rows != other.rows) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + header.hashCode()
        __r = __r*31 + rows.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTableSegment (")
        printer.indent {
            print("header = "); header.print(printer); println()
            print("rows = "); rows.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:186]
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
 * #### Generated from [RdComment.kt:170]
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
 * #### Generated from [RdComment.kt:160]
 */
data class RdTextHighlighter (
    val key: String,
    val startOffset: Int,
    val endOffset: Int,
    val attributes: RdTextAttributes,
    val backgroundStyle: RdBackgroundStyle? = null,
    val animation: RdTextAnimation? = null,
    val references: List<RdReference>? = null
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
            return RdTextHighlighter(key, startOffset, endOffset, attributes, backgroundStyle, animation, references)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTextHighlighter)  {
            buffer.writeString(value.key)
            buffer.writeInt(value.startOffset)
            buffer.writeInt(value.endOffset)
            RdTextAttributes.write(ctx, buffer, value.attributes)
            buffer.writeNullable(value.backgroundStyle) { RdBackgroundStyle.write(ctx, buffer, it) }
            buffer.writeNullable(value.animation) { ctx.serializers.writePolymorphic(ctx, buffer, it) }
            buffer.writeNullable(value.references) { buffer.writeList(it) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) } }
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
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:138]
 */
class RdTextInvariant (
    val text: String
) : RdInvariant (
) {
    //companion
    
    companion object : IMarshaller<RdTextInvariant> {
        override val _type: KClass<RdTextInvariant> = RdTextInvariant::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTextInvariant  {
            val text = buffer.readString()
            return RdTextInvariant(text)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTextInvariant)  {
            buffer.writeString(value.text)
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
        printer.println("RdTextInvariant (")
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
 * #### Generated from [RdComment.kt:87]
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
 * #### Generated from [RdComment.kt:212]
 */
data class RdTicket (
    val url: String,
    val shortName: String
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdTicket> {
        override val _type: KClass<RdTicket> = RdTicket::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdTicket  {
            val url = buffer.readString()
            val shortName = buffer.readString()
            return RdTicket(url, shortName)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTicket)  {
            buffer.writeString(value.url)
            buffer.writeString(value.shortName)
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
        
        other as RdTicket
        
        if (url != other.url) return false
        if (shortName != other.shortName) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + url.hashCode()
        __r = __r*31 + shortName.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdTicket (")
        printer.indent {
            print("url = "); url.print(printer); println()
            print("shortName = "); shortName.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:201]
 */
abstract class RdToDo (
    val author: RdIntelligentCommentAuthor,
    val name: String,
    val description: RdContentSegments,
    val blockingReferences: List<RdReference>
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdToDo> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdToDo  {
            val objectStartPosition = buffer.position
            val author = RdIntelligentCommentAuthor.read(ctx, buffer)
            val name = buffer.readString()
            val description = RdContentSegments.read(ctx, buffer)
            val blockingReferences = buffer.readList { ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference) }
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdToDo_Unknown(author, name, description, blockingReferences, unknownId, unknownBytes)
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
 * #### Generated from [RdComment.kt:208]
 */
class RdToDoWithTickets (
    val tickets: List<RdTicket>,
    author: RdIntelligentCommentAuthor,
    name: String,
    description: RdContentSegments,
    blockingReferences: List<RdReference>
) : RdToDo (
    author,
    name,
    description,
    blockingReferences
) {
    //companion
    
    companion object : IMarshaller<RdToDoWithTickets> {
        override val _type: KClass<RdToDoWithTickets> = RdToDoWithTickets::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdToDoWithTickets  {
            val author = RdIntelligentCommentAuthor.read(ctx, buffer)
            val name = buffer.readString()
            val description = RdContentSegments.read(ctx, buffer)
            val blockingReferences = buffer.readList { ctx.serializers.readPolymorphic<RdReference>(ctx, buffer, RdReference) }
            val tickets = buffer.readList { RdTicket.read(ctx, buffer) }
            return RdToDoWithTickets(tickets, author, name, description, blockingReferences)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdToDoWithTickets)  {
            RdIntelligentCommentAuthor.write(ctx, buffer, value.author)
            buffer.writeString(value.name)
            RdContentSegments.write(ctx, buffer, value.description)
            buffer.writeList(value.blockingReferences) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) }
            buffer.writeList(value.tickets) { v -> RdTicket.write(ctx, buffer, v) }
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
        
        other as RdToDoWithTickets
        
        if (tickets != other.tickets) return false
        if (author != other.author) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (blockingReferences != other.blockingReferences) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + tickets.hashCode()
        __r = __r*31 + author.hashCode()
        __r = __r*31 + name.hashCode()
        __r = __r*31 + description.hashCode()
        __r = __r*31 + blockingReferences.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdToDoWithTickets (")
        printer.indent {
            print("tickets = "); tickets.print(printer); println()
            print("author = "); author.print(printer); println()
            print("name = "); name.print(printer); println()
            print("description = "); description.print(printer); println()
            print("blockingReferences = "); blockingReferences.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


class RdToDo_Unknown (
    author: RdIntelligentCommentAuthor,
    name: String,
    description: RdContentSegments,
    blockingReferences: List<RdReference>,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdToDo (
    author,
    name,
    description,
    blockingReferences
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdToDo_Unknown> {
        override val _type: KClass<RdToDo_Unknown> = RdToDo_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdToDo_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdToDo_Unknown)  {
            RdIntelligentCommentAuthor.write(ctx, buffer, value.author)
            buffer.writeString(value.name)
            RdContentSegments.write(ctx, buffer, value.description)
            buffer.writeList(value.blockingReferences) { v -> ctx.serializers.writePolymorphic(ctx, buffer, v) }
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
        
        other as RdToDo_Unknown
        
        if (author != other.author) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (blockingReferences != other.blockingReferences) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + author.hashCode()
        __r = __r*31 + name.hashCode()
        __r = __r*31 + description.hashCode()
        __r = __r*31 + blockingReferences.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdToDo_Unknown (")
        printer.indent {
            print("author = "); author.print(printer); println()
            print("name = "); name.print(printer); println()
            print("description = "); description.print(printer); println()
            print("blockingReferences = "); blockingReferences.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:60]
 */
class RdTypeParam (
    name: String,
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
            val name = buffer.readString()
            val content = RdContentSegments.read(ctx, buffer)
            return RdTypeParam(name, content)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTypeParam)  {
            buffer.writeString(value.name)
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
 * #### Generated from [RdComment.kt:187]
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
 * #### Generated from [RdComment.kt:130]
 */
enum class RdVerticalAlignment {
    Center, 
    Top, 
    Bottom;
    
    companion object {
        val marshaller = FrameworkMarshallers.enum<RdVerticalAlignment>()
        
    }
}
