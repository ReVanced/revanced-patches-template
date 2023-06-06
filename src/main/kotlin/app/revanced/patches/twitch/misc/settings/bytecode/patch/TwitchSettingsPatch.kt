package app.revanced.patches.twitch.misc.settings.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableField.Companion.toMutable
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.settings.preference.impl.PreferenceCategory
import app.revanced.patches.shared.settings.resource.patch.AbstractSettingsResourcePatch.Companion.addPreference
import app.revanced.patches.shared.settings.util.AbstractPreferenceScreen
import app.revanced.patches.twitch.misc.settings.annotations.TwitchSettingsCompatibility
import app.revanced.patches.twitch.misc.settings.fingerprints.MenuGroupsOnClickFingerprint
import app.revanced.patches.twitch.misc.settings.fingerprints.MenuGroupsUpdatedFingerprint
import app.revanced.patches.twitch.misc.settings.fingerprints.SettingsActivityOnCreateFingerprint
import app.revanced.patches.twitch.misc.settings.fingerprints.SettingsMenuItemEnumFingerprint
import app.revanced.patches.twitch.misc.settings.resource.patch.TwitchSettingsResourcePatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.immutable.ImmutableField

@Patch
@DependsOn([TwitchSettingsResourcePatch::class])
@Name("settings")
@Description("Adds settings menu to Twitch.")
@TwitchSettingsCompatibility
@Version("0.0.1")
class TwitchSettingsPatch : BytecodePatch(
    listOf(
        SettingsActivityOnCreateFingerprint,
        SettingsMenuItemEnumFingerprint,
        MenuGroupsUpdatedFingerprint,
        MenuGroupsOnClickFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        // Hook onCreate to handle fragment creation
        with(SettingsActivityOnCreateFingerprint.result!!) {
            val insertIndex = mutableMethod.implementation!!.instructions.size - 2
            mutableMethod.addInstructions(
                insertIndex,
                """
                        invoke-static       {p0}, $SETTINGS_HOOKS_CLASS->handleSettingsCreation(Landroidx/appcompat/app/AppCompatActivity;)Z
                        move-result         v0
                        if-eqz              v0, :no_rv_settings_init
                        return-void
                """,
                listOf(ExternalLabel("no_rv_settings_init", mutableMethod.instruction(insertIndex)))
            )
        }

        // Create new menu item for settings menu
        with(SettingsMenuItemEnumFingerprint.result!!) {
            injectMenuItem(
                REVANCED_SETTINGS_MENU_ITEM_NAME,
                REVANCED_SETTINGS_MENU_ITEM_ID,
                REVANCED_SETTINGS_MENU_ITEM_TITLE_RES,
                REVANCED_SETTINGS_MENU_ITEM_ICON_RES
            )
        }

        // Intercept settings menu creation and add new menu item
        with(MenuGroupsUpdatedFingerprint.result!!) {
            mutableMethod.addInstructions(
                0,
                """
                    sget-object             v0, $MENU_ITEM_ENUM_CLASS->$REVANCED_SETTINGS_MENU_ITEM_NAME:$MENU_ITEM_ENUM_CLASS 
                    invoke-static           {p1, v0}, $SETTINGS_HOOKS_CLASS->handleSettingMenuCreation(Ljava/util/List;Ljava/lang/Object;)Ljava/util/List;
                    move-result-object      p1
                """
            )
        }

        // Intercept onclick events for the settings menu
        with(MenuGroupsOnClickFingerprint.result!!) {
            val insertIndex = 0
            mutableMethod.addInstructions(
                insertIndex,
                """
                        invoke-static       {p1}, $SETTINGS_HOOKS_CLASS->handleSettingMenuOnClick(Ljava/lang/Enum;)Z
                        move-result         p2
                        if-eqz              p2, :no_rv_settings_onclick
                        sget-object         p1, $MENU_DISMISS_EVENT_CLASS->INSTANCE:$MENU_DISMISS_EVENT_CLASS
                        invoke-virtual      {p0, p1}, Ltv/twitch/android/core/mvp/viewdelegate/RxViewDelegate;->pushEvent(Ltv/twitch/android/core/mvp/viewdelegate/ViewDelegateEvent;)V
                        return-void
                """,
                listOf(ExternalLabel("no_rv_settings_onclick", mutableMethod.instruction(insertIndex)))
            )
        }

        return PatchResultSuccess()
    }

    internal companion object {
        /* Private members */
        private const val REVANCED_SETTINGS_MENU_ITEM_NAME = "RevancedSettings"
        private const val REVANCED_SETTINGS_MENU_ITEM_ID = 0x7
        private const val REVANCED_SETTINGS_MENU_ITEM_TITLE_RES = "revanced_settings"
        private const val REVANCED_SETTINGS_MENU_ITEM_ICON_RES = "ic_settings"

        private const val MENU_ITEM_ENUM_CLASS = "Ltv/twitch/android/feature/settings/menu/SettingsMenuItem;"
        private const val MENU_DISMISS_EVENT_CLASS = "Ltv/twitch/android/feature/settings/menu/SettingsMenuViewDelegate\$Event\$OnDismissClicked;"

        private const val INTEGRATIONS_PACKAGE = "app/revanced/twitch"
        private const val SETTINGS_HOOKS_CLASS = "L$INTEGRATIONS_PACKAGE/settingsmenu/SettingsHooks;"
        private const val REVANCED_UTILS_CLASS = "L$INTEGRATIONS_PACKAGE/utils/TwitchReVancedUtils;"

        private fun MethodFingerprintResult.injectMenuItem(
            name: String,
            value: Int,
            titleResourceName: String,
            iconResourceName: String
        ) {
            // Add new static enum member field
            mutableClass.staticFields.add(
                ImmutableField(
                    mutableMethod.definingClass,
                    name,
                    MENU_ITEM_ENUM_CLASS,
                    AccessFlags.PUBLIC or AccessFlags.FINAL or AccessFlags.ENUM or AccessFlags.STATIC,
                    null,
                    null,
                    null
                ).toMutable()
            )

            // Add initializer for the new enum member
            mutableMethod.addInstructions(
                mutableMethod.implementation!!.instructions.size - 4,
                """   
                new-instance        v0, $MENU_ITEM_ENUM_CLASS
                const-string        v1, "$titleResourceName"
                invoke-static       {v1}, $REVANCED_UTILS_CLASS->getStringId(Ljava/lang/String;)I
                move-result         v1
                const-string        v3, "$iconResourceName"
                invoke-static       {v3}, $REVANCED_UTILS_CLASS->getDrawableId(Ljava/lang/String;)I
                move-result         v3
                const-string        v4, "$name"
                const/4             v5, $value
                invoke-direct       {v0, v4, v5, v1, v3}, $MENU_ITEM_ENUM_CLASS-><init>(Ljava/lang/String;III)V 
                sput-object         v0, $MENU_ITEM_ENUM_CLASS->$name:$MENU_ITEM_ENUM_CLASS
            """
            )
        }
    }

    /**
     * Preference screens patches should add their settings to.
     */
    internal object PreferenceScreen : AbstractPreferenceScreen() {
        val ADS = CustomScreen("revanced_twitch_ads_screen", "revanced_twitch_ads_screen_title", "revanced_twitch_ads_screen_summary")
        val CHAT = CustomScreen("revanced_twitch_chat_screen", "revanced_twitch_chat_screen_title", "revanced_twitch_chat_screen_summary")
        val MISC = CustomScreen("revanced_twitch_misc_screen", "revanced_twitch_misc_screen_title", "revanced_twitch_misc_screen_summary")

        internal class CustomScreen(key: String, titleKey: String, summaryKey: String) : Screen(key, titleKey, summaryKey) {
            /* Categories */
            val GENERAL = CustomCategory("twitch_general","twitch_general_title")
            val OTHER = CustomCategory("twitch_other", "twitch_other_title")
            val CLIENT_SIDE = CustomCategory("twitch_client_ads", "twitch_client_ads_title")
            val SURESTREAM = CustomCategory("twitch_surestream_ads","twitch_surestream_ads_title")

            internal inner class CustomCategory(key: String, title: String) : Screen.Category(key, title) {

                /* For Twitch, we need to load our CustomPreferenceCategory class instead of the default one. */
                override fun transform(): PreferenceCategory {
                    return PreferenceCategory(
                        key,
                        titleKey,
                        preferences.sortedBy { it.titleKey },
                        "app.revanced.twitch.settingsmenu.preference.CustomPreferenceCategory"
                    )
                }
            }
        }

        override fun commit(screen: app.revanced.patches.shared.settings.preference.impl.PreferenceScreen) {
            addPreference(screen)
        }
    }


    override fun close() = PreferenceScreen.close()
}
