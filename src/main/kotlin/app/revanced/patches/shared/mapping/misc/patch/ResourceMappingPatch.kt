package app.revanced.patches.shared.mapping.misc.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.apk.arsc.ResourceElement
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.ResourcePatch
import java.util.*


// TODO: remove this patch and put resourceMappings in the context instead.

@Name("resource-mapping")
@Description("Creates a map of public resources.")
@Version("0.0.1")
class ResourceMappingPatch : ResourcePatch {
    companion object {
        internal lateinit var resourceMappings: List<ResourceElement>
            private set
    }

    override fun execute(context: ResourceContext): PatchResult {
        resourceMappings = context.apkBundle.base.resourceMappings

        return PatchResult.Success
    }
}
