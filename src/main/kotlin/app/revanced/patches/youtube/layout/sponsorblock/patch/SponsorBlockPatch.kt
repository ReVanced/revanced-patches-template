package app.revanced.patches.youtube.layout.sponsorblock.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.data.impl.toMethodWalker
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.fingerprint.method.utils.MethodFingerprintUtils.resolve
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Dependencies
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.layout.sponsorblock.annotations.SponsorBlockCompatibility
import app.revanced.patches.youtube.layout.sponsorblock.fingerprints.*
import app.revanced.patches.youtube.layout.sponsorblock.utils.InstructionUtils.findInstructionsByName
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.mapping.patch.ResourceIdMappingProviderResourcePatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction11x
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.reference.MethodReference
import org.jf.dexlib2.util.MethodUtil

@Patch
@Dependencies(dependencies = [IntegrationsPatch::class, ResourceIdMappingProviderResourcePatch::class])
@Name("sponsorblock")
@Description("Integrate SponsorBlock.")
@SponsorBlockCompatibility
@Version("0.0.1")
class SponsorBlockPatch : BytecodePatch(
    listOf(
        PlayerControllerSetTimeReferenceFingerprint,
        CreateVideoPlayerSeekbarFingerprint,
        VideoIdFingerprint,
        VideoTimeFingerprint,
        NextGenWatchLayoutFingerprint,
        AppendTimeFingerprint,
        PlayerInitFingerprint,
        WatchWhileActiveFingerprint,
        PlayerOverlaysLayoutInitFingerprint
    )
) {
    // TODO fi/razerman/ and fi/vanced/
    override fun execute(data: BytecodeData): PatchResult {/*
        Set current video time
        */
        val referenceResult = PlayerControllerSetTimeReferenceFingerprint.result!!
        val playerControllerSetTimeMethod =
            data.toMethodWalker(referenceResult.method).nextMethod(referenceResult.patternScanResult!!.startIndex, true)
                .getMethod() as MutableMethod
        playerControllerSetTimeMethod.addInstruction(
            2, "invoke-static {p1, p2}, Lpl/jakubweg/PlayerController;->setCurrentVideoTime(J)V"
        )

        /*
        Set current video time high precision
         */
        val constructorFingerprint =
            object : MethodFingerprint("V", null, listOf("J", "J", "J", "J", "I", "L"), null) {}
        constructorFingerprint.resolve(data, VideoTimeFingerprint.result!!.classDef)

        val constructor = constructorFingerprint.result!!.mutableMethod
        constructor.addInstruction(
            0, "invoke-static {p1, p2}, Lpl/jakubweg/PlayerController;->setCurrentVideoTimeHighPrecision(J)V"
        )

        /*
         Set current video id
         */
        VideoIdFingerprint.resolve(data, VideoIdFingerprint.result!!.classDef)
        val videoIdMethodResult = VideoIdFingerprint.result!!

        val videoIdMethod = videoIdMethodResult.mutableMethod
        val videoIdRegister =
            (videoIdMethod.implementation!!.instructions[videoIdMethodResult.patternScanResult!!.endIndex + 1] as Instruction11x).registerA

        videoIdMethod.addInstructions(
            videoIdMethodResult.patternScanResult!!.endIndex + 2, // after the move result
            """
                 invoke-static {v$videoIdRegister}, Lfi/vanced/libraries/youtube/player/VideoInformation;->setCurrentVideoId(Ljava/lang/String;)V
                 invoke-static {v$videoIdRegister}, Lpl/jakubweg/PlayerController;->setCurrentVideoId(Ljava/lang/String;)V
                 invoke-static {}, Lfi/razerman/youtube/XGlobals;->NewVideoStarted()V
            """
        )

        /*
         Seekbar drawing
         */
        val seekbarSignatureResult = CreateVideoPlayerSeekbarFingerprint.result!!
        val seekbarMethod = seekbarSignatureResult.mutableMethod
        val seekbarMethodInstructions = seekbarMethod.implementation!!.instructions

        /*
         Get the instance of the seekbar rectangle
         */
        seekbarMethod.addInstruction(
            1, "invoke-static {v0}, Lpl/jakubweg/PlayerController;->setSponsorBarRect(Ljava/lang/Object;)V"
        )

        for ((index, instruction) in seekbarMethodInstructions.withIndex()) {
            if (instruction.opcode != Opcode.INVOKE_STATIC) continue

            val invokeInstruction = instruction as Instruction35c
            if ((invokeInstruction.reference as MethodReference).name != "round") continue

            val insertIndex = index + 3

            // set the thickness of the segment
            seekbarMethod.addInstruction(
                insertIndex,
                "invoke-static {v${invokeInstruction.registerC}}, Lpl/jakubweg/PlayerController;->setSponsorBarThickness(I)V"
            )
            break
        }

        /*
        Set rectangle absolute left and right positions
        */
        val drawRectangleInstructions =
            seekbarMethodInstructions.findInstructionsByName("drawRect").map { // TODO: improve code
                seekbarMethodInstructions.indexOf(it) to (it as Instruction35c).registerD
            }

        val (indexRight, rectangleRightRegister) = drawRectangleInstructions[0]
        val (indexLeft, rectangleLeftRegister) = drawRectangleInstructions[2]

        // order of operation is important here due to the code above which has to be improved
        // the reason for that is that we get the index, add instructions and then the offset would be wrong
        seekbarMethod.addInstruction(
            indexLeft + 1,
            "invoke-static {v$rectangleLeftRegister}, Lpl/jakubweg/PlayerController;->setSponsorBarAbsoluteRight(Landroid/graphics/Rect;)V"
        )
        seekbarMethod.addInstruction(
            indexRight + 1,
            "invoke-static {v$rectangleRightRegister}, Lpl/jakubweg/PlayerController;->setSponsorBarAbsoluteRight(Landroid/graphics/Rect;)V"
        )

        /*
        Draw segment
        */
        val drawSegmentInstructionInsertIndex = (seekbarMethodInstructions.size - 1 - 2)
        val (canvasInstance, centerY) = (seekbarMethodInstructions[drawSegmentInstructionInsertIndex] as FiveRegisterInstruction).let {
            it.registerC to it.registerE
        }
        seekbarMethod.addInstruction(
            drawSegmentInstructionInsertIndex - 1,
            "invoke-static {v$canvasInstance, v$centerY}, Lpl/jakubweg/PlayerController;->drawSponsorTimeBars(Landroid/graphics/Canvas;F)V"
        )

        /*
        Set video length
         */
        VideoLengthFingerprint.resolve(data, seekbarSignatureResult.classDef)
        val videoLengthMethodResult = VideoLengthFingerprint.result!!
        val videoLengthMethod = videoLengthMethodResult.mutableMethod
        val videoLengthMethodInstructions = videoLengthMethod.implementation!!.instructions

        val videoLengthRegister =
            (videoLengthMethodInstructions[videoLengthMethodResult.patternScanResult!!.endIndex - 2] as OneRegisterInstruction).registerA
        videoLengthMethod.addInstruction(
            videoLengthMethodResult.patternScanResult!!.endIndex,
            "invoke-static {v$videoLengthRegister}, Lpl/jakubweg/PlayerController;->setVideoLength(J)V"
        )

        /*
        Voting & Shield button
         */
        ShowPlayerControlsFingerprint.resolve(data, data.classes.find { it.type.endsWith("YouTubeControlsOverlay;") }!!)
        val controlsMethodResult = ShowPlayerControlsFingerprint.result!!

        val controlsLayoutStubResourceId =
            ResourceIdMappingProviderResourcePatch.resourceMappings["controls_layout_stub"]!!
        val zoomOverlayResourceId = ResourceIdMappingProviderResourcePatch.resourceMappings["video_zoom_overlay_stub"]!!

        methods@ for (method in controlsMethodResult.mutableClass.methods) {
            val instructions = method.implementation?.instructions!!
            instructions@ for ((index, instruction) in instructions.withIndex()) {
                // search for method which inflates the controls layout view
                if (instruction.opcode != Opcode.CONST) continue@instructions

                when ((instruction as NarrowLiteralInstruction).wideLiteral) {
                    controlsLayoutStubResourceId -> {
                        // replace the view with the YouTubeControlsOverlay
                        val moveResultInstructionIndex = index + 5
                        val inflatedViewRegister =
                            (instructions[moveResultInstructionIndex] as OneRegisterInstruction).registerA
                        // initialize with the player overlay object
                        method.addInstructions(
                            moveResultInstructionIndex + 1, // insert right after moving the view to the register and use that register
                            """
                                invoke-static {v$inflatedViewRegister}, Lpl/jakubweg/ShieldButton;->initialize(Ljava/lang/Object;)V
                                invoke-static {v$inflatedViewRegister}, Lpl/jakubweg/VotingButton;->initialize(Ljava/lang/Object;)V
                            """
                        )
                    }

                    zoomOverlayResourceId -> {
                        val invertVisibilityMethod =
                            data.toMethodWalker(method).nextMethod(index - 6, true).getMethod() as MutableMethod
                        // change visibility of the buttons
                        invertVisibilityMethod.addInstructions(
                            0, """
                                invoke-static {p1}, Lpl/jakubweg/ShieldButton;->changeVisibilityNegatedImmediate(Z)V
                                invoke-static {p1}, Lpl/jakubweg/VotingButton;->changeVisibilityNegatedImmediate(Z)V
                            """.trimIndent()
                        )
                    }
                }
            }
        }

        // change visibility of the buttons
        controlsMethodResult.mutableMethod.addInstructions(
            0, """
                invoke-static {p1}, Lpl/jakubweg/ShieldButton;->changeVisibility(Z)V
                invoke-static {p1}, Lpl/jakubweg/VotingButton;->changeVisibility(Z)V
            """.trimIndent()
        )

        // set SegmentHelperLayout.context to the player layout instance
        val instanceRegister = 0
        NextGenWatchLayoutFingerprint.result!!.mutableMethod.addInstruction(
            1,
            "invoke-static {p$instanceRegister}, Lpl/jakubweg/PlayerController;->addSkipSponsorView15(Landroid/view/View;)V"
        )

        // append the new time to the player layout
        val appendTimeFingerprintResult = AppendTimeFingerprint.result!!
        val appendTimePatternScanStartIndex = appendTimeFingerprintResult.patternScanResult!!.startIndex
        val targetRegister = (appendTimeFingerprintResult
            .method
            .implementation!!
            .instructions
            .elementAt(appendTimePatternScanStartIndex + 1) as OneRegisterInstruction)
            .registerA

        appendTimeFingerprintResult.mutableMethod.addInstructions(
            appendTimePatternScanStartIndex + 2,
            """
                    invoke-static {p$targetRegister}, Lpl/jakubweg/SponsorBlockUtils;->appendTimeWithoutSegments(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object p$targetRegister
            """
        )

        // initialize the player controller
        val initInstanceRegister = 0
        PlayerInitFingerprint.result!!.mutableClass.methods.first { MethodUtil.isConstructor(it) }.addInstruction(
            4, // after super class invoke
            "invoke-static {v$initInstanceRegister}, Lpl/jakubweg/PlayerController;->onCreate(Ljava/lang/Object;)V"
        )

        // show a dialog for sponsor block
        val watchWhileActiveResult = WatchWhileActiveFingerprint.result!!
        val watchWhileActiveInstance = 1
        watchWhileActiveResult.mutableMethod.addInstruction(
            watchWhileActiveResult.patternScanResult!!.startIndex,
            "invoke-static {v$watchWhileActiveInstance}, Lfi/vanced/libraries/youtube/dialog/Dialogs;->showDialogsAtStartup(Landroid/app/Activity;)V" // TODO: separate ryd and sb dialogs
        )

        // initialize the sponsorblock view
        PlayerOverlaysLayoutInitFingerprint.result!!.mutableMethod.addInstruction(
            6, // after inflating the view
            "invoke-static {p0}, Lfi/vanced/libraries/youtube/sponsors/player/ui/SponsorBlockView;->initialize(Ljava/lang/Object;)V"
        )

        // TODO: isSBChannelWhitelisting implementation


        TODO("Until here some classes have been completely implemented, but not tested yet, continue to implement other classes and test all of them")
        return PatchResultSuccess()
    }
}
