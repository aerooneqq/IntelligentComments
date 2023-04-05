@file:Suppress("EXPERIMENTAL_API_USAGE","EXPERIMENTAL_UNSIGNED_LITERALS","PackageDirectoryMismatch","UnusedImport","unused","LocalVariableName","CanBeVal","PropertyName","EnumEntryName","ClassName","ObjectPropertyName","UnnecessaryVariable","SpellCheckingInspection")
package com.jetbrains.rd.ide.model

import com.jetbrains.rd.framework.*
import com.jetbrains.rd.framework.base.*
import com.jetbrains.rd.framework.impl.*

import com.jetbrains.rd.util.lifetime.*
import com.jetbrains.rd.util.reactive.*
import com.jetbrains.rd.util.string.*
import com.jetbrains.rd.util.*
import kotlin.time.Duration
import kotlin.reflect.KClass
import kotlin.jvm.JvmStatic



/**
 * #### Generated from [RdComment.kt:12]
 */
class RdCommentsSettingsModel private constructor(
    private val _enableExperimentalFeatures: RdOptionalProperty<Boolean>
) : RdExtBase() {
    //companion
    
    companion object : ISerializersOwner {
        
        override fun registerSerializersCore(serializers: ISerializers)  {
        }
        
        
        
        
        
        const val serializationHash = 3658469664905839431L
        
    }
    override val serializersOwner: ISerializersOwner get() = RdCommentsSettingsModel
    override val serializationHash: Long get() = RdCommentsSettingsModel.serializationHash
    
    //fields
    val enableExperimentalFeatures: IOptProperty<Boolean> get() = _enableExperimentalFeatures
    //methods
    //initializer
    init {
        _enableExperimentalFeatures.optimizeNested = true
    }
    
    init {
        bindableChildren.add("enableExperimentalFeatures" to _enableExperimentalFeatures)
    }
    
    //secondary constructor
    internal constructor(
    ) : this(
        RdOptionalProperty<Boolean>(FrameworkMarshallers.Bool)
    )
    
    //equals trait
    //hash code trait
    //pretty print
    override fun print(printer: PrettyPrinter)  {
        printer.println("RdCommentsSettingsModel (")
        printer.indent {
            print("enableExperimentalFeatures = "); _enableExperimentalFeatures.print(printer); println()
        }
        printer.print(")")
    }
    //deepClone
    override fun deepClone(): RdCommentsSettingsModel   {
        return RdCommentsSettingsModel(
            _enableExperimentalFeatures.deepClonePolymorphic()
        )
    }
    //contexts
}
val ShellModel.rdCommentsSettingsModel get() = getOrCreateExtension("rdCommentsSettingsModel", ::RdCommentsSettingsModel)

