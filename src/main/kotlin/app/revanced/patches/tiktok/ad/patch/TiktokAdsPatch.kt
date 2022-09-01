package app.revanced.patches.tiktok.ad.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.tiktok.ad.annotations.TiktokAdsCompatibility
import app.revanced.patches.youtube.ad.general.bytecode.extensions.MethodExtensions.findMutableMethodOf
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n
import org.jf.dexlib2.iface.instruction.formats.Instruction22c

@Patch
@Name("tiktok-ads")
@Description("Removes ads from TikTok.")
@TiktokAdsCompatibility
@Version("0.0.1")
class TiktokAdsPatch : BytecodePatch() {
    override fun execute(data: BytecodeData): PatchResult {
        //Find the preloadAds FieldReference
        val feedItemListClazz = data.classes.find { it.type.endsWith("/FeedItemList;") }
            ?: return PatchResultError("Can not find target class.")
        val preloadAdsField = feedItemListClazz.fields.find { it.name == "preloadAds" }
            ?: return PatchResultError("Can not find target field.")

        // iterating through all instructions to find and patch "setter" of field "preloadAds" to set it null
        data.classes.forEach { classDef ->
            var mutableClass: MutableClass? = null
            classDef.methods.forEach method@{ method ->
                var mutableMethod: MutableMethod? = null
                if (method.implementation == null) return@method
                val instructions = method.implementation!!.instructions
                instructions.forEachIndexed { index, instruction ->
                    when (instruction.opcode) {
                        Opcode.IPUT_OBJECT -> {
                            val targetInstruction = instruction as Instruction22c
                            when (targetInstruction.reference) {
                                preloadAdsField -> {
                                    // create proxied method, make sure to not re-resolve() the current class
                                    if (mutableClass == null) mutableClass = data.proxy(classDef).resolve()
                                    if (mutableMethod == null) mutableMethod =
                                        mutableClass!!.findMutableMethodOf(method)

                                    // patch the setter make it always set field preloadAds null
                                    val overrideRegister = targetInstruction.registerA
                                    mutableMethod!!.implementation!!.addInstruction(
                                        index,
                                        BuilderInstruction11n(Opcode.CONST_4, overrideRegister, 0)
                                    )
                                }
                            }
                        }
                        else -> return@forEachIndexed
                    }
                }
            }
        }
        return PatchResultSuccess()
    }
}
