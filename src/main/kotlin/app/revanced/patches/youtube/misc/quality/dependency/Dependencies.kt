package app.revanced.patches.youtube.misc.quality.dependency

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.youtube.misc.quality.annotations.DefaultVideoQualityCompatibility
import app.revanced.patches.youtube.misc.quality.patch.DefaultVideoQualityPatch

@Name("quality-dependency")
@Description("Adds missing dependencies for the quality patch.")
@DefaultVideoQualityCompatibility
@Version("0.0.1")

class QualityDependency : BytecodePatch(listOf()) {

    override fun execute(data: BytecodeData): PatchResult {
        data.classes.forEach { classDef ->
            classDef.methods.forEach { method ->
                if (classDef.type.endsWith("PlaybackLifecycleMonitor;") && method.name == "l") {
                    val startMethod =
                        data.proxy(classDef).resolve().methods.first { it.name == "l" }

                    startMethod.addInstructions(
                        30, """
					const/4 v6, 0x1
    					invoke-static {v6}, Lapp/revanced/integrations/utils/ReVancedUtils;->setNewVideo(Z)V

                        """
                    )
                }
            }
        }

        return PatchResultSuccess()
    }
}
