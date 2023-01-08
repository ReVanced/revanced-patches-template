package app.revanced.patches.all.connectivity.wifi.spoof.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.util.patch.AbstractTransformInstructionsPatch
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.instruction.Instruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.reference.MethodReference
import java.util.*

private typealias InstructionInfo = Triple<SpoofWifiPatch.MethodCall, Instruction35c, Int>

@Patch(false)
@Name("spoof-wifi-connection")
@Description("Spoofs an existing Wi-Fi connection.")
@Version("0.0.1")
class SpoofWifiPatch : AbstractTransformInstructionsPatch<InstructionInfo>() {

    private companion object {
        const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/all/connectivity/wifi/spoof/SpoofWifiPatch;"
    }

    // Information about method calls we want to replace
    enum class MethodCall(
        val definedClassName: String,
        val methodName: String,
        val methodParams: Array<String>,
        val returnType: String,
    ) {
        GetSystemService1(
            "Landroid/content/Context;",
            "getSystemService",
            arrayOf("Ljava/lang/String;"),
            "Ljava/lang/Object;",
        ),
        GetSystemService2(
            "Landroid/content/Context;",
            "getSystemService",
            arrayOf("Ljava/lang/Class;"),
            "Ljava/lang/Object;",
        ),
        GetActiveNetworkInfo(
            "Landroid/net/ConnectivityManager;",
            "getActiveNetworkInfo",
            arrayOf(),
            "Landroid/net/NetworkInfo;",
        ),
        IsConnected(
            "Landroid/net/NetworkInfo;",
            "isConnected",
            arrayOf(),
            "Z",
        ),
        IsConnectedOrConnecting(
            "Landroid/net/NetworkInfo;",
            "isConnectedOrConnecting",
            arrayOf(),
            "Z",
        ),
        IsAvailable(
            "Landroid/net/NetworkInfo;",
            "isAvailable",
            arrayOf(),
            "Z",
        ),
        GetState(
            "Landroid/net/NetworkInfo;",
            "getState",
            arrayOf(),
            "Landroid/net/NetworkInfo\$State;",
        ),
        GetDetailedState(
            "Landroid/net/NetworkInfo;",
            "getDetailedState",
            arrayOf(),
            "Landroid/net/NetworkInfo\$DetailedState;",
        ),
        IsActiveNetworkMetered(
            "Landroid/net/ConnectivityManager;",
            "isActiveNetworkMetered",
            arrayOf(),
            "Z",
        ),
        GetActiveNetwork(
            "Landroid/net/ConnectivityManager;",
            "getActiveNetwork",
            arrayOf(),
            "Landroid/net/Network;",
        ),
        GetNetworkInfo(
            "Landroid/net/ConnectivityManager;",
            "getNetworkInfo",
            arrayOf("Landroid/net/Network;"),
            "Landroid/net/NetworkInfo;",
        ),
        HasTransport(
            "Landroid/net/NetworkCapabilities;",
            "hasTransport",
            arrayOf("I"),
            "Z",
        ),
        HasCapability(
            "Landroid/net/NetworkCapabilities;",
            "hasCapability",
            arrayOf("I"),
            "Z",
        ),
        RegisterBestMatchingNetworkCallback(
            "Landroid/net/ConnectivityManager;",
            "registerBestMatchingNetworkCallback",
            arrayOf("Landroid/net/NetworkRequest;", "Landroid/net/ConnectivityManager\$NetworkCallback;", "Landroid/os/Handler;"),
            "V",
        ),
        RegisterDefaultNetworkCallback1(
            "Landroid/net/ConnectivityManager;",
            "registerDefaultNetworkCallback",
            arrayOf("Landroid/net/ConnectivityManager\$NetworkCallback;"),
            "V",
        ),
        RegisterDefaultNetworkCallback2(
            "Landroid/net/ConnectivityManager;",
            "registerDefaultNetworkCallback",
            arrayOf("Landroid/net/ConnectivityManager\$NetworkCallback;", "Landroid/os/Handler;"),
            "V",
        ),
        RegisterNetworkCallback1(
            "Landroid/net/ConnectivityManager;",
            "registerNetworkCallback",
            arrayOf("Landroid/net/NetworkRequest;", "Landroid/net/ConnectivityManager\$NetworkCallback;"),
            "V",
        ),
        RegisterNetworkCallback2(
            "Landroid/net/ConnectivityManager;",
            "registerNetworkCallback",
            arrayOf("Landroid/net/NetworkRequest;", "Landroid/app/PendingIntent;"),
            "V",
        ),
        RegisterNetworkCallback3(
            "Landroid/net/ConnectivityManager;",
            "registerNetworkCallback",
            arrayOf("Landroid/net/NetworkRequest;", "Landroid/net/ConnectivityManager\$NetworkCallback;", "Landroid/os/Handler;"),
            "V",
        ),
        RequestNetwork1(
            "Landroid/net/ConnectivityManager;",
            "requestNetwork",
            arrayOf("Landroid/net/NetworkRequest;", "Landroid/net/ConnectivityManager\$NetworkCallback;"),
            "V",
        ),
        RequestNetwork2(
            "Landroid/net/ConnectivityManager;",
            "requestNetwork",
            arrayOf("Landroid/net/NetworkRequest;", "Landroid/net/ConnectivityManager\$NetworkCallback;", "I"),
            "V",
        ),
        RequestNetwork3(
            "Landroid/net/ConnectivityManager;",
            "requestNetwork",
            arrayOf("Landroid/net/NetworkRequest;", "Landroid/net/ConnectivityManager\$NetworkCallback;", "Landroid/os/Handler;"),
            "V",
        ),
        RequestNetwork4(
            "Landroid/net/ConnectivityManager;",
            "requestNetwork",
            arrayOf("Landroid/net/NetworkRequest;", "Landroid/app/PendingIntent;"),
            "V",
        ),
        RequestNetwork5(
            "Landroid/net/ConnectivityManager;",
            "requestNetwork",
            arrayOf("Landroid/net/NetworkRequest;", "Landroid/net/ConnectivityManager\$NetworkCallback;", "Landroid/os/Handler;", "I"),
            "V",
        );

        fun replaceInstruction(method: MutableMethod, instruction: Instruction35c, instructionIndex: Int) {
            val registers = arrayOf(instruction.registerC, instruction.registerD, instruction.registerE, instruction.registerF, instruction.registerG)
            val argsNum = this.methodParams.size + 1 // + 1 for instance of definedClassName
            if (argsNum > registers.size) {
                // should never happen, but just to be sure (also for the future) a safety check
                throw RuntimeException("not enough registers for ${this.definedClassName}#${this.methodName}: required $argsNum registers, but only got ${registers.size}")
            }
            val args = registers.take(argsNum).joinToString(separator = ", ") { reg -> "v${reg}" }
            val replacementMethodDefinition = "${this.methodName}(${this.definedClassName}${this.methodParams.joinToString(separator = "")})${this.returnType}"
            method.replaceInstruction(
                instructionIndex,
                "invoke-static { $args }, $INTEGRATIONS_CLASS_DESCRIPTOR->${replacementMethodDefinition}"
            )
        }

        companion object {
            fun fromMethodReference(methodReference: MethodReference) = values().firstOrNull { search ->
                search.definedClassName == methodReference.definingClass
                        && search.methodName == methodReference.name
                        && methodReference.parameterTypes.toTypedArray().contentEquals(search.methodParams)
            }
        }
    }

    override fun filterMap(
        classDef: ClassDef,
        method: Method,
        instruction: Instruction,
        instructionIndex: Int
    ): InstructionInfo? {
        if (classDef.type.startsWith(INTEGRATIONS_CLASS_DESCRIPTOR.removeSuffix(";"))) {
            // avoid infinite recursion
            return null
        }

        if (instruction.opcode != Opcode.INVOKE_VIRTUAL) {
            return null
        }

        val invokeInstruction = instruction as Instruction35c
        val methodRef = invokeInstruction.reference as MethodReference
        val methodCall = MethodCall.fromMethodReference(methodRef) ?: return null

        return InstructionInfo(methodCall, invokeInstruction, instructionIndex)
    }

    override fun transform(mutableMethod: MutableMethod, entry: InstructionInfo) {
        val (methodType, instruction, instructionIndex) = entry
        methodType.replaceInstruction(mutableMethod, instruction, instructionIndex)
    }
}
