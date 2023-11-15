package app.revanced.patches.openinghours.crash

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.openinghours.crash.fingerprints.SetPlaceFingerprint
import com.android.tools.smali.dexlib2.builder.BuilderInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Patch(
    name = "Fix crash",
    compatiblePackages = [CompatiblePackage("de.simon.openinghours", ["1.0"])]
)
object CrashPatch : BytecodePatch(setOf(SetPlaceFingerprint)) {

    override fun execute(context: BytecodeContext) {
        with(SetPlaceFingerprint.result ?: throw SetPlaceFingerprint.exception) {
            val indexedInstructions = mutableMethod.implementation!!.instructions.withIndex().toList()
            val getOpeningHoursIndex = getIndicesOfInvoke(indexedInstructions, "Lde/simon/openinghours/models/Place;", "getOpeningHours")
            val setWeekDayTextIndex = getIndicesOfInvoke(indexedInstructions, "Lde/simon/openinghours/views/custom/PlaceCard;", "setWeekDayText")[0]
            val startCalculateStatusIndex = getIndicesOfInvoke(indexedInstructions, "Lde/simon/openinghours/views/custom/PlaceCard;", "startCalculateStatus")[0]
            val getOpeningHoursIndex1 = getOpeningHoursIndex[0]
            val getOpeningHoursIndex2 = getOpeningHoursIndex[1]
            val continueLabel2 = ExternalLabel("continue_1", mutableMethod.getInstruction(startCalculateStatusIndex + 1))

            mutableMethod.removeInstructions(getOpeningHoursIndex2, startCalculateStatusIndex - getOpeningHoursIndex2 + 1)
            mutableMethod.addInstructionsWithLabels(
                getOpeningHoursIndex2,
                """
                    invoke-virtual {p1}, Lde/simon/openinghours/models/Place;->getOpeningHours()Lde/simon/openinghours/models/OpeningHours;
                    move-result-object p1
                    if-eqz p1, :continue_1
                    invoke-virtual {p1}, Lde/simon/openinghours/models/OpeningHours;->getPeriods()Lio/realm/RealmList;
                    move-result-object p1
                    if-eqz p1, :continue_1
                    check-cast p1, Ljava/util/List;
                    invoke-direct {p0, p1}, Lde/simon/openinghours/views/custom/PlaceCard;->startCalculateStatus(Ljava/util/List;)V
                """,
                continueLabel2,
            )

            val continueLabel1 = ExternalLabel("continue_0", mutableMethod.getInstruction(setWeekDayTextIndex + 1))

            mutableMethod.removeInstructions(getOpeningHoursIndex1, setWeekDayTextIndex - getOpeningHoursIndex1 + 1)
            mutableMethod.addInstructionsWithLabels(
                getOpeningHoursIndex1,
                """
                        invoke-virtual {p1}, Lde/simon/openinghours/models/Place;->getOpeningHours()Lde/simon/openinghours/models/OpeningHours;
                        move-result-object v0
                        if-eqz v0, :continue_0
                        invoke-virtual {v0}, Lde/simon/openinghours/models/OpeningHours;->getWeekdayText()Lio/realm/RealmList;
                        move-result-object v0
                        if-eqz v0, :continue_0
                        check-cast v0, Ljava/util/List;
                        invoke-direct {p0, v0}, Lde/simon/openinghours/views/custom/PlaceCard;->setWeekDayText(Ljava/util/List;)V

                """,
                continueLabel1,
            )
        }
    }

    private fun getIndicesOfInvoke(
        instructions: List<IndexedValue<BuilderInstruction>>,
        className: String,
        methodName: String,
    ): List<Int> = instructions.mapNotNull { (index, instruction) ->
        val invokeInstruction = instruction as? ReferenceInstruction ?: return@mapNotNull null
        val methodRef = invokeInstruction.reference as? MethodReference ?: return@mapNotNull null

        if (methodRef.definingClass != className || methodRef.name != methodName) {
            return@mapNotNull null
        }

        index
    }

}
