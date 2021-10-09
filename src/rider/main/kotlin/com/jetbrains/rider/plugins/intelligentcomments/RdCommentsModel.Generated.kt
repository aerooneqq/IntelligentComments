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
 * #### Generated from [RdComment.kt:7]
 */
class RdCommentsModel private constructor(
    private val _documents: RdMap<RdDocumentId, RdDocumentCommentsModel>,
    private val _evaluate: RdCall<Int, Boolean>
) : RdExtBase() {
    //companion
    
    companion object : ISerializersOwner {
        
        override fun registerSerializersCore(serializers: ISerializers)  {
            serializers.register(RdDocumentCommentsModel)
            serializers.register(RdIntelligentComment)
            serializers.register(RdIntelligentCommentAuthor)
            serializers.register(RdIntelligentCommentContent)
            serializers.register(RdTextSegment)
            serializers.register(RdTextInvariant)
            serializers.register(RdDependencyReference)
            serializers.register(RdHighlightedText)
            serializers.register(RdTextHighlighter)
            serializers.register(RdTextAttributes)
            serializers.register(RdBackgroundStyle)
            serializers.register(RdColor)
            serializers.register(RdUnderlineTextAnimation)
            serializers.register(RdFontStyle.marshaller)
            serializers.register(RdComment_Unknown)
            serializers.register(RdContentSegment_Unknown)
            serializers.register(RdInvariant_Unknown)
            serializers.register(RdReference_Unknown)
            serializers.register(RdFileBasedReference_Unknown)
            serializers.register(RdTextAnimation_Unknown)
        }
        
        
        
        
        const val serializationHash = 2051931797146116882L
        
    }
    override val serializersOwner: ISerializersOwner get() = RdCommentsModel
    override val serializationHash: Long get() = RdCommentsModel.serializationHash
    
    //fields
    val documents: IMutableViewableMap<RdDocumentId, RdDocumentCommentsModel> get() = _documents
    val evaluate: IRdCall<Int, Boolean> get() = _evaluate
    //methods
    //initializer
    init {
        bindableChildren.add("documents" to _documents)
        bindableChildren.add("evaluate" to _evaluate)
    }
    
    //secondary constructor
    internal constructor(
    ) : this(
        RdMap<RdDocumentId, RdDocumentCommentsModel>(AbstractPolymorphic(RdDocumentId), RdDocumentCommentsModel),
        RdCall<Int, Boolean>(FrameworkMarshallers.Int, FrameworkMarshallers.Bool)
    )
    
    //equals trait
    //hash code trait
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCommentsModel (")
        printer.indent {
            print("documents = "); _documents.print(printer); println()
            print("evaluate = "); _evaluate.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    override fun deepClone(): RdCommentsModel   {
        return RdCommentsModel(
            _documents.deepClonePolymorphic(),
            _evaluate.deepClonePolymorphic()
        )
    }
    //contexts
}
val Solution.rdCommentsModel get() = getOrCreateExtension("rdCommentsModel", ::RdCommentsModel)



/**
 * #### Generated from [RdComment.kt:84]
 */
data class RdBackgroundStyle (
    val backgroundColor: RdColor,
    val roundedRect: Boolean
) : IPrintable {
    //companion
    
    companion object : IMarshaller<RdBackgroundStyle> {
        override val _type: KClass<RdBackgroundStyle> = RdBackgroundStyle::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdBackgroundStyle  {
            val backgroundColor = RdColor.read(ctx, buffer)
            val roundedRect = buffer.readBool()
            return RdBackgroundStyle(backgroundColor, roundedRect)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdBackgroundStyle)  {
            RdColor.write(ctx, buffer, value.backgroundColor)
            buffer.writeBool(value.roundedRect)
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
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + backgroundColor.hashCode()
        __r = __r*31 + roundedRect.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdBackgroundStyle (")
        printer.indent {
            print("backgroundColor = "); backgroundColor.print(printer); println()
            print("roundedRect = "); roundedRect.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:89]
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
 * #### Generated from [RdComment.kt:12]
 */
abstract class RdComment (
    val offset: Int,
    val documentId: RdDocumentId
) : RdBindableBase() {
    //companion
    
    companion object : IAbstractDeclaration<RdComment> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdComment  {
            val objectStartPosition = buffer.position
            val _id = RdId.read(buffer)
            val offset = buffer.readInt()
            val documentId = ctx.serializers.readPolymorphic<RdDocumentId>(ctx, buffer, RdDocumentId)
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdComment_Unknown(offset, documentId, unknownId, unknownBytes).withId(_id)
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
    offset: Int,
    documentId: RdDocumentId,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdComment (
    offset,
    documentId
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdComment_Unknown> {
        override val _type: KClass<RdComment_Unknown> = RdComment_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdComment_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdComment_Unknown)  {
            value.rdid.write(buffer)
            buffer.writeInt(value.offset)
            ctx.serializers.writePolymorphic(ctx, buffer, value.documentId)
            buffer.writeByteArrayRaw(value.unknownBytes)
        }
        
        
    }
    //fields
    //methods
    //initializer
    //secondary constructor
    //equals trait
    //hash code trait
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdComment_Unknown (")
        printer.indent {
            print("offset = "); offset.print(printer); println()
            print("documentId = "); documentId.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    override fun deepClone(): RdComment_Unknown   {
        return RdComment_Unknown(
            offset,
            documentId,
            unknownId,
            unknownBytes
        )
    }
    //contexts
}


/**
 * #### Generated from [RdComment.kt:35]
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
 * #### Generated from [RdComment.kt:59]
 */
class RdDependencyReference (
    val referenceName: String,
    val dependencyDescription: String,
    filePath: String
) : RdFileBasedReference (
    filePath
) {
    //companion
    
    companion object : IMarshaller<RdDependencyReference> {
        override val _type: KClass<RdDependencyReference> = RdDependencyReference::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdDependencyReference  {
            val filePath = buffer.readString()
            val referenceName = buffer.readString()
            val dependencyDescription = buffer.readString()
            return RdDependencyReference(referenceName, dependencyDescription, filePath)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdDependencyReference)  {
            buffer.writeString(value.filePath)
            buffer.writeString(value.referenceName)
            buffer.writeString(value.dependencyDescription)
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
        
        other as RdDependencyReference
        
        if (referenceName != other.referenceName) return false
        if (dependencyDescription != other.dependencyDescription) return false
        if (filePath != other.filePath) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + referenceName.hashCode()
        __r = __r*31 + dependencyDescription.hashCode()
        __r = __r*31 + filePath.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdDependencyReference (")
        printer.indent {
            print("referenceName = "); referenceName.print(printer); println()
            print("dependencyDescription = "); dependencyDescription.print(printer); println()
            print("filePath = "); filePath.print(printer); println()
        }
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:8]
 */
class RdDocumentCommentsModel private constructor(
    private val _comments: RdList<RdComment>
) : RdBindableBase() {
    //companion
    
    companion object : IMarshaller<RdDocumentCommentsModel> {
        override val _type: KClass<RdDocumentCommentsModel> = RdDocumentCommentsModel::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdDocumentCommentsModel  {
            val _id = RdId.read(buffer)
            val _comments = RdList.read(ctx, buffer, AbstractPolymorphic(RdComment))
            return RdDocumentCommentsModel(_comments).withId(_id)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdDocumentCommentsModel)  {
            value.rdid.write(buffer)
            RdList.write(ctx, buffer, value._comments)
        }
        
        
    }
    //fields
    val comments: IMutableViewableList<RdComment> get() = _comments
    //methods
    //initializer
    init {
        bindableChildren.add("comments" to _comments)
    }
    
    //secondary constructor
    constructor(
    ) : this(
        RdList<RdComment>(AbstractPolymorphic(RdComment))
    )
    
    //equals trait
    //hash code trait
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdDocumentCommentsModel (")
        printer.indent {
            print("comments = "); _comments.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    override fun deepClone(): RdDocumentCommentsModel   {
        return RdDocumentCommentsModel(
            _comments.deepClonePolymorphic()
        )
    }
    //contexts
}


/**
 * #### Generated from [RdComment.kt:55]
 */
abstract class RdFileBasedReference (
    val filePath: String
) : RdReference (
) {
    //companion
    
    companion object : IAbstractDeclaration<RdFileBasedReference> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdFileBasedReference  {
            val objectStartPosition = buffer.position
            val filePath = buffer.readString()
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdFileBasedReference_Unknown(filePath, unknownId, unknownBytes)
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


class RdFileBasedReference_Unknown (
    filePath: String,
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdFileBasedReference (
    filePath
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdFileBasedReference_Unknown> {
        override val _type: KClass<RdFileBasedReference_Unknown> = RdFileBasedReference_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdFileBasedReference_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdFileBasedReference_Unknown)  {
            buffer.writeString(value.filePath)
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
        
        other as RdFileBasedReference_Unknown
        
        if (filePath != other.filePath) return false
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        __r = __r*31 + filePath.hashCode()
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdFileBasedReference_Unknown (")
        printer.indent {
            print("filePath = "); filePath.print(printer); println()
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
enum class RdFontStyle {
    Regular, 
    Bold;
    
    companion object {
        val marshaller = FrameworkMarshallers.enum<RdFontStyle>()
        
    }
}


/**
 * #### Generated from [RdComment.kt:64]
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
 * #### Generated from [RdComment.kt:17]
 */
class RdIntelligentComment private constructor(
    private val _authors: RdList<RdIntelligentCommentAuthor>,
    private val _date: RdOptionalProperty<Date>,
    private val _content: RdOptionalProperty<RdIntelligentCommentContent>,
    private val _invariants: RdList<RdInvariant>,
    private val _references: RdList<RdReference>,
    offset: Int,
    documentId: RdDocumentId
) : RdComment (
    offset,
    documentId
) {
    //companion
    
    companion object : IMarshaller<RdIntelligentComment> {
        override val _type: KClass<RdIntelligentComment> = RdIntelligentComment::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdIntelligentComment  {
            val _id = RdId.read(buffer)
            val offset = buffer.readInt()
            val documentId = ctx.serializers.readPolymorphic<RdDocumentId>(ctx, buffer, RdDocumentId)
            val _authors = RdList.read(ctx, buffer, RdIntelligentCommentAuthor)
            val _date = RdOptionalProperty.read(ctx, buffer, FrameworkMarshallers.DateTime)
            val _content = RdOptionalProperty.read(ctx, buffer, RdIntelligentCommentContent)
            val _invariants = RdList.read(ctx, buffer, AbstractPolymorphic(RdInvariant))
            val _references = RdList.read(ctx, buffer, AbstractPolymorphic(RdReference))
            return RdIntelligentComment(_authors, _date, _content, _invariants, _references, offset, documentId).withId(_id)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdIntelligentComment)  {
            value.rdid.write(buffer)
            buffer.writeInt(value.offset)
            ctx.serializers.writePolymorphic(ctx, buffer, value.documentId)
            RdList.write(ctx, buffer, value._authors)
            RdOptionalProperty.write(ctx, buffer, value._date)
            RdOptionalProperty.write(ctx, buffer, value._content)
            RdList.write(ctx, buffer, value._invariants)
            RdList.write(ctx, buffer, value._references)
        }
        
        
    }
    //fields
    val authors: IMutableViewableList<RdIntelligentCommentAuthor> get() = _authors
    val date: IOptProperty<Date> get() = _date
    val content: IOptProperty<RdIntelligentCommentContent> get() = _content
    val invariants: IMutableViewableList<RdInvariant> get() = _invariants
    val references: IMutableViewableList<RdReference> get() = _references
    //methods
    //initializer
    init {
        _authors.optimizeNested = true
        _date.optimizeNested = true
        _invariants.optimizeNested = true
        _references.optimizeNested = true
    }
    
    init {
        bindableChildren.add("authors" to _authors)
        bindableChildren.add("date" to _date)
        bindableChildren.add("content" to _content)
        bindableChildren.add("invariants" to _invariants)
        bindableChildren.add("references" to _references)
    }
    
    //secondary constructor
    constructor(
        offset: Int,
        documentId: RdDocumentId
    ) : this(
        RdList<RdIntelligentCommentAuthor>(RdIntelligentCommentAuthor),
        RdOptionalProperty<Date>(FrameworkMarshallers.DateTime),
        RdOptionalProperty<RdIntelligentCommentContent>(RdIntelligentCommentContent),
        RdList<RdInvariant>(AbstractPolymorphic(RdInvariant)),
        RdList<RdReference>(AbstractPolymorphic(RdReference)),
        offset,
        documentId
    )
    
    //equals trait
    //hash code trait
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdIntelligentComment (")
        printer.indent {
            print("authors = "); _authors.print(printer); println()
            print("date = "); _date.print(printer); println()
            print("content = "); _content.print(printer); println()
            print("invariants = "); _invariants.print(printer); println()
            print("references = "); _references.print(printer); println()
            print("offset = "); offset.print(printer); println()
            print("documentId = "); documentId.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    override fun deepClone(): RdIntelligentComment   {
        return RdIntelligentComment(
            _authors.deepClonePolymorphic(),
            _date.deepClonePolymorphic(),
            _content.deepClonePolymorphic(),
            _invariants.deepClonePolymorphic(),
            _references.deepClonePolymorphic(),
            offset,
            documentId
        )
    }
    //contexts
}


/**
 * #### Generated from [RdComment.kt:26]
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
 * #### Generated from [RdComment.kt:31]
 */
class RdIntelligentCommentContent private constructor(
    private val _segments: RdList<RdContentSegment>
) : RdBindableBase() {
    //companion
    
    companion object : IMarshaller<RdIntelligentCommentContent> {
        override val _type: KClass<RdIntelligentCommentContent> = RdIntelligentCommentContent::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdIntelligentCommentContent  {
            val _id = RdId.read(buffer)
            val _segments = RdList.read(ctx, buffer, AbstractPolymorphic(RdContentSegment))
            return RdIntelligentCommentContent(_segments).withId(_id)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdIntelligentCommentContent)  {
            value.rdid.write(buffer)
            RdList.write(ctx, buffer, value._segments)
        }
        
        
    }
    //fields
    val segments: IMutableViewableList<RdContentSegment> get() = _segments
    //methods
    //initializer
    init {
        _segments.optimizeNested = true
    }
    
    init {
        bindableChildren.add("segments" to _segments)
    }
    
    //secondary constructor
    constructor(
    ) : this(
        RdList<RdContentSegment>(AbstractPolymorphic(RdContentSegment))
    )
    
    //equals trait
    //hash code trait
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdIntelligentCommentContent (")
        printer.indent {
            print("segments = "); _segments.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    override fun deepClone(): RdIntelligentCommentContent   {
        return RdIntelligentCommentContent(
            _segments.deepClonePolymorphic()
        )
    }
    //contexts
}


/**
 * #### Generated from [RdComment.kt:42]
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
 * #### Generated from [RdComment.kt:51]
 */
abstract class RdReference (
) : IPrintable {
    //companion
    
    companion object : IAbstractDeclaration<RdReference> {
        override fun readUnknownInstance(ctx: SerializationCtx, buffer: AbstractBuffer, unknownId: RdId, size: Int): RdReference  {
            val objectStartPosition = buffer.position
            val unknownBytes = ByteArray(objectStartPosition + size - buffer.position)
            buffer.readByteArrayRaw(unknownBytes)
            return RdReference_Unknown(unknownId, unknownBytes)
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
    override val unknownId: RdId,
    val unknownBytes: ByteArray
) : RdReference (
), IUnknownInstance {
    //companion
    
    companion object : IMarshaller<RdReference_Unknown> {
        override val _type: KClass<RdReference_Unknown> = RdReference_Unknown::class
        
        @Suppress("UNCHECKED_CAST")
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): RdReference_Unknown  {
            throw NotImplementedError("Unknown instances should not be read via serializer")
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdReference_Unknown)  {
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
        
        
        return true
    }
    //hash code trait
    override fun hashCode(): Int  {
        var __r = 0
        return __r
    }
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdReference_Unknown (")
        printer.print(")")
    }
    
    override fun toString() = PrettyPrinter().singleLine().also { print(it) }.toString()
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:93]
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
 * #### Generated from [RdComment.kt:78]
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
 * #### Generated from [RdComment.kt:69]
 */
data class RdTextHighlighter (
    val key: String,
    val startOffset: Int,
    val endOffset: Int,
    val attributes: RdTextAttributes,
    val backgroundStyle: RdBackgroundStyle? = null,
    val animation: RdTextAnimation? = null
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
            return RdTextHighlighter(key, startOffset, endOffset, attributes, backgroundStyle, animation)
        }
        
        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: RdTextHighlighter)  {
            buffer.writeString(value.key)
            buffer.writeInt(value.startOffset)
            buffer.writeInt(value.endOffset)
            RdTextAttributes.write(ctx, buffer, value.attributes)
            buffer.writeNullable(value.backgroundStyle) { RdBackgroundStyle.write(ctx, buffer, it) }
            buffer.writeNullable(value.animation) { ctx.serializers.writePolymorphic(ctx, buffer, it) }
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
        }
        printer.print(")")
    }
    //deepClone
    //contexts
}


/**
 * #### Generated from [RdComment.kt:46]
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
 * #### Generated from [RdComment.kt:38]
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
 * #### Generated from [RdComment.kt:97]
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