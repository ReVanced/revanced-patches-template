package app.revanced.patches.youtube.layout.personalinformation.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.layout.personalinformation.annotations.HideEmailAddressCompatibility
import app.revanced.patches.youtube.layout.personalinformation.fingerprints.AccountSwitcherAccessibilityLabelFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.mapping.patch.ResourceMappingResourcePatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch
import app.revanced.patches.youtube.misc.settings.framework.components.impl.StringResource
import app.revanced.patches.youtube.misc.settings.framework.components.impl.SwitchPreference
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction
import org.jf.dexlib2.iface.reference.MethodReference

@Patch
@DependsOn([IntegrationsPatch::class, SettingsPatch::class, ResourceMappingResourcePatch::class])
@Name("hide-email-address")
@Description("Hides the email address in the account switcher.")
@HideEmailAddressCompatibility
@Version("0.0.1")
class HideEmailAddressPatch : BytecodePatch(
    listOf(
        AccountSwitcherAccessibilityLabelFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        SettingsPatch.PreferenceScreen.LAYOUT.addPreferences(
            SwitchPreference(
                "revanced_hide_email_address",
                StringResource("revanced_hide_email_address_title", "Hide the email address"),
                false,
                StringResource("revanced_hide_email_address_summary_on", "Email address is hidden"),
                StringResource("revanced_hide_email_address_summary_off", "Email address is visible")
            )
        )

        val accountSwitcherAccessibilityLabelId =
            ResourceMappingResourcePatch.resourceMappings.single {
                it.type == "string" && it.name == "account_switcher_accessibility_label"
            }.id

        val accountSwitcherAccessibilityLabelMethod = AccountSwitcherAccessibilityLabelFingerprint.result!!.mutableMethod
        val accountSwitcherAccessibilityLabelInstruction = accountSwitcherAccessibilityLabelMethod.implementation!!.instructions

        val resourceIndex = accountSwitcherAccessibilityLabelInstruction.indexOfFirst {
            it.opcode == Opcode.CONST &&
                    (it as? WideLiteralInstruction)?.wideLiteral == accountSwitcherAccessibilityLabelId
        }
        val setVisibilityIndex = accountSwitcherAccessibilityLabelInstruction.subList(
            resourceIndex,
            accountSwitcherAccessibilityLabelInstruction.size - 1
        ).indexOfFirst {
            it.opcode == Opcode.INVOKE_VIRTUAL &&
                    (((it as? BuilderInstruction35c)?.reference as? MethodReference)?.name == "setVisibility")
        } + resourceIndex

        val setVisibilityRegister = (accountSwitcherAccessibilityLabelInstruction[setVisibilityIndex] as BuilderInstruction35c).registerD
        val toggleRegister = (setVisibilityRegister - 1)

        accountSwitcherAccessibilityLabelMethod.addInstructions(
            setVisibilityIndex, """
            invoke-static {}, Lapp/revanced/integrations/patches/HideEmailAddressPatch;->hideEmailAddress()Z
            move-result v$toggleRegister
            if-eqz v$toggleRegister, :hide
            const/16 v$setVisibilityRegister, 0x8
            :hide
            nop
        """
        )

        return PatchResultSuccess()
    }
}
