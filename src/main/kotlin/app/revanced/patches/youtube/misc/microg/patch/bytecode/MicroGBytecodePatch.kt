package app.revanced.patches.youtube.misc.microg.patch.bytecode

import app.revanced.extensions.equalsAny
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableClass
import app.revanced.patches.youtube.layout.castbutton.patch.HideCastButtonPatch
import app.revanced.patches.youtube.misc.clientspoof.patch.ClientSpoofPatch
import app.revanced.patches.youtube.misc.microg.annotations.MicroGPatchCompatibility
import app.revanced.patches.youtube.misc.microg.fingerprints.*
import app.revanced.patches.youtube.misc.microg.patch.resource.MicroGResourcePatch
import app.revanced.patches.youtube.misc.microg.patch.resource.enum.StringReplaceMode
import app.revanced.patches.youtube.misc.microg.shared.Constants.BASE_MICROG_PACKAGE_NAME
import app.revanced.patches.youtube.misc.microg.shared.Constants.REVANCED_PACKAGE_NAME
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.MutableMethodImplementation
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.StringReference
import org.jf.dexlib2.immutable.reference.ImmutableStringReference

@Patch
@DependsOn(
    [
        MicroGResourcePatch::class,
        HideCastButtonPatch::class,
        ClientSpoofPatch::class
    ]
)
@Name("microg-support")
@Description("Allows YouTube ReVanced to run without root and under a different package name with Vanced MicroG.")
@MicroGPatchCompatibility
@Version("0.0.1")
class MicroGBytecodePatch : BytecodePatch(
    listOf(
        IntegrityCheckFingerprint,
        ServiceCheckFingerprint,
        GooglePlayUtilityFingerprint,
        CastDynamiteModuleFingerprint,
        CastDynamiteModuleV2Fingerprint,
        CastContextFetchFingerprint,
        PrimeFingerprint,
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        disablePlayServiceChecksAndFixCastIssues()
        data.classes.forEach { classDef ->
            var proxiedClass: MutableClass? = null

            classDef.methods.forEach methodLoop@{ method ->
                val implementation = method.implementation ?: return@methodLoop

                var proxiedImplementation: MutableMethodImplementation? = null

                implementation.instructions.forEachIndexed { i, instruction ->
                    if (instruction.opcode != Opcode.CONST_STRING) return@forEachIndexed

                    val stringValue = ((instruction as Instruction21c).reference as StringReference).string

                    val replaceMode = if (stringValue.equalsAny(
                            "com.google.android.gms",
                            "com.google.android.c2dm.intent.REGISTER",
                            "com.google.android.c2dm.permission.SEND",
                            "com.google.iid.TOKEN_REQUEST",
                            "com.google",
                            "com.google.android.gms.auth.accounts",
                            "com.google.android.c2dm.intent.REGISTRATION",
                            "com.google.android.gsf.action.GET_GLS",
                            "com.google.android.gsf.login",
                            "content://com.google.settings/partner",
                            "content://com.google.android.gsf.gservices",
                            "content://com.google.android.gsf.gservices/prefix",
                            "com.google.android.c2dm.intent.RECEIVE"
                        )
                    ) {
                        StringReplaceMode.REPLACE_WITH_MICROG
                    } else if (stringValue.equalsAny(
                            "com.google.android.youtube.SuggestionsProvider", "com.google.android.youtube.fileprovider"
                        )
                    ) {
                        StringReplaceMode.REPLACE_WITH_REVANCED
                    } else {
                        StringReplaceMode.DO_NOT_REPLACE
                    }

                    if (replaceMode != StringReplaceMode.DO_NOT_REPLACE) {
                        if (proxiedClass == null) {
                            proxiedClass = data.proxy(classDef).resolve()
                        }

                        if (proxiedImplementation == null) {
                            proxiedImplementation = proxiedClass!!.methods.first {
                                it.name == method.name && it.parameterTypes.containsAll(method.parameterTypes)
                            }.implementation!!
                        }

                        val newString = if (replaceMode == StringReplaceMode.REPLACE_WITH_REVANCED) stringValue.replace(
                            "com.google.android.youtube", REVANCED_PACKAGE_NAME
                        )
                        else stringValue.replace("com.google", BASE_MICROG_PACKAGE_NAME)

                        proxiedImplementation!!.replaceInstruction(
                            i, BuilderInstruction21c(
                                Opcode.CONST_STRING, instruction.registerA, ImmutableStringReference(newString)
                            )
                        )
                    }
                }
            }
        }

        return PatchResultSuccess()
    }

    private fun disablePlayServiceChecksAndFixCastIssues() {
        listOf(
            IntegrityCheckFingerprint,
            ServiceCheckFingerprint,
            GooglePlayUtilityFingerprint,
            CastDynamiteModuleFingerprint,
            CastDynamiteModuleV2Fingerprint,
            CastContextFetchFingerprint
        ).forEach { fingerprint ->
            val result = fingerprint.result!!
            val stringInstructions = when (result.method.returnType.first()) {
                'L' -> """
                        const/4 v0, 0x0
                        return-object v0
                        """

                'V' -> "return-void"
                'I' -> """
                        const/4 v0, 0x0
                        return v0
                        """

                else -> throw Exception("This case should never happen.")
            }
            result.mutableMethod.addInstructions(
                0, stringInstructions
            )
        }

        val primeMethod = PrimeFingerprint.result!!.mutableMethod
        val implementation = primeMethod.implementation!!

        var register = 2
        val index = implementation.instructions.indexOfFirst {
            if (it.opcode != Opcode.CONST_STRING) return@indexOfFirst false

            val instructionString = ((it as Instruction21c).reference as StringReference).string
            if (instructionString != "com.google.android.youtube") return@indexOfFirst false

            register = it.registerA
            return@indexOfFirst true
        }

        primeMethod.replaceInstruction(
            index, "const-string v$register, \"$REVANCED_PACKAGE_NAME\""
        )
    }
}
