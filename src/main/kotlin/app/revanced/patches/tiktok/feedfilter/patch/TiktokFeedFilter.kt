package app.revanced.patches.tiktok.feedfilter.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.tiktok.feedfilter.annotations.FeedFilterCompatibility
import app.revanced.patches.tiktok.feedfilter.fingerprints.FeedApiServiceLIZFingerprint
import app.revanced.patches.tiktok.misc.integrations.patch.TikTokIntegrationsPatch
import app.revanced.patches.tiktok.misc.settings.patch.TikTokSettingsPatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([TikTokIntegrationsPatch::class, TikTokSettingsPatch::class])
@Name("tiktok-feed-filter")
@Description("Filters tiktok videos: removing ads, removing livestreams.")
@FeedFilterCompatibility
@Version("0.0.1")
class TiktokFeedFilter : BytecodePatch(
    listOf(
        FeedApiServiceLIZFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val method = FeedApiServiceLIZFingerprint.result!!.mutableMethod
        for ((index, instruction) in method.implementation!!.instructions.withIndex()) {
            if (instruction.opcode != Opcode.RETURN_OBJECT) continue
            val feedItemsRegister = (instruction as OneRegisterInstruction).registerA
            method.addInstruction(
                index,
                "invoke-static {v$feedItemsRegister}, Lapp/revanced/integrations/tiktok/feedfilter/FeedItemsFilter;->filter(Lcom/ss/android/ugc/aweme/feed/model/FeedItemList;)V"
            )
            break
        }
        return PatchResultSuccess()
    }
}
