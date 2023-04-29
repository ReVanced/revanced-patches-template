package app.revanced.patches.shared.mapping.misc.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.ResourceContext
import app.revanced.patcher.apk.ApkBundle
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.ResourcePatch

@Name("resource-mapping")
@Description("Creates a map of public resources.")
@Version("0.0.1")
class ResourceMappingPatch : ResourcePatch {
    companion object {
        private var resources: ApkBundle.GlobalResources? = null

        /**
         * Resolve a resource id for the specified resource.
         *
         * @param type The type of the resource.
         * @param name The name of the resource.
         * @return The id of the resource.
         */
        fun resourceIdOf(type: String, name: String) = resources!!.resolve(type, name).toLong()
    }

    override fun execute(context: ResourceContext): PatchResult {
        resources = context.apkBundle.resources

        return PatchResult.Success
    }
}
