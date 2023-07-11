package app.revanced.patches.twitch.ad.embedded.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.MethodFingerprintExtensions.name
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.settings.preference.impl.ArrayResource
import app.revanced.patches.shared.settings.preference.impl.ListPreference
import app.revanced.patches.twitch.ad.embedded.annotations.EmbeddedAdsCompatibility
import app.revanced.patches.twitch.ad.embedded.fingerprints.CreateUsherClientFingerprint
import app.revanced.patches.twitch.ad.video.patch.VideoAdsPatch
import app.revanced.patches.twitch.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.twitch.misc.settings.bytecode.patch.TwitchSettingsPatch

@Patch
@DependsOn([VideoAdsPatch::class, IntegrationsPatch::class, TwitchSettingsPatch::class])
@Name("Block embedded ads")
@Description("Blocks embedded stream ads using services like TTV.lol or PurpleAdBlocker.")
@EmbeddedAdsCompatibility
@Version("0.0.1")
class EmbeddedAdsPatch : BytecodePatch(
    listOf(CreateUsherClientFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val result = CreateUsherClientFingerprint.result ?: return PatchResultError("${CreateUsherClientFingerprint.name} not found")

        // Inject OkHttp3 application interceptor
        result.mutableMethod.addInstructions(
            3,
            """
                invoke-static  {}, Lapp/revanced/twitch/patches/EmbeddedAdsPatch;->createRequestInterceptor()Lapp/revanced/twitch/api/RequestInterceptor;
                move-result-object v2
                invoke-virtual {v0, v2}, Lokhttp3/OkHttpClient${"$"}Builder;->addInterceptor(Lokhttp3/Interceptor;)Lokhttp3/OkHttpClient${"$"}Builder;
            """
        )

        TwitchSettingsPatch.PreferenceScreen.ADS.SURESTREAM.addPreferences(
            ListPreference(
                "revanced_block_embedded_ads",
                    "revanced_block_embedded_ads",
                ArrayResource(
                    "revanced_hls_proxies",
                    listOf(
                        "revanced_proxy_disabled",
                        "revanced_proxy_ttv_lol",
                        "revanced_proxy_purpleadblock",
                    )
                ),
                ArrayResource(
                    "revanced_hls_proxies_values",
                    listOf(
                        "key_revanced_proxy_disabled",
                        "key_revanced_proxy_ttv_lol",
                        "key_revanced_proxy_purpleadblock",
                    )
                ),
                default = "ttv-lol"
            )
        )

        return PatchResultSuccess()
    }
}
