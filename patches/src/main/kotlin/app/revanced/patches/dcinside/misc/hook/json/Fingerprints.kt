package app.revanced.patches.dcinside.misc.hook.json

import app.revanced.patcher.accessFlags
import app.revanced.patcher.firstImmutableClassDef
import app.revanced.patcher.firstMethodComposite
import app.revanced.patcher.gettingFirstMethodDeclaratively
import app.revanced.patcher.name
import app.revanced.patcher.opcodes
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.strings
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import kotlin.properties.ReadOnlyProperty

internal val BytecodePatchContext.jsonHookPatchMethodMatch by ReadOnlyProperty { context, _ ->
    context.firstImmutableClassDef(JSON_HOOK_PATCH_CLASS_DESCRIPTOR).firstMethodComposite {
        name("<clinit>")
        opcodes(
            Opcode.SGET_OBJECT, // Get DummyHook object.
            Opcode.INVOKE_INTERFACE, // Add hook to the hooks list.
        )
    }
}

internal val BytecodePatchContext.jsonHookMethod by gettingFirstMethodDeclaratively {
    strings("%s%s")
}