package app.revanced.patches.youtube.layout.hide.shorts

import app.revanced.extensions.exception
import app.revanced.extensions.findIndexForIdResource
import app.revanced.extensions.injectHideViewCall
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch
import app.revanced.patches.youtube.layout.hide.shorts.fingerprints.*
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.litho.filter.LithoFilterPatch
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

@Patch(
    name = "Hide Shorts components",
    description = "Hides components from YouTube Shorts.",
    dependencies = [
        IntegrationsPatch::class,
        LithoFilterPatch::class,
        HideShortsComponentsResourcePatch::class,
        ResourceMappingPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube", [
                "18.16.37",
                "18.19.35",
                "18.20.39",
                "18.23.35",
                "18.29.38",
                "18.32.39"
            ]
        )
    ]
)
@Suppress("unused")
object HideShortsComponentsPatch : BytecodePatch(
    setOf(
        CreateShortsButtonsFingerprint,
        ReelConstructorFingerprint,
        BottomNavigationBarFingerprint,
        RenderBottomNavigationBarParentFingerprint,
        SetPivotBarVisibilityParentFingerprint
    )
) {
    private const val FILTER_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/components/ShortsFilter;"

    override fun execute(context: BytecodeContext) {
        LithoFilterPatch.addFilter(FILTER_CLASS_DESCRIPTOR)

        // region Hide the Shorts shelf.

        ReelConstructorFingerprint.result?.let {
            it.mutableMethod.apply {
                val insertIndex = it.scanResult.patternScanResult!!.startIndex + 2
                val viewRegister = getInstruction<TwoRegisterInstruction>(insertIndex).registerA

                injectHideViewCall(
                    insertIndex,
                    viewRegister,
                    FILTER_CLASS_DESCRIPTOR,
                    "hideShortsShelf"
                )
            }
        } ?: throw ReelConstructorFingerprint.exception

        // endregion

        // region Hide the Shorts buttons.

        // Some Shorts buttons are views, hide them by setting their visibility to GONE.
        CreateShortsButtonsFingerprint.result?.let {
            ShortsButtons.values().forEach { button -> button.injectHideCall(it.mutableMethod) }
        } ?: throw CreateShortsButtonsFingerprint.exception

        // endregion

        // region Hide the navigation bar.

        // Hook to get the pivotBar view.
        SetPivotBarVisibilityParentFingerprint.result?.let {
            if (!SetPivotBarVisibilityFingerprint.resolve(context, it.classDef))
                throw SetPivotBarVisibilityFingerprint.exception

            SetPivotBarVisibilityFingerprint.result!!.let { result ->
                result.mutableMethod.apply {
                    val checkCastIndex = result.scanResult.patternScanResult!!.endIndex
                    val viewRegister = getInstruction<OneRegisterInstruction>(checkCastIndex).registerA
                    addInstruction(
                        checkCastIndex + 1,
                        "sput-object v$viewRegister, $FILTER_CLASS_DESCRIPTOR->pivotBar:" +
                                "Lcom/google/android/libraries/youtube/rendering/ui/pivotbar/PivotBar;"
                    )
                }
            }
        } ?: throw SetPivotBarVisibilityParentFingerprint.exception

        // Hook to hide the navigation bar when Shorts are being played.
        RenderBottomNavigationBarParentFingerprint.result?.let {
            if (!RenderBottomNavigationBarFingerprint.resolve(context, it.classDef))
                throw RenderBottomNavigationBarFingerprint.exception

            RenderBottomNavigationBarFingerprint.result!!.mutableMethod.apply {
                addInstruction(0, "invoke-static { }, $FILTER_CLASS_DESCRIPTOR->hideNavigationBar()V")
            }
        } ?: throw RenderBottomNavigationBarParentFingerprint.exception

        // Required to prevent a black bar from appearing at the bottom of the screen.
        BottomNavigationBarFingerprint.result?.let {
            it.mutableMethod.apply {
                val moveResultIndex = it.scanResult.patternScanResult!!.startIndex
                val viewRegister = getInstruction<OneRegisterInstruction>(moveResultIndex).registerA
                val insertIndex = moveResultIndex + 1

                addInstruction(
                    insertIndex,
                    "invoke-static { v$viewRegister }, $FILTER_CLASS_DESCRIPTOR->" +
                            "hideNavigationBar(Landroid/view/View;)Landroid/view/View;"
                )
            }
        } ?: throw BottomNavigationBarFingerprint.exception

        // endregion
    }


    private enum class ShortsButtons(private val resourceName: String, private val methodName: String) {
        COMMENTS("reel_dyn_comment", "hideShortsCommentsButton"),
        REMIX("reel_dyn_remix", "hideShortsRemixButton"),
        SHARE("reel_dyn_share", "hideShortsShareButton");

        fun injectHideCall(method: MutableMethod) {
            val referencedIndex = method.findIndexForIdResource(resourceName)

            val setIdIndex = referencedIndex + 1
            val viewRegister = method.getInstruction<FiveRegisterInstruction>(setIdIndex).registerC
            method.injectHideViewCall(setIdIndex, viewRegister, FILTER_CLASS_DESCRIPTOR, methodName)
        }
    }
}
