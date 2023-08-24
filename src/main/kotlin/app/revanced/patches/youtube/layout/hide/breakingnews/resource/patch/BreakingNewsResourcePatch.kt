package app.revanced.patches.youtube.layout.hide.breakingnews.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.shared.settings.preference.impl.StringResource
import app.revanced.patches.shared.settings.preference.impl.SwitchPreference
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsPatch

@DependsOn([SettingsPatch::class, ResourceMappingPatch::class])
class BreakingNewsResourcePatch : ResourcePatch {
    companion object {
        internal var horizontalCardListId: Long = -1
    }

    override fun execute(context: ResourceContext) {
        horizontalCardListId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "horizontal_card_list"
        }.id
    }
}