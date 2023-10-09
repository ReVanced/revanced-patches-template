package app.revanced.patches.unifiprotect.localdevice.directconnections

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.types.StringPatchOption.Companion.stringPatchOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patches.unifiprotect.localdevice.directconnections.fingerprints.DirectConnectionsMethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter

@Patch(
    name = "Enable direct connections",
    compatiblePackages = [CompatiblePackage("com.ubnt.unifi.protect")],
)
@Suppress("unused")
object DirectConnectionsPatch : BytecodePatch(
    setOf(
        DirectConnectionsMethodFingerprint
    )
) {
    private var model by stringPatchOption(
        "Model",
        "UCK-G2-PLUS",
        "The model to use for the direct connection device"
    )

    private var IP by stringPatchOption(
        "IP",
        "",
        "The IP to use for the direct connection device"
    )

    private var MAC by stringPatchOption(
        "MAC",
        null,
        "The MAC to use for the direct connection device"
    )

    override fun execute(context: BytecodeContext) {

        val result = DirectConnectionsMethodFingerprint.result ?: throw PatchException("Could not find method to patch")
        result.mutableClass.methods.removeIf{ it.name == "registerListener" }

        val helperMethod = ImmutableMethod(
            result.method.definingClass,
            "registerListener",
            listOf(
                ImmutableMethodParameter(
                    "Lcom/ubnt/common/service/discovery/DeviceDiscoveryService\$DeviceDiscoveryServiceListener;",
                    null,
                    null,
                )
            ),
            "V",
            AccessFlags.PUBLIC or AccessFlags.FINAL,
            null,
            null,
            MutableMethodImplementation(7)
        ).toMutable().apply {
            addInstructionsWithLabels(
                0,
                """                                          
                                const-string v0, "listener"
                            
                                invoke-static {p1, v0}, Lkotlin/jvm/internal/Intrinsics;->checkNotNullParameter(Ljava/lang/Object;Ljava/lang/String;)V
                            
                                .line 128
                                iget-object v0, p0, Lcom/ubnt/common/service/discovery/DeviceDiscoveryService;->discoveredDevicesChangedListeners:Ljava/util/concurrent/CopyOnWriteArrayList;
                            
                                monitor-enter v0
                            
                                .line 129
                                :try_start_8
                                iget-object v1, p0, Lcom/ubnt/common/service/discovery/DeviceDiscoveryService;->discoveredDevicesChangedListeners:Ljava/util/concurrent/CopyOnWriteArrayList;
                            
                                invoke-interface {v1, p1}, Ljava/util/List;->add(Ljava/lang/Object;)Z
                                :try_end_d
                                .catchall {:try_start_8 .. :try_end_d} :catchall_4b
                            
                                .line 128
                                monitor-exit v0
                            
                                new-instance v4, Lcom/ubnt/common/service/discovery/Version1Packet;
                            
                                invoke-direct {v4}, Lcom/ubnt/common/service/discovery/Version1Packet;-><init>()V
                            
                                const-string v1, "UniFi Direct Connect"
                            
                                iput-object v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->hostname:Ljava/lang/String;
                            
                                const-string v1, "$model"
                            
                                iput-object v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->platform:Ljava/lang/String;
                            
                                const-string v1, ""
                            
                                iput-object v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->essid:Ljava/lang/String;
                            
                                const/4 v1, 0x1
                            
                                iput v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->wmode:I
                            
                                sget-object v1, Lcom/ubnt/common/service/discovery/Version1Packet${'$'}Protocol;->http:Lcom/ubnt/common/service/discovery/Version1Packet${'$'}Protocol;
                                
                            
                                iput-object v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->webProto:Lcom/ubnt/common/service/discovery/Version1Packet${'$'}Protocol;
                            
                                const/16 v1, 0x1ba8
                            
                                iput v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->webPort:I
                            
                                const-string v1, "unifi-protect.arm64.v99.99.0.0.0.0"
                            
                                iput-object v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->version:Ljava/lang/String;
                            
                                new-instance v1, Lcom/ubnt/common/service/discovery/Version1Packet${'$'}IpInfo;
                            
                                const-string v2, "$MAC"
                            
                                const-string v3, "$IP"
                            
                                invoke-direct {v1, v2, v3}, Lcom/ubnt/common/service/discovery/Version1Packet${'$'}IpInfo;-><init>(Ljava/lang/String;Ljava/lang/String;)V
                            
                                iput-object v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->ipInfo:Lcom/ubnt/common/service/discovery/Version1Packet${'$'}IpInfo;
                            
                                const-string v1, "$IP"
                            
                                iput-object v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->ipaddr:Ljava/lang/String;
                            
                                const-string v1, "$MAC"
                            
                                iput-object v1, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->hwaddr:Ljava/lang/String;
                            
                                invoke-static {}, Ljava/lang/System;->currentTimeMillis()J
                            
                                move-result-wide v2
                            
                                iput-wide v2, v4, Lcom/ubnt/common/service/discovery/Version1Packet;->lastSeen:J
                            
                                invoke-direct {p0, v4}, Lcom/ubnt/common/service/discovery/DeviceDiscoveryService;->onDeviceUpdated(Lcom/ubnt/common/service/discovery/Version1Packet;)V
                            
                                return-void
                            
                                :catchall_4b
                                move-exception p1
                            
                                monitor-exit v0
                            
                                throw p1
                """
            )
        }
        result.mutableClass.methods.add(helperMethod)
    }
}