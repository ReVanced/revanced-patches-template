package app.revanced.patches.shared.mapping.misc.patch

import app.revanced.patcher.ResourceContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.apk.Apk
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.ResourcePatch
import org.w3c.dom.Element
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Name("resource-mapping")
@Description("Creates a map of public resources.")
@Version("0.0.1")
class ResourceMappingPatch : ResourcePatch {
    companion object {
        internal lateinit var resourceMappings: List<Apk.ResourceElement>
            private set
    }

    override fun execute(context: ResourceContext): PatchResult {
        resourceMappings = context.apkBundle.base.resourceMap!!

        return PatchResult.Success
    }
}
