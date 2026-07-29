package app.revanced.patches.dcinside.post.list

import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.extensions.methodReference
import app.revanced.patcher.extensions.reference
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.patch.resourcePatch
import app.revanced.patches.dcinside.misc.extension.sharedExtensionPatch
import app.revanced.patches.dcinside.misc.hook.json.jsonHookPatch
import app.revanced.util.doRecursively
import app.revanced.util.getFreeRegisterProvider
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import org.w3c.dom.Element

private const val QUICK_POST_CONTROL_PATCH_EXTENSION_CLASS_DESCRIPTOR = "Lapp/revanced/extension/dcinside/patches/QuickPostControlPatch;"
private const val POST_SHOW_USER_ID_PATCH_EXTENSION_CLASS_DESCRIPTOR = "Lapp/revanced/extension/dcinside/patches/PostShowUserIdPatch;"

private val showUserIdResourcePatch = resourcePatch {
    compatibleWith("com.dcinside.app.android")

    apply {
        listOf(
            "res/layout/view_post_list_item_basic.xml",
            "res/layout/view_post_list_item_split.xml",
        ).forEach { layoutPath ->
            document(layoutPath).use { document ->
                val root = document.documentElement ?: return@use

                var countsElement: Element? = null
                var memberIcElement: Element? = null

                root.doRecursively { node ->
                    if (node is Element) {
                        when (node.getAttribute("android:id")) {
                            "@+id/post_list_item_counts", "@id/post_list_item_counts" -> countsElement = node
                            "@+id/post_list_item_member_ic", "@id/post_list_item_member_ic" -> memberIcElement = node
                        }
                    }
                }

                val targetCounts = countsElement ?: return@use
                val targetMemberIc = memberIcElement ?: return@use

                /* [Anchor Re-linking] member_ic와 counts 사이 제약 조건을 신규 주입할 View ID로 재연결 */
                targetMemberIc.setAttribute("app:layout_constraintEnd_toStartOf", "@+id/custom_user_id")
                targetCounts.setAttribute("app:layout_constraintStart_toEndOf", "@+id/custom_user_id")

                /* [DOM Injection] user_id 표시용 TextView 생성 및 Constraint/Style 속성 할당 */
                val userIdElement = document.createElement("com.dcinside.app.view.ResizeTextView").apply {
                    setAttribute("android:textAppearance", "?attr/textTypePostExt")
                    setAttribute("android:id", "@+id/custom_user_id")
                    setAttribute("android:layout_width", "wrap_content")
                    setAttribute("android:layout_height", "wrap_content")
                    setAttribute("android:singleLine", "true")
                    setAttribute("android:includeFontPadding", "false")
                    setAttribute("android:visibility", "gone")
                    setAttribute("android:layout_marginStart", "2dp")

                    setAttribute("app:layout_constraintBottom_toBottomOf", "@+id/post_list_item_nic")
                    setAttribute("app:layout_constraintEnd_toStartOf", "@+id/post_list_item_counts")
                    setAttribute("app:layout_constraintStart_toEndOf", "@+id/post_list_item_member_ic")
                    setAttribute("app:layout_constraintTop_toTopOf", "@+id/post_list_item_nic")
                }

                /* [DOM Insertion] member_ic 노드와 counts 노드 사이에 삽입 */
                targetMemberIc.parentNode?.insertBefore(userIdElement, targetCounts)
            }
        }
    }
}

@Suppress("unused")
val postListExtraFeaturesPatch = bytecodePatch(
    name = "Post list extra features",
    description = "Enables extra features in the post list.",
) {
    compatibleWith("com.dcinside.app.android")

    dependsOn(
        sharedExtensionPatch,
        jsonHookPatch,
        showUserIdResourcePatch
    )

    val longPress by booleanOption(
        default = true,
        name = "Long press to control posts",
        description = "Adds a long press option to manage posts using manager permission."
    )

    val showUserId by booleanOption(
        default = true,
        name = "Show user ID",
        description = "Shows the user ID in the post list."
    )

    apply {
        jsonApiPostListHookMethod.apply {
            addInstructions(
                0,
                $$"""
                    invoke-static/range {p0 .. p0}, $$QUICK_POST_CONTROL_PATCH_EXTENSION_CLASS_DESCRIPTOR->hookGalleryID(Ljava/lang/String;)V
                """
            )
        }

        addQueryParameterHookMethod.addInstructions(
            0,
            $$"""
                move-object/from16 v0, p1
                move-object/from16 v1, p2
                invoke-static {v0, v1}, $$QUICK_POST_CONTROL_PATCH_EXTENSION_CLASS_DESCRIPTOR->hookParam(Ljava/lang/String;Ljava/lang/String;)V
            """
        )

        postItemBindMethodMatch.let {
            it.method.apply {
                val postItemIndex = it[1]
                val postItemRegister = getInstruction<OneRegisterInstruction>(postItemIndex).registerA

                val userIdMethodReference = getInstruction(it[3]).methodReference

                val postNoMethodReference = getInstruction(it[4]).methodReference

                val spannableIndex = it[-1]
                val spannableRegister = getInstruction<OneRegisterInstruction>(spannableIndex).registerA

                val registerProvider = getFreeRegisterProvider(spannableIndex, 4, spannableRegister)
                val viewRegister = registerProvider.getFreeRegister()
                val freeRegister = registerProvider.getFreeRegister()

                val insertIndex = spannableIndex + 1
                var insertSmali = $$"""
                    move-object/from16 v$$viewRegister, p1
                    iget-object v$$viewRegister, v$$viewRegister, Landroidx/recyclerview/widget/RecyclerView$ViewHolder;->itemView:Landroid/view/View;
                """
                if (longPress!!) {
                    insertSmali += $$"""                        
                        invoke-virtual {v$$postItemRegister}, $$postNoMethodReference
                        move-result v$$freeRegister
                        
                        invoke-static {v$$viewRegister, v$$freeRegister}, $${QUICK_POST_CONTROL_PATCH_EXTENSION_CLASS_DESCRIPTOR}->setLongClickListener(Landroid/view/View;I)V
                    """
                }
                if (showUserId!!) {
                    insertSmali += $$"""                        
                        invoke-virtual {v$$postItemRegister}, $$userIdMethodReference
                        move-result-object v$$freeRegister
                        
                        invoke-static {v$$viewRegister, v$$freeRegister, v$$spannableRegister}, $$POST_SHOW_USER_ID_PATCH_EXTENSION_CLASS_DESCRIPTOR->setUserId(Landroid/view/View;Ljava/lang/String;Ljava/lang/CharSequence;)V
                    """
                }

                addInstructions(insertIndex, insertSmali)
            }
        }

        postSearchItemBindMethodMatch.let {
            it.method.apply {
                val postItemIndex = it[1]
                val postItemRegister = getInstruction<OneRegisterInstruction>(postItemIndex).registerA

                val userIdMethodReference = getInstruction(it[6]).methodReference

                val spannableIndex = it[-1]
                val spannableRegister = getInstruction<OneRegisterInstruction>(spannableIndex).registerA

                val registerProvider = getFreeRegisterProvider(postItemIndex, 2, postItemRegister)
                val viewRegister = registerProvider.getFreeRegister()
                val userIdRegister = registerProvider.getFreeRegister()

                val insertIndex = spannableIndex + 1
                addInstructions(
                    insertIndex,
                    $$"""
                        move-object/from16 v$$viewRegister, p1
                        iget-object v$$viewRegister, v$$viewRegister, Landroidx/recyclerview/widget/RecyclerView$ViewHolder;->itemView:Landroid/view/View;
                        
                        invoke-virtual {v$$postItemRegister}, $$userIdMethodReference
                        move-result-object v$$userIdRegister
                        
                        invoke-static {v$$viewRegister, v$$userIdRegister, v$$spannableRegister}, $$POST_SHOW_USER_ID_PATCH_EXTENSION_CLASS_DESCRIPTOR->setUserId(Landroid/view/View;Ljava/lang/String;Ljava/lang/CharSequence;)V
                    """
                )
            }
        }
    }
}