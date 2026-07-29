package app.revanced.patches.dcinside.post.view

import app.revanced.com.android.tools.smali.dexlib2.mutable.MutableField.Companion.toMutable
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.fieldReference
import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.extensions.methodReference
import app.revanced.patcher.extensions.reference
import app.revanced.patcher.firstClassDef
import app.revanced.patcher.patch.ResourcePatchContext
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.patch.resourcePatch
import app.revanced.patches.dcinside.misc.extension.sharedExtensionPatch
import app.revanced.util.doRecursively
import app.revanced.util.findFreeRegister
import app.revanced.util.getFreeRegisterProvider
import app.revanced.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.value.StringEncodedValue
import org.w3c.dom.Element

private const val POST_SHOW_USER_ID_EXTENSION_CLASS_DESCRIPTOR = "Lapp/revanced/extension/dcinside/patches/PostShowUserIdPatch;"
private const val POST_ITEM_CLASS_DESCRIPTOR = "Lcom/dcinside/app/model/PostInfo;"

context(context: ResourcePatchContext)
private fun injectUserIdTextView(
    layoutPath: String,
    leftAnchorId: String,
    rightAnchorId: String,
    verticalAnchorId: String,
    viewClass: String,
    textColorAttr: String,
    height: String,
    extraAttributes: Map<String, String> = emptyMap(),
) {
    context.document(layoutPath).use { document ->
        val root = document.documentElement ?: return@use

        var leftElement: Element? = null
        var rightElement: Element? = null

        root.doRecursively { node ->
            if (node is Element) {
                when (node.getAttribute("android:id")) {
                    "@+id/$leftAnchorId", "@id/$leftAnchorId" -> leftElement = node
                    "@+id/$rightAnchorId", "@id/$rightAnchorId" -> rightElement = node
                }
            }
        }

        val targetLeft = leftElement ?: return@use
        val targetRight = rightElement ?: return@use

        /* [Anchor Re-linking] 좌우 앵커 사이 제약 조건을 신규 주입할 View ID로 재연결 */
        targetLeft.setAttribute("app:layout_constraintEnd_toStartOf", "@+id/custom_user_id")
        targetRight.setAttribute("app:layout_constraintStart_toEndOf", "@+id/custom_user_id")

        /* [DOM Injection] user_id 표시용 TextView 생성 및 Constraint/Style 속성 할당 */
        val userIdElement = document.createElement(viewClass).apply {
            setAttribute("android:textAppearance", "?attr/textTypeSub")
            setAttribute("android:textColor", textColorAttr)
            setAttribute("android:id", "@+id/custom_user_id")
            setAttribute("android:layout_width", "wrap_content")
            setAttribute("android:layout_height", height)
            setAttribute("android:singleLine", "true")
            setAttribute("android:includeFontPadding", "false")
            setAttribute("android:visibility", "gone")

            setAttribute("app:layout_constraintBottom_toBottomOf", "@+id/$verticalAnchorId")
            setAttribute("app:layout_constraintStart_toEndOf", "@+id/$leftAnchorId")
            setAttribute("app:layout_constraintEnd_toStartOf", "@+id/$rightAnchorId")

            extraAttributes.forEach { (key, value) -> setAttribute(key, value) }
        }

        /* [DOM Insertion] 좌측 앵커 노드와 우측 앵커 노드 사이에 삽입 */
        targetLeft.parentNode?.insertBefore(userIdElement, targetRight)
    }
}

private val postHeaderShowUserIdResourcePatch = resourcePatch {
    compatibleWith("com.dcinside.app.android")

    apply {
        injectUserIdTextView(
            layoutPath = "res/layout/view_read_header.xml",
            leftAnchorId = "read_header_member_ic",
            rightAnchorId = "read_header_gallog",
            verticalAnchorId = "read_header_name",
            viewClass = "android.widget.TextView",
            textColorAttr = "?attr/colorPostExt",
            height = "wrap_content",
            extraAttributes = mapOf(
                "android:layout_marginStart" to "1dp",
                "app:layout_constraintBaseline_toBaselineOf" to "@+id/read_header_name",
            ),
        )
    }
}

private val replyLayoutHeights = mapOf(
    "res/layout/view_reply_item_text.xml" to "26dp",
    "res/layout/view_reply_item_image.xml" to "31dp",
    "res/layout/view_reply_item_image_big.xml" to "31dp",
    "res/layout/view_reply_item_voice.xml" to "26dp",
    "res/layout/view_reply_item_voice2.xml" to "26dp",
)

private val replyShowUserIdResourcePatch = resourcePatch {
    compatibleWith("com.dcinside.app.android")

    apply {
        replyLayoutHeights.forEach { (layoutPath, height) ->
            injectUserIdTextView(
                layoutPath = layoutPath,
                leftAnchorId = "reply_member_ic",
                rightAnchorId = "reply_user_memo",
                verticalAnchorId = "reply_name",
                viewClass = "com.dcinside.app.view.ResizeTextView",
                textColorAttr = "?attr/dcPostReadSubColor",
                height = height,
                extraAttributes = mapOf(
                    "android:gravity" to "center_vertical",
                    "app:layout_constraintTop_toTopOf" to "@+id/reply_name",
                ),
            )
        }
    }
}

@Suppress("unused")
val postViewShowUserIdPatch = bytecodePatch(
    name = "Show user ID",
    description = "Shows the user ID in the various places."
) {
    compatibleWith("com.dcinside.app.android")

    dependsOn(
        sharedExtensionPatch,
        postHeaderShowUserIdResourcePatch,
        replyShowUserIdResourcePatch,
    )

    apply {
        postHeaderSetupMethodMatch.let {
            it.method.apply {
                val userIdIndex = it[2]
                val userIdReference = getInstruction<OneRegisterInstruction>(userIdIndex).reference!!

                val charSequenceIndex = it[6]
                val charSequenceRegister = getInstruction<OneRegisterInstruction>(charSequenceIndex).registerA

                val registerProvider = getFreeRegisterProvider(charSequenceIndex, 2, charSequenceRegister)
                val viewRegister = registerProvider.getFreeRegister()
                val userIdRegister = registerProvider.getFreeRegister()

                addInstructions(
                    charSequenceIndex + 1,
                    """
                        move-object/from16 v$viewRegister, p0
                        
                        move-object/from16 v$userIdRegister, p1
                        invoke-virtual { v$userIdRegister }, $userIdReference
                        move-result-object v$userIdRegister
                        
                        invoke-static { v$viewRegister , v$userIdRegister, v$charSequenceRegister }, $POST_SHOW_USER_ID_EXTENSION_CLASS_DESCRIPTOR->setUserId(Landroid/view/View;Ljava/lang/String;Ljava/lang/CharSequence;)V
                    """
                )
            }
        }

        postReplySetupMethodMatch.let {
            it.method.apply {
                val userIdIndex = it[10]
                val userIdReference = getInstruction<OneRegisterInstruction>(userIdIndex).reference!!

                val charSequenceIndex = it[-1]
                val charSequenceRegister = getInstruction<OneRegisterInstruction>(charSequenceIndex).registerA

                val viewRegister = getInstruction<FiveRegisterInstruction>(charSequenceIndex - 1).registerD
                val userIdRegister = getInstruction<FiveRegisterInstruction>(charSequenceIndex - 1).registerE

                val viewIndex = it[3]
                val viewReference = getInstruction<OneRegisterInstruction>(viewIndex).reference!!

                addInstructions(
                    charSequenceIndex + 1,
                    """
                        move-object/from16 v$viewRegister, p1
                        invoke-virtual { v$viewRegister }, $viewReference
                        move-result-object v$viewRegister
                        
                        move-object/from16 v$userIdRegister, p3
                        invoke-virtual { v$userIdRegister }, $userIdReference
                        move-result-object v$userIdRegister
                        
                        invoke-static { v$viewRegister , v$userIdRegister, v$charSequenceRegister }, $POST_SHOW_USER_ID_EXTENSION_CLASS_DESCRIPTOR->setUserId(Landroid/view/View;Ljava/lang/String;Ljava/lang/CharSequence;)V
                    """
                )
            }
        }

        postHistoryRealmSetupMethodMatch.let {
            it.method.apply {
                fun ClassDef.getFieldBySerializedName(serializedName: String) = fields.first { field ->
                    field.annotations.any { annotation ->
                        annotation.elements.any { element -> element.name == "value" && (element.value as? StringEncodedValue)?.value == serializedName }
                    }
                }
                val postItemClassDef = firstClassDef(POST_ITEM_CLASS_DESCRIPTOR)

                val userIdField = postItemClassDef.getFieldBySerializedName("user_id")
                val userIpField = postItemClassDef.getFieldBySerializedName("ip")
                val userNameField = postItemClassDef.getFieldBySerializedName("name")

                val userIdGetterMethodReference = postItemClassDef.getStringGetterMethod(userIdField.name)
                val userIpGetterMethodReference = postItemClassDef.getStringGetterMethod(userIpField.name)
                val userNameGetterMethodReference = postItemClassDef.getStringGetterMethod(userNameField.name, it)

                val userNameIndex = indexOfFirstInstructionOrThrow {methodReference == userNameGetterMethodReference} + 1
                val userNameRegister = getInstruction<OneRegisterInstruction>(userNameIndex).registerA

                val registerProvider = getFreeRegisterProvider(userNameIndex, 2, userNameRegister)
                val userIdRegister = registerProvider.getFreeRegister()
                val userIpRegister = registerProvider.getFreeRegister()

                addInstructions(
                    userNameIndex + 1,
                    """
                        invoke-virtual {p2}, $userIdGetterMethodReference
                        move-result-object v$userIdRegister
                        invoke-virtual {p2}, $userIpGetterMethodReference
                        move-result-object v$userIpRegister
                        invoke-static {v$userNameRegister, v$userIdRegister, v$userIpRegister }, $POST_SHOW_USER_ID_EXTENSION_CLASS_DESCRIPTOR->addUserId(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$userNameRegister
                    """
                )
            }
        }
    }
}