package app.revanced.patches.all.misc.mitmproxy.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.all.misc.debugging.patch.EnableAndroidDebuggingPatch
import org.w3c.dom.Element
import java.io.File

@Patch(false)
@Name("Mitm proxy")
@Description("Mitmproxy to inspect HTTPS traffic.")
@Version("0.0.1")
@DependsOn([EnableAndroidDebuggingPatch::class])
class MitmProxyPatch : ResourcePatch {

//    steps required to achieve this:
    // 1. check if "networkSecurityConfig" attribute exists in the application node
    // 2. if it does, open the "network_security_config.xml" file and <certificates src="user" /> to the trust-anchors node
    // 3. if it does not, Add android:networkSecurityConfig="@xml/network_security_config" to the <application> element in your application manifest.
    // 4. create the "network_security_config.xml" file in the res/xml directory with the following contents:
    //<?xml version="1.0" encoding="utf-8"?>
    //<network-security-config>
    //    <base-config cleartextTrafficPermitted="true">
    //        <trust-anchors>
    //            <certificates src="system" />
    //            <certificates
    //                src="user"
    //                overridePins="true" />
    //        </trust-anchors>
    //    </base-config>
    //    <debug-overrides>
    //        <trust-anchors>
    //            <certificates src="system" />
    //            <certificates
    //                src="user"
    //                overridePins="true" />
    //        </trust-anchors>
    //    </debug-overrides>
    //</network-security-config>


    override fun execute(context: ResourceContext): PatchResult {
        val resXmlDirectory = context["res/xml"]

        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file
            val applicationNode = document
                .getElementsByTagName("application")
                .item(0) as Element

            // Add `android:networkSecurityConfig` attribute to the application node with the value of "@xml/network_security_config"
            if (!applicationNode.hasAttribute("networkSecurityConfig")) {
                document.createAttribute("android:networkSecurityConfig")
                    .apply { value = "@xml/network_security_config" }
                    .let(applicationNode.attributes::setNamedItem)
            }

        }

        // Check and create the "network_security_config.xml" file in the res/xml directory with the following contents:
        val networkSecurityConfigFile = File(resXmlDirectory, "network_security_config.xml")

        if (!networkSecurityConfigFile.exists()) {
            networkSecurityConfigFile.createNewFile()
            networkSecurityConfigFile.writeText(
                """
                <?xml version="1.0" encoding="utf-8"?>
                    <network-security-config>
                        <base-config cleartextTrafficPermitted="true">
                            <trust-anchors>
                                <certificates src="system" />
                                <certificates
                                    src="user"
                                    overridePins="true" />
                            </trust-anchors>
                        </base-config>
                        <debug-overrides>
                            <trust-anchors>
                                <certificates src="system" />
                                <certificates
                                    src="user"
                                    overridePins="true" />
                            </trust-anchors>
                        </debug-overrides>
                    </network-security-config>
                """.trimIndent()
            )
        } else {
            // If the file already exists, open the "network_security_config.xml" file and <certificates src="system" /> and <certificates src="user" overridePins="true" /> to the trust-anchors node
            networkSecurityConfigFile.readText().let { fileContents ->
                if (!fileContents.contains("<certificates src=\"user\" />")) {
                    networkSecurityConfigFile.writeText(
                        fileContents.replace(
                            "<trust-anchors>",
                            "<trust-anchors>\n<certificates src=\"user\" overridePins=\"true\" />\n<certificates src=\"system\" />"
                        )
                    )
                }
            }
        }

        return PatchResultSuccess()
    }
}
