package app.revanced.patches.twitter.misc.hook.patch.recommendation

import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.twitter.misc.hook.json.JsonHookPatch
import app.revanced.patches.twitter.misc.hook.BaseHookPatchPatch

@Patch(
    name = "Hide recommended users",
    description = "Hides recommended users.",
    dependencies = [ JsonHookPatch::class ],
    compatiblePackages = [ CompatiblePackage("com.twitter.android") ]
)
class HideRecommendedUsersPatch : BaseHookPatchPatch(
    "Lapp/revanced/twitter/patches/hook/patch/recommendation/RecommendedUsersHook;"
)