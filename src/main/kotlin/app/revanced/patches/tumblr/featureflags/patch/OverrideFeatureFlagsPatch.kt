package app.revanced.patches.tumblr.featureflags.patch

import app.revanced.extensions.exception
import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Package
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patches.tumblr.featureflags.fingerprints.GetFeatureValueFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter

@Name("Override feature flags")
@Description("Forcibly set the value of A/B testing features of your choice.")
@Compatibility([Package("com.tumblr")])
class OverrideFeatureFlagsPatch : BytecodePatch(
    listOf(GetFeatureValueFingerprint)
) {
    override fun execute(context: BytecodeContext) = GetFeatureValueFingerprint.result?.let {
        // The method we want to inject into does not have enough registers,
        // so instead of dealing with increasing the register count, we add a
        // helper method "getValueOverride" to the class and pass it the feature object.
        // If the helper returns null, we let the function run normally, otherwise we return the helper result value.
        val helperMethod = ImmutableMethod(
            it.method.definingClass,
            "getValueOverride",
            listOf(ImmutableMethodParameter("Lcom/tumblr/configuration/Feature;", null, "feature")),
            "Ljava/lang/String;",
            AccessFlags.PUBLIC or AccessFlags.FINAL,
            null,
            null,
            MutableMethodImplementation(4)
        ).toMutable().apply {
            // At the start of the helper, we get the String representation of the enum once.
            // At the end of the helper, we return null.
            // Between these two, we will later insert the checks & returns for the overrides
            addInstructions(
                0,
                """
                    # toString() the enum value
                    invoke-virtual {p1}, Lcom/tumblr/configuration/Feature;->toString()Ljava/lang/String;
                    move-result-object v0
                    
                    # !!! If you add more instructions above this line, update helperInsertIndex below!
                    # Here we will insert one compare & return for every registered Feature override
                    # This is done below in the addOverride function
                    
                    # If none of the overrides returned a value, we should return null
                    const/4 v0, 0x0
                    return-object v0
                """
            )
        }.also { helperMethod ->
            it.mutableClass.methods.add(helperMethod)
        }


        // Here we actually insert the hook to call our helper method and return its value if it returns not null
        val getFeatureIndex = it.scanResult.patternScanResult!!.startIndex
        it.mutableMethod.addInstructionsWithLabels(
            getFeatureIndex,
            """
                    # Call the Helper Method with the Feature
                    invoke-virtual {p0, p1}, Lcom/tumblr/configuration/Configuration;->getValueOverride(Lcom/tumblr/configuration/Feature;)Ljava/lang/String;
                    move-result-object v0
                    # If it returned null, skip
                    if-eqz v0, :is_null
                    # If it didnt return null, return that string
                    return-object v0
                    
                    # If our override helper returned null, we let the function continue normally
                    :is_null
                    nop
                """
        )

        val helperInsertIndex = 2
        addOverride = { name, value ->
            // For every added override, we add a few instructions in the middle of the helper method
            // to check if the feature is the one we want and return the override value if it is.
            helperMethod.addInstructionsWithLabels(
                helperInsertIndex,
                """
                    # v0 is still the string name of the currently checked feature from above
                    # Compare the current string with the override string
                    const-string v1, "$name"
                    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
                    move-result v1
                    # If the current string is the one we want to override, we return the override value
                    if-eqz v1, :no_override
                    const-string v1, "$value"
                    return-object v1
                    # Else we just continue...
                    :no_override
                    nop
                """
            )
        }
    } ?: throw GetFeatureValueFingerprint.exception

    companion object {
        /**
         * Override a feature flag with a value.
         *
         * @param name The name of the feature flag to override.
         * @param value The value to override the feature flag with.
         */
        @Suppress("KDocUnresolvedReference")
        internal lateinit var addOverride: (name: String, value: String) -> Unit private set
    }
}