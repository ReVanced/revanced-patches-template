package app.revanced.patches.youtube.layout.hide.suggestionsshelf

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.shared.mapping.misc.ResourceMappingPatch

@Patch(dependencies = [ResourceMappingPatch::class])
object SuggestionsShelfResourcePatch: ResourcePatch() {
    internal var horizontalCardListId = -1L

    override fun execute(context: ResourceContext) {
        horizontalCardListId = ResourceMappingPatch.resourceMappings.single {
            it.type == "layout" && it.name == "horizontal_card_list"
        }.id
    }
}