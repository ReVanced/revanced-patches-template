package app.revanced.patches.youtube.layout.utils.navbarindexhook

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.layout.utils.navbarindexhook.fingerprints.MobileTopBarButtonOnClickFingerprint
import app.revanced.patches.youtube.layout.utils.navbarindexhook.fingerprints.NavButtonOnClickFingerprint
import app.revanced.patches.youtube.layout.utils.navbarindexhook.fingerprints.SettingsActivityOnBackPressedFingerprint
import app.revanced.patches.youtube.shared.fingerprints.WatchWhileActivityOnBackPressedFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Suppress("unused")
object NavBarIndexHookPatch : BytecodePatch(
    setOf(
        NavButtonOnClickFingerprint,
        SettingsActivityOnBackPressedFingerprint,
        WatchWhileActivityOnBackPressedFingerprint,
        MobileTopBarButtonOnClickFingerprint
    )
) {
    const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/utils/NavBarIndexHook;"

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
         *
         *  When we open Settings Activity from Library tab, NavBar index will be zero,
         *  so we call *setLastNavBarIndex* method on back press to fix the index
         */
        arrayOf(
            WatchWhileActivityOnBackPressedFingerprint,
            SettingsActivityOnBackPressedFingerprint
        ).map { it.result ?: throw it.exception }.forEach {
            it.mutableMethod.apply {
                addInstruction(
                    0,
                    "invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->setLastNavBarIndex()V"
                )
            }
        }

        /**
         * Set Navbar index to zero on clicking MobileTopBarButton (May be you want to switch to Incognito mode while in Library Tab)
         */
        MobileTopBarButtonOnClickFingerprint.result?.let {
            it.mutableMethod.addInstructions(
                0,
                """
                     const/4 v0, 0x0
                     invoke-static {v0}, $INTEGRATIONS_CLASS_DESCRIPTOR->setCurrentNavBarIndex(I)V
                """
            )
        }
    }
}