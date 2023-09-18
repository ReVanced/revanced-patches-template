package app.revanced.patches.youtube.layout.utils.navbarindexhook.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.layout.utils.navbarindexhook.fingerprints.NavButtonOnClickFingerprint
import app.revanced.patches.shared.fingerprints.OnBackPressedFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Name("Hook NavBar index")
class NavBarIndexHookPatch : BytecodePatch(
    listOf(
        NavButtonOnClickFingerprint,
        OnBackPressedFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        /**
         * Change NavBar Index value according to selected Tab
         */
        NavButtonOnClickFingerprint.result?.let {
            val insertIndex = it.scanResult.patternScanResult!!.endIndex + 1
            it.mutableMethod.apply {
                val instruction = getInstruction(insertIndex - 2)
                if (((instruction as Instruction35c).reference as MethodReference).name != "indexOf") throw NavButtonOnClickFingerprint.exception
                val indexRegister =
                    getInstruction<OneRegisterInstruction>(insertIndex - 1).registerA

                addInstruction(
                    insertIndex,
                    "invoke-static {v$indexRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->setCurrentNavBarIndex(I)V"
                )
            }
        } ?: throw NavButtonOnClickFingerprint.exception

        /**
         *  Set NavBar index to last index on back press
         */
        OnBackPressedFingerprint.result?.let {
            it.mutableMethod.apply {
                addInstruction(
                    0,
                    "invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->setLastNavBarIndex()V"
                )
            }
        } ?: throw OnBackPressedFingerprint.exception

        /**
         * Initialize NavBar Index
         */
        context.initializeNavBarIndex(INTEGRATIONS_CLASS_DESCRIPTOR, "initializeIndex")
    }

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "Lapp/revanced/integrations/utils/NavBarIndexHook;"

        /**
         * Initialize NavBar Index while creating WatchWhileActivity.
         *
         * @param classDescriptor target class for initializing the NavBar index.
         * @param methodDescriptor target method for initializing the NavBar index.
         */
        fun BytecodeContext.initializeNavBarIndex(
            classDescriptor: String,
            methodDescriptor: String
        ) {
            this.classes.forEach { classDef ->
                if (classDef.type.endsWith("/WatchWhileActivity;")) {
                    val onCreateMethod = classDef.methods.single { it.name == "onCreate" }

                    onCreateMethod.apply {
                        proxy(classDef).mutableClass.methods.first { it.name == "onCreate" }.also {
                            it.addInstruction(
                                2,
                                "invoke-static/range {p0 .. p0}, $classDescriptor->$methodDescriptor(Landroid/content/Context;)V"
                            )
                        }
                    }
                }
            }
        }
    }
}