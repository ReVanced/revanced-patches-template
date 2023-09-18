package app.revanced.patches.youtube.layout.hide.suggestionsshelf.resource.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.mapping.misc.patch.ResourceMappingPatch
import app.revanced.patches.youtube.layout.hide.suggestionsshelf.patch.SuggestionsShelfPatch

@DependsOn([ResourceMappingPatch::class])
class BreakingNewsResourcePatch: ResourcePatch {
    override fun execute(context: ResourceContext) {
        SuggestionsShelfPatch.horizontalCardListId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "horizontal_card_list"
        }.id
    }

    internal companion object {
        var horizontalCardListId = -1L
    }
}