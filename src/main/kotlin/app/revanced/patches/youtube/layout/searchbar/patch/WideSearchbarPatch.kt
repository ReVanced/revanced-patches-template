package app.revanced.patches.youtube.layout.searchbar.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.layout.searchbar.annotations.WideSearchbarCompatibility
import app.revanced.patches.youtube.layout.searchbar.fingerprints.CreateSearchSuggestionsFingerprint
import app.revanced.patches.youtube.layout.searchbar.fingerprints.SetWordmarkHeaderFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.YouTubeSettingsPatch

@Patch
@DependsOn([IntegrationsPatch::class, YouTubeSettingsPatch::class])
@Name("Wide searchbar")
@Description("Replaces the search icon with a wide search bar. This will hide the YouTube logo when active.")
@WideSearchbarCompatibility
class WideSearchbarPatch : BytecodePatch(
    listOf(
        SetWordmarkHeaderFingerprint, CreateSearchSuggestionsFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        YouTubeSettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_wide_searchbar",
                "revanced_wide_searchbar_enabled_title",
                "revanced_wide_searchbar_summary_on",
                "revanced_wide_searchbar_summary_off"
            )
        )

        val result = CreateSearchSuggestionsFingerprint.result ?: return CreateSearchSuggestionsFingerprint.toErrorResult()

        // patch methods
        mapOf(
            SetWordmarkHeaderFingerprint to 1,
            CreateSearchSuggestionsFingerprint to result.scanResult.patternScanResult!!.startIndex
        ).forEach { (fingerprint, callIndex) ->
            context.walkMutable(callIndex, fingerprint).injectSearchBarHook()
        }

        return PatchResultSuccess()
    }

    private companion object {
        /**
         * Walk a fingerprints method at a given index mutably.
         *
         * @param index The index to walk at.
         * @param fromFingerprint The fingerprint to walk the method on.
         * @return The [MutableMethod] which was walked on.
         */
        fun BytecodeContext.walkMutable(index: Int, fromFingerprint: MethodFingerprint) =
            fromFingerprint.result?.let {
                toMethodWalker(it.method).nextMethod(index, true).getMethod() as MutableMethod
            } ?: throw fromFingerprint.toErrorResult()


        /**
         * Injects instructions required for certain methods.
         */
        fun MutableMethod.injectSearchBarHook() {
            addInstructions(
                implementation!!.instructions.size - 1,
                """
                    invoke-static {}, Lapp/revanced/integrations/patches/WideSearchbarPatch;->enableWideSearchbar()Z
                    move-result p0
                """
            )
        }
    }
}
