package app.revanced.patches.youtube.misc.video.information.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.instruction
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.video.information.annotation.VideoInformationCompatibility
import app.revanced.patches.youtube.misc.video.information.fingerprints.*
import app.revanced.patches.youtube.misc.video.videoid.patch.VideoIdPatch
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.builder.MutableMethodImplementation
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.immutable.ImmutableMethod
import org.jf.dexlib2.immutable.ImmutableMethodParameter
import org.jf.dexlib2.util.MethodUtil

@Name("video-information")
@Description("Hooks YouTube to get information about the current playing video.")
@VideoInformationCompatibility
@Version("0.0.1")
@DependsOn([IntegrationsPatch::class, VideoIdPatch::class])
class VideoInformationPatch : BytecodePatch(
    listOf(
        PlayerInitFingerprint,
        CreateVideoPlayerSeekbarFingerprint,
        PlayerControllerSetTimeReferenceFingerprint,
        VideoTimeFingerprint,
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        with(PlayerInitFingerprint.result!!) {
            playerInitMethod = mutableClass.methods.first { MethodUtil.isConstructor(it) }

            // hook the player controller for use through integrations
            onCreateHook(INTEGRATIONS_CLASS_DESCRIPTOR, "playerController_onCreateHook")

            // seek method
            val seekFingerprintResultMethod = SeekFingerprint.also { it.resolve(context, classDef) }.result!!.method

            // create helper method
            val seekHelperMethod = ImmutableMethod(
                seekFingerprintResultMethod.definingClass,
                "seekTo",
                listOf(ImmutableMethodParameter("J", null, "time")),
                "Z",
                AccessFlags.PUBLIC or AccessFlags.FINAL,
                null, null,
                MutableMethodImplementation(4)
            ).toMutable()

            // get enum type for the seek helper method
            val seekSourceEnumType = seekFingerprintResultMethod.parameterTypes[1].toString()

            // insert helper method instructions
            seekHelperMethod.addInstructions(
                0,
                """
                sget-object v0, $seekSourceEnumType->a:$seekSourceEnumType
                invoke-virtual {p0, p1, p2, v0}, ${seekFingerprintResultMethod.definingClass}->${seekFingerprintResultMethod.name}(J$seekSourceEnumType)Z
                move-result p1
                return p1
            """
            )

            // add the seekTo method to the class for the integrations to call
            mutableClass.methods.add(seekHelperMethod)
        }

        with(CreateVideoPlayerSeekbarFingerprint.result!!) {
            val videoLengthMethodResult = VideoLengthFingerprint.also { it.resolve(context, classDef) }.result!!

            with(videoLengthMethodResult.mutableMethod) {
                val videoLengthRegisterIndex = videoLengthMethodResult.scanResult.patternScanResult!!.endIndex - 2
                val videoLengthRegister = (instruction(videoLengthRegisterIndex) as OneRegisterInstruction).registerA
                val dummyRegisterForLong = videoLengthRegister + 1 // required for long values since they are wide

                addInstruction(
                    videoLengthMethodResult.scanResult.patternScanResult!!.endIndex,
                    "invoke-static {v$videoLengthRegister, v$dummyRegisterForLong}, $INTEGRATIONS_CLASS_DESCRIPTOR->setVideoLength(J)V"
                )
            }
        }

        /*
        Inject call for video id
         */
        VideoIdPatch.injectCall("$INTEGRATIONS_CLASS_DESCRIPTOR->setVideoId(Ljava/lang/String;)V")

        /*
        Set the video time method
        */
        with(PlayerControllerSetTimeReferenceFingerprint.result!!) {
            timeMethod = context.toMethodWalker(method)
                .nextMethod(scanResult.patternScanResult!!.startIndex, true)
                .getMethod() as MutableMethod
        }

        /*
        Set the high precision video time method
         */
        highPrecisionTimeMethod =
            (object : MethodFingerprint("V", null, listOf("J", "J", "J", "J", "I", "L"), null) {}).also {
                it.resolve(context, VideoTimeFingerprint.result!!.classDef)
            }.result!!.mutableMethod

        /*
        Hook the methods which set the time
         */
        highPrecisionTimeHook(INTEGRATIONS_CLASS_DESCRIPTOR, "setVideoTime")

        return PatchResultSuccess()
    }

    companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/VideoInformation;"

        private lateinit var playerInitMethod: MutableMethod
        private lateinit var timeMethod: MutableMethod
        private lateinit var highPrecisionTimeMethod: MutableMethod

        private fun MutableMethod.insert(insert: InsertIndex, register: String, descriptor: String) =
            addInstruction(insert.index, "invoke-static { $register }, $descriptor")

        private fun MutableMethod.insertTimeHook(insert: InsertIndex, descriptor: String) =
            insert(insert, "p1, p2", descriptor)

        /**
         * Hook the player controller.
         *
         * @param targetMethodClass The descriptor for the class to invoke when the player controller is created.
         * @param targetMethodName The name of the static method to invoke when the player controller is created.
         */
        internal fun onCreateHook(targetMethodClass: String, targetMethodName: String) =
            playerInitMethod.insert(
                InsertIndex.CREATE,
                "v0",
                "$targetMethodClass->$targetMethodName(Ljava/lang/Object;)V"
            )

        /**
         * Hook the video time.
         *
         * @param targetMethodClass The descriptor for the static method to invoke when the player controller is created.
         * @param targetMethodName The name of the static method to invoke when the player controller is created.
         */
        internal fun videoTimeHook(targetMethodClass: String, targetMethodName: String) =
            timeMethod.insertTimeHook(
                InsertIndex.TIME,
                "$targetMethodClass->$targetMethodName(J)V"
            )

        /**
         * Hook the high precision video time.
         *
         * @param targetMethodClass The descriptor for the static method to invoke when the player controller is created.
         * @param targetMethodName The name of the static method to invoke when the player controller is created.
         */
        internal fun highPrecisionTimeHook(targetMethodClass: String, targetMethodName: String) =
            highPrecisionTimeMethod.insertTimeHook(
                InsertIndex.HIGH_PRECISION_TIME,
                "$targetMethodClass->$targetMethodName(J)V"
            )

        enum class InsertIndex(internal val index: Int) {
            CREATE(4),
            TIME(2),
            HIGH_PRECISION_TIME(0),
        }
    }
}