package app.revanced.patches.youtube.ad.general.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.ad.general.annotations.GeneralAdsRemovalPatchCompatibility
import app.revanced.patches.youtube.ad.general.fingerprints.LithoFingerprint
import app.revanced.patches.youtube.ad.general.fingerprints.PromotedVideoItemFullBleedFingerprint
import app.revanced.patches.youtube.ad.general.fingerprints.PromotedVideoItemLandFingerprint
import app.revanced.patches.youtube.ad.general.resource.patch.GeneralResourceAdsPatch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, ResourceMappingResourcePatch::class, GeneralResourceAdsPatch::class])
@Name("general-ads")
@Description("Removes general ads.")
@GeneralAdsRemovalPatchCompatibility
@Version("0.0.1")
class GeneralAdsRemovalPatch : BytecodePatch(
    listOf(
        PromotedVideoItemFullBleedFingerprint,
        PromotedVideoItemLandFingerprint,
        LithoFingerprint
    )
)
{
    internal companion object {
        // list of resource names to get the id of
        var resourceIds = arrayOf(
            "channel_name",
            "promoted_video_item_land",
        ).map { name ->
            ResourceMappingResourcePatch.resourceMappings.single { it.name == name }.id
        }
    }

    override fun execute(data: BytecodeContext): PatchResult {
        SettingsPatch.PreferenceScreen.ADS.addPreferences(
            SwitchPreference(
                "revanced_home_ads_removal",
                StringResource("revanced_home_ads_removal_title", "Remove home ads"),
                true,
                StringResource("revanced_home_ads_removal_summary_on", "Home ads are hidden"),
                StringResource("revanced_home_ads_removal_summary_off", "Home ads are shown")
            ),
            SwitchPreference(
                "revanced_general_ads_removal",
                StringResource("revanced_general_ads_enabled_title", "Remove general ads"),
                true,
                StringResource("revanced_general_ads_enabled_summary_on", "General ads are hidden"),
                StringResource("revanced_general_ads_enabled_summary_off", "General ads are shown")
            ),
            SwitchPreference(
                "revanced_merchandise",
                StringResource("revanced_merchandise_enabled_title", "Remove merchandise banners"),
                true,
                StringResource("revanced_merchandise_enabled_summary_on", "Merchandise banners are hidden"),
                StringResource("revanced_merchandise_enabled_summary_off", "Merchandise banners are shown")
            ),
            SwitchPreference(
                "revanced_community_posts_removal",
                StringResource("revanced_community_posts_enabled_title", "Remove community posts"),
                true,
                StringResource("revanced_community_posts_enabled_summary_on", "Community posts are hidden"),
                StringResource("revanced_community_posts_enabled_summary_off", "Community posts are shown")
            ),
            SwitchPreference(
                "revanced_compact_banner_removal",
                StringResource("revanced_compact_banner_enabled_title", "Remove compact banners"),
                true,
                StringResource("revanced_compact_banner_enabled_summary_on", "Compact banners are hidden"),
                StringResource("revanced_compact_banner_enabled_summary_off", "Compact banners are shown")
            ),
            SwitchPreference(
                "revanced_comments_removal",
                StringResource("revanced_comments_enabled_title", "Remove comments section"),
                false,
                StringResource("revanced_comments_enabled_summary_on", "Comment section is hidden"),
                StringResource("revanced_comments_enabled_summary_off", "Comment section is shown")
            ),
            SwitchPreference(
                "revanced_movie",
                StringResource("revanced_movie_enabled_title", "Remove movies section"),
                true,
                StringResource("revanced_movie_enabled_summary_on", "Movies section is hidden"),
                StringResource("revanced_movie_enabled_summary_off", "Movies section is shown")
            ),
            SwitchPreference(
                "revanced_feed_survey",
                StringResource("revanced_feed_survey_enabled_title", "Remove feed surveys"),
                true,
                StringResource("revanced_feed_survey_enabled_summary_on", "Feed surveys are hidden"),
                StringResource("revanced_feed_survey_enabled_summary_off", "Feed surveys are shown")
            ),
            SwitchPreference(
                "revanced_shorts_shelf",
                StringResource("revanced_shorts_shelf_enabled_title", "Remove shorts shelf"),
                true,
                StringResource("revanced_shorts_shelf_enabled_summary_on", "Shorts shelves are hidden"),
                StringResource("revanced_shorts_shelf_enabled_summary_off", "Shorts shelves are shown")
            ),
            SwitchPreference(
                "revanced_community_guidelines",
                StringResource("revanced_community_guidelines_enabled_title", "Remove community guidelines"),
                true,
                StringResource("revanced_community_guidelines_enabled_summary_on", "Community guidelines are hidden"),
                StringResource("revanced_community_guidelines_enabled_summary_off", "Community guidelines are shown")
            ),
            SwitchPreference(
                "revanced_emergency_box_removal",
                StringResource("revanced_emergency_box_enabled_title", "Remove emergency boxes"),
                true,
                StringResource("revanced_emergency_box_enabled_summary_on", "Emergency boxes are hidden"),
                StringResource("revanced_emergency_box_enabled_summary_off", "Emergency boxes are shown")
            ),
            SwitchPreference(
                "revanced_info_panel",
                StringResource("revanced_info_panel_enabled_title", "Remove info panels"),
                true,
                StringResource("revanced_info_panel_enabled_summary_on", "Merchandise banners are hidden"),
                StringResource("revanced_info_panel_enabled_summary_off", "Merchandise banners are shown")
            ),
            SwitchPreference(
                "revanced_medical_panel",
                StringResource("revanced_medical_panel_enabled_title", "Remove medical panels"),
                true,
                StringResource("revanced_medical_panel_enabled_summary_on", "Medical panels are hidden"),
                StringResource("revanced_medical_panel_enabled_summary_off", "Medical panels are shown")
            ),
            SwitchPreference(
                "revanced_paid_content",
                StringResource("revanced_paid_content_enabled_title", "Remove paid content"),
                true,
                StringResource("revanced_paid_content_enabled_summary_on", "Paid content is hidden"),
                StringResource("revanced_paid_content_enabled_summary_off", "Paid content is shown")
            ),
            SwitchPreference(
                "revanced_suggested",
                StringResource("revanced_suggested_enabled_title", "Remove personal suggestions"),
                true,
                StringResource("revanced_suggested_enabled_summary_on", "Personal suggestions are hidden"),
                StringResource("revanced_suggested_enabled_summary_off", "Personal suggestions are shown")
            ),
            SwitchPreference(
                "revanced_hide_suggestions",
                StringResource("revanced_hide_suggestions_enabled_title", "Hide suggestions"),
                true,
                StringResource("revanced_hide_suggestions_enabled_summary_on", "Suggestions are hidden"),
                StringResource("revanced_hide_suggestions_enabled_summary_off", "Suggestions are shown")
            ),
            SwitchPreference(
                "revanced_hide_latest_posts",
                StringResource("revanced_hide_latest_posts_enabled_title", "Hide latest posts"),
                true,
                StringResource("revanced_hide_latest_posts_enabled_summary_on", "Latest posts are hidden"),
                StringResource("revanced_hide_latest_posts_enabled_summary_off", "Latest posts are shown")
            ),
            SwitchPreference(
                "revanced_hide_channel_guidelines",
                StringResource("revanced_hide_channel_guidelines_enabled_title", "Hide channel guidelines"),
                true,
                StringResource("revanced_hide_channel_guidelines_enabled_summary_on", "Channel guidelines are hidden"),
                StringResource("revanced_hide_channel_guidelines_enabled_summary_off", "Channel guidelines are shown")
            ),
            SwitchPreference(
                "revanced_hide_player_live_chat_button",
                StringResource("revanced_hide_player_live_chat_button_title", "Hide live chat button on video player"),
                false,
                StringResource("revanced_hide_player_live_chat_button_on", "Live chat button is hidden"),
                StringResource("revanced_hide_player_live_chat_button_off", "Live chat button is shown")
            ),
            SwitchPreference(
                "revanced_hide_player_report_button",
                StringResource("revanced_hide_player_report_button_title", "Hide report button on video player"),
                false,
                StringResource("revanced_hide_player_report_button_summary_on", "Report button is hidden"),
                StringResource("revanced_hide_player_report_button_summary_off", "Report button is shown")
            ),
            SwitchPreference(
                "revanced_hide_player_create_short_button",
                StringResource("revanced_hide_player_create_short_button_title", "Create short button on video player"),
                false,
                StringResource("revanced_hide_player_create_short_button_summary_on", "Create short button is hidden"),
                StringResource("revanced_hide_player_create_short_button_summary_off", "Create short button is shown")
            ),
            SwitchPreference(
                "revanced_hide_player_thanks_button",
                StringResource("revanced_hide_player_thanks_button_title", "Hide thanks button on video player"),
                false,
                StringResource("revanced_hide_player_thanks_button_summary_on", "Thanks button is hidden"),
                StringResource("revanced_hide_player_thanks_button_summary_off", "Thanks button is shown")
            ),
            SwitchPreference(
                "revanced_hide_player_create_clip_button",
                StringResource("revanced_hide_player_create_clip_button_title", "Hide clip button on video player"),
                false,
                StringResource("revanced_hide_player_create_clip_button_summary_on", "Clip button is hidden"),
                StringResource("revanced_hide_player_create_clip_button_summary_off", "Clip button is shown")
            ),
            SwitchPreference(
                "revanced_hide_player_download_button",
                StringResource("revanced_hide_player_download_button_title", "Hide download button on video player"),
                false,
                StringResource("revanced_hide_player_download_button_summary_on", "Download button is hidden"),
                StringResource("revanced_hide_player_download_button_summary_off", "Download button is shown")
            ),
            SwitchPreference(
                "revanced_hide_player_spoiler_comment",
                StringResource("revanced_hide_player_spoiler_comment_title", "Hide spoiler comment on video player"),
                false,
                StringResource("revanced_hide_player_spoiler_comment_summary_on", "Spoiler comment is hidden"),
                StringResource("revanced_hide_player_spoiler_comment_summary_off", "Spoiler comment is shown")
            ),
            SwitchPreference(
                "revanced_hide_player_external_comment_box",
                StringResource("revanced_hide_player_external_comment_box_title", "Hide external comment box on video player"),
                false,
                StringResource("revanced_hide_player_external_comment_box_summary_on", "External comment box is hidden"),
                StringResource("revanced_hide_player_external_comment_box_summary_off", "External comment box is shown")
            ),
        )

        //General
        val jumpIndex = 1

        val promotedVideoItemFullBleedMethod = PromotedVideoItemFullBleedFingerprint.result!!.mutableMethod
        val promotedVideoItemFullBleedInstructions = promotedVideoItemFullBleedMethod.implementation!!.instructions
        val fullBleedMoveResultObjectIndex = 5

        val promotedVideoItemLandMethod = PromotedVideoItemLandFingerprint.result!!.mutableMethod
        val promotedVideoItemLandInstructions = promotedVideoItemLandMethod.implementation!!.instructions
        val itemLandMoveResultObjectIndex = promotedVideoItemLandInstructions.indexOfFirst {
            (it as? WideLiteralInstruction)?.wideLiteral == resourceIds[1]
        } + 2

        fun promotedVideoItemByteCode(instructions: List<BuilderInstruction>, index: Int): String {
            return "invoke-static {v${(instructions[index] as OneRegisterInstruction).registerA}}, Lapp/revanced/integrations/patches/HidePromotedVideoItemPatch;->hidePromotedVideoItem(Landroid/view/View;)V"
        }
        promotedVideoItemFullBleedMethod.addInstruction(
            fullBleedMoveResultObjectIndex + jumpIndex, promotedVideoItemByteCode(promotedVideoItemFullBleedInstructions, fullBleedMoveResultObjectIndex)
        )
        promotedVideoItemLandMethod.addInstruction(
            itemLandMoveResultObjectIndex + jumpIndex, promotedVideoItemByteCode(promotedVideoItemLandInstructions, itemLandMoveResultObjectIndex)
        )

        //Litho
        val lithoResult = LithoFingerprint.result
        val lithoMethod = lithoResult!!.mutableMethod

        val pathBuilderAnchorFingerprint = object : MethodFingerprint(
            opcodes = listOf(
                Opcode.CONST_16,
                Opcode.INVOKE_VIRTUAL,
                Opcode.IPUT_OBJECT
            )
        ) {}

        val pathBuilderScanResult = pathBuilderAnchorFingerprint.also {
            it.resolve(data, lithoMethod, lithoResult.classDef)
        }.result!!.scanResult.patternScanResult!!

        val clobberedRegister =
            (lithoMethod.instruction(pathBuilderScanResult.startIndex) as OneRegisterInstruction).registerA

        val insertIndex = pathBuilderScanResult.endIndex + 1
        lithoMethod.addInstructions(
            insertIndex, // right after setting the component.pathBuilder field,
            """
                move-object/from16 v$clobberedRegister, p3
                iget-object v$clobberedRegister, v$clobberedRegister, ${lithoMethod.parameters[2]}->b:Ljava/nio/ByteBuffer;
                invoke-static {v5, v2, v$clobberedRegister}, Lapp/revanced/integrations/patches/LithoFilterPatch;->filter(Ljava/lang/StringBuilder;Ljava/lang/String;Ljava/nio/ByteBuffer;)Z
                move-result v$clobberedRegister
                if-eqz v$clobberedRegister, :not_an_ad
                const/4 v0, 0x0
                return-object v0
            """, listOf(ExternalLabel("not_an_ad", lithoMethod.instruction(insertIndex)))
        )

        return PatchResultSuccess()
    }
}
