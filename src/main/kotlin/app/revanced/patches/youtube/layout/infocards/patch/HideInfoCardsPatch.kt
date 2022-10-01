package app.revanced.patches.youtube.layout.infocards.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.infocards.annotations.HideInfoCardsCompatibility
import app.revanced.patches.youtube.layout.infocards.fingerprints.InfoCardsDrawerHeaderFingerprint
import app.revanced.patches.youtube.layout.infocards.fingerprints.InfoCardsFingerprint
import app.revanced.patches.youtube.layout.infocards.fingerprints.InfoCardsParentFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c
import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, ResourceMappingResourcePatch::class])
@Name("hide-info-cards")
@Description("Hides info-cards in videos.")
@HideInfoCardsCompatibility
@Version("0.0.1")
class HideInfoCardsPatch : BytecodePatch(
    listOf(
        InfoCardsParentFingerprint,
        InfoCardsDrawerHeaderFingerprint
    )
) {
    internal companion object {
        internal var drawerResourceId = ResourceMappingResourcePatch.resourceMappings.single {
            it.name == "info_cards_drawer_header"
        }.id
    }

    override fun execute(data: BytecodeData): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_info_cards_enabled",
                StringResource("revanced_info_cards_enabled_title", "Show info-cards"),
                false,
                StringResource("revanced_info_cards_enabled_summary_on", "Info-cards are shown"),
                StringResource("revanced_info_cards_enabled_summary_off", "Info-cards are hidden")
            )
        )

        val parentResult = InfoCardsParentFingerprint.result
            ?: return PatchResultError("Parent fingerprint not resolved!")


        InfoCardsFingerprint.resolve(data, parentResult.classDef)
        val result = InfoCardsFingerprint.result
            ?: return PatchResultError("Required parent method could not be found.")

        val method = result.mutableMethod
        val implementation = method.implementation
            ?: return PatchResultError("Implementation not found.")

        val index =
            implementation.instructions.indexOfFirst { ((it as? BuilderInstruction35c)?.reference.toString() == "Landroid/view/View;->setVisibility(I)V") }

        method.replaceInstruction(
            index, """
            invoke-static {p1}, Lapp/revanced/integrations/patches/HideInfoCardsPatch;->hide(Landroid/view/View;)V
        """)

        // hide the drawer to prevent the info card to pop up for a second
        val infoCardsDrawerHeaderMethod = InfoCardsDrawerHeaderFingerprint.result!!.mutableMethod
        val infoCardsDrawerHeaderInstructions = infoCardsDrawerHeaderMethod.implementation!!.instructions

        val invokeInterfaceIndex = infoCardsDrawerHeaderInstructions.indexOfFirst {
            (it as? WideLiteralInstruction)?.wideLiteral == drawerResourceId
        } - 4

        val toggleRegister = infoCardsDrawerHeaderMethod.implementation!!.registerCount - 1
        val jumpInstruction = infoCardsDrawerHeaderInstructions[invokeInterfaceIndex + 1] as Instruction
        infoCardsDrawerHeaderMethod.addInstructions(
            invokeInterfaceIndex, """
                invoke-static {}, Lapp/revanced/integrations/patches/HideInfoCardsPatch;->hideDrawerHeader()Z
                move-result v$toggleRegister
                if-eqz v$toggleRegister, :hide
            """, listOf(ExternalLabel("hide", jumpInstruction))
        )

        return PatchResultSuccess()
    }

}