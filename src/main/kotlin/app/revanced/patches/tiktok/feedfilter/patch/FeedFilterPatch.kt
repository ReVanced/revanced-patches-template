package app.revanced.patches.tiktok.feedfilter.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.tiktok.feedfilter.annotations.FeedFilterCompatibility
import app.revanced.patches.tiktok.feedfilter.fingerprints.FeedApiServiceLIZFingerprint
import app.revanced.patches.tiktok.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.tiktok.misc.settings.fingerprints.SettingsStatusLoadFingerprint
import app.revanced.patches.tiktok.misc.settings.patch.SettingsPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class])
@Name("feed-filter")
@Description("Filters tiktok videos: removing ads, removing livestreams.")
@FeedFilterCompatibility
@Version("0.0.1")
class FeedFilterPatch : BytecodePatch(
    listOf(
        FeedApiServiceLIZFingerprint,
        SettingsStatusLoadFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        FeedApiServiceLIZFingerprint.result?.let {
            val index = it.scanResult.patternScanResult!!.startIndex

            with (it.mutableMethod) {
                val register = (this.implementation!!.instructions[index] as OneRegisterInstruction).registerA
                addInstruction(
                    index,
                    "invoke-static {v$register}, Lapp/revanced/tiktok/feedfilter/FeedItemsFilter;->filter(Lcom/ss/android/ugc/aweme/feed/model/FeedItemList;)V"
                )
            }
        } ?: return FeedApiServiceLIZFingerprint.toErrorResult()

        SettingsStatusLoadFingerprint.result?.mutableMethod?.addInstruction(
            0,
            "invoke-static {}, Lapp/revanced/tiktok/settingsmenu/SettingsStatus;->enableFeedFilter()V"
        ) ?: return SettingsStatusLoadFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}