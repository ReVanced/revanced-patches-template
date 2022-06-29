package app.revanced.patches.youtube.interaction.videobuffer.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.youtube.interaction.videobuffer.annotations.VideoBufferCompatibility
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch

@Patch
@Dependencies(dependencies = [IntegrationsPatch::class])
@Name("video-buffer")
@Description("Patch to set video buffer limits.")
@VideoBufferCompatibility
@Version("0.0.1")
class VideoBufferPatch : BytecodePatch(listOf()) {
    override fun execute(data: BytecodeData): PatchResult {
        data.classes.forEach { classDef ->
            classDef.methods.forEach { method ->
                if (classDef.type.endsWith("PlayerConfigModel;") && method.name == "t") {
                    val reBufferMethod =
                        data.proxy(classDef).resolve().methods.first { it.name == "t" }

                    reBufferMethod.addInstructions(
                        7, """
                            invoke-static {v0}, Lapp/revanced/integrations/patches/VideoBufferPatch;->getReBuffer(I)I
                            move-result v0
                        """
                    )
                }
                    val playbackBufferMethod =
                        data.proxy(classDef).resolve().methods.first { it.name == "p" }

                    playbackBufferMethod.addInstructions(
                        7,"""
                            invoke-static {v0}, Lapp/revanced/integrations/patches/VideoBufferPatch;->getPlaybackBuffer(I)I
                            move-result v0
                    """
                    )
                }
                    val maxBufferMethod =
                        data.proxy(classDef).resolve().methods.first { it.name == "r" }
                    maxBufferMethod.addInstructions(
                        10,"""
                            invoke-static {v0}, Lapp/revanced/integrations/patches/VideoBufferPatch;->getMaxBuffer(I)I
                            move-result v0
                        """
                    )
                }
            }
        }

    return PatchResultSuccess()
    }
}
