package app.revanced.patches.youtube.misc.video.speed.default.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.util.smali.toInstructions
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.videoid.patch.VideoIdPatch
import app.revanced.annotation.YouTubeCompatibility
import app.revanced.patches.youtube.extended.speed.fingerprints.VideoSpeedSetterFingerprint
import app.revanced.patches.youtube.extended.speed.fingerprints.VideoUserSpeedChangeFingerprint
import app.revanced.patches.youtube.extended.speed.fingerprints.VideoSpeedReferenceFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.immutable.ImmutableMethod
import org.jf.dexlib2.immutable.ImmutableMethodImplementation
import org.jf.dexlib2.immutable.ImmutableMethodParameter
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.iface.reference.MethodReference

@Patch
@DependsOn([IntegrationsPatch::class, VideoIdPatch::class])
@Name("default-video-speed")
@Description("Adds the ability to set default video speed.")
@YouTubeCompatibility
@Version("0.0.1")
class DefaultVideoSpeedPatch : BytecodePatch(
    listOf(
        VideoSpeedSetterFingerprint, VideoUserSpeedChangeFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        val userSpeedMethod = VideoUserSpeedChangeFingerprint.result!!
        val userSpeedMutableMethod = userSpeedMethod.mutableMethod

        val setterMethod = VideoSpeedSetterFingerprint.result!!
        val setterMutableMethod = setterMethod.mutableMethod

        VideoUserSpeedChangeFingerprint.resolve(context, setterMethod.classDef)
        val FirstReference =
            VideoUserSpeedChangeFingerprint.result!!.method.let { method ->
                (method.implementation!!.instructions.elementAt(5) as ReferenceInstruction).reference as FieldReference
            }
        VideoUserSpeedChangeFingerprint.resolve(context, setterMethod.classDef)
        val SecondReference =
            VideoUserSpeedChangeFingerprint.result!!.method.let { method ->
                (method.implementation!!.instructions.elementAt(10) as ReferenceInstruction).reference as FieldReference
            }
        val ThirdReference =
            VideoUserSpeedChangeFingerprint.result!!.method.let { method ->
                (method.implementation!!.instructions.elementAt(11) as ReferenceInstruction).reference as MethodReference
            }

        userSpeedMutableMethod.addInstruction(
            0, "invoke-static {}, Lapp/revanced/integrations/patches/VideoSpeedPatch;->userChangedSpeed()V"
        )

        setterMutableMethod.addInstructions(
            0,
            """
   		        invoke-static {p1, p2}, Lapp/revanced/integrations/patches/VideoSpeedPatch;->getSpeedValue([Ljava/lang/Object;I)F
   		        move-result v0
   		        invoke-direct {p0, v0}, ${setterMethod.classDef.type}->overrideSpeed(F)V
            """,
        )

        val classDef = userSpeedMethod.mutableClass 
        classDef.methods.add(
            ImmutableMethod(
                classDef.type,
                "overrideSpeed",
                listOf(ImmutableMethodParameter("F", null, null)),
                "V",
                AccessFlags.PRIVATE or AccessFlags.PRIVATE,
                null,
                null,
                ImmutableMethodImplementation(
                    3, """
                        const/4 v0, 0x0
                        cmpg-float v0, p1, v0
                        if-gez v0, :cond_0
						return-void
						:cond_0
						iget-object v0, p0, ${setterMethod.classDef.type}->${FirstReference.name}:${FirstReference.type}
						check-cast v0, ${SecondReference.definingClass}
						iget-object v1, v0, ${SecondReference.definingClass}->${SecondReference.name}:${SecondReference.type}
						invoke-virtual {v1, p1}, $ThirdReference
						return-void
                    """.toInstructions(), null, null
                )
            ).toMutable()
        )

		VideoIdPatch.injectCall("Lapp/revanced/integrations/patches/VideoSpeedPatch;->newVideoStarted(Ljava/lang/String;)V")
        return PatchResultSuccess()
    }
}
