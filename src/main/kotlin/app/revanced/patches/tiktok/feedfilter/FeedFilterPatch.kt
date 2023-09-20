package app.revanced.patches.tiktok.feedfilter

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.tiktok.feedfilter.fingerprints.FeedApiServiceLIZFingerprint
import app.revanced.patches.tiktok.misc.integrations.IntegrationsPatch
import app.revanced.patches.tiktok.misc.settings.SettingsPatch
import app.revanced.patches.tiktok.misc.settings.fingerprints.SettingsStatusLoadFingerprint
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Feed filter",
    description = "Filters tiktok videos: removing ads, removing livestreams.",
    dependencies = [IntegrationsPatch::class, SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage("com.ss.android.ugc.trill"),
        CompatiblePackage("com.zhiliaoapp.musically")
    ]
)
@Suppress("unused")
object FeedFilterPatch : BytecodePatch(setOf(FeedApiServiceLIZFingerprint, SettingsStatusLoadFingerprint)) {
    override fun execute(context: BytecodeContext) {
        val method = FeedApiServiceLIZFingerprint.result!!.mutableMethod
        for ((index, instruction) in method.implementation!!.instructions.withIndex()) {
            if (instruction.opcode != Opcode.RETURN_OBJECT) continue
            val feedItemsRegister = (instruction as OneRegisterInstruction).registerA
            method.addInstruction(
                index,
                "invoke-static {v$feedItemsRegister}, Lapp/revanced/tiktok/feedfilter/FeedItemsFilter;->filter(Lcom/ss/android/ugc/aweme/feed/model/FeedItemList;)V"
            )
            break
        }

        SettingsStatusLoadFingerprint.result?.mutableMethod?.addInstruction(
            0,
            "invoke-static {}, Lapp/revanced/tiktok/settingsmenu/SettingsStatus;->enableFeedFilter()V"
        ) ?: throw SettingsStatusLoadFingerprint.exception
    }
}
