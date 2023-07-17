package app.revanced.patches.shared.mapping.misc.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.util.resources.ResourceUtils.resourceIdOf

class ResourceMappingPatch : ResourcePatch {
    companion object {
        private var resourceContext: ResourceContext? = null

        /**
         * Resolve a resource id for the specified resource.
         *
         * @param type The type of the resource.
         * @param name The name of the resource.
         * @return The id of the resource.
         */
        fun resourceIdOf(type: String, name: String) = resourceContext!!.resourceIdOf(type, name)
    }

    override suspend fun execute(context: ResourceContext) {
        resourceContext = context
    }
}
