package app.revanced.patches.twitch.misc.settings.bytecode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.*
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableField.Companion.toMutable
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.twitch.misc.settings.annotations.SettingsCompatibility
import app.revanced.patches.twitch.misc.settings.components.BasePreference
import app.revanced.patches.twitch.misc.settings.components.impl.*
import app.revanced.patches.twitch.misc.settings.components.impl.PreferenceScreen
import app.revanced.patches.twitch.misc.settings.components.impl.StringResource
import app.revanced.patches.twitch.misc.settings.fingerprints.*
import app.revanced.patches.twitch.misc.settings.resource.patch.SettingsResourcePatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.immutable.ImmutableField
import java.io.Closeable

@Patch
@DependsOn([IntegrationsPatch::class, SettingsResourcePatch::class])
@Name("settings")
@Description("Add settings menu to Twitch.")
@SettingsCompatibility
@Version("0.0.1")
class SettingsPatch : BytecodePatch(
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

        addString("revanced_settings", "ReVanced Settings", false)
        addString("revanced_reboot_message", "Twitch needs to restart to apply your changes. Restart now?", false)
        addString("revanced_reboot", "Restart", false)
        addString("revanced_cancel", "Cancel", false)

        return PatchResultSuccess()
    }

    companion object {
       internal fun addString(identifier: String, value: String, formatted: Boolean = true) =
            SettingsResourcePatch.addString(identifier, value, formatted)

       internal fun addPreferenceScreens(vararg preferenceScreen: PreferenceScreen) =
           SettingsResourcePatch.addPreferenceScreens(*preferenceScreen)

       internal fun addRootPreferences(vararg preference: BasePreference) =
           SettingsResourcePatch.addRootPreferences(*preference)

        /* Private members */
        private const val REVANCED_SETTINGS_MENU_ITEM_NAME = "RevancedSettings"
        private const val REVANCED_SETTINGS_MENU_ITEM_ID = 0x7
        private const val REVANCED_SETTINGS_MENU_ITEM_TITLE_RES = "revanced_settings"
        private const val REVANCED_SETTINGS_MENU_ITEM_ICON_RES = "ic_settings"

        private const val MENU_ITEM_ENUM_CLASS = "Ltv/twitch/android/feature/settings/menu/SettingsMenuItem;"
        private const val MENU_DISMISS_EVENT_CLASS = "Ltv/twitch/android/feature/settings/menu/SettingsMenuViewDelegate\$Event\$OnDismissClicked;"

        private const val INTEGRATIONS_PACKAGE = "app/revanced/twitch"
        private const val SETTINGS_HOOKS_CLASS = "L$INTEGRATIONS_PACKAGE/settingsmenu/SettingsHooks;"
        private const val REVANCED_UTILS_CLASS = "L$INTEGRATIONS_PACKAGE/utils/ReVancedUtils;"

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
    internal enum class PreferenceCategoryHelper(
        private val key: String,
        private val title: String,
        private val preferences: MutableList<BasePreference> = mutableListOf()
    ) : Closeable {
        ADS("ads", "Ads"),
        CHAT("chat", "Chat"),
        MISC("misc", "Miscellaneous");

        /**
         * Target PreferenceScreen to insert category.
         * If null, it will be added to the root.
         */
        val targetScreen: PreferenceScreen? = null

        override fun close() {
            if (preferences.size == 0) return

            val category = PreferenceCategory(
                key,
                StringResource("${key}_title", title),
                preferences
            )

            if(targetScreen == null)
                addRootPreferences(category)
            else
                targetScreen.preferences.add(category)
        }

        /**
         * Add preferences to the preference screen.
         */
        fun addPreferences(vararg preferences: BasePreference) = this.preferences.addAll(preferences)
    }

    override fun close() = PreferenceCategoryHelper.values().forEach(PreferenceCategoryHelper::close)

}
