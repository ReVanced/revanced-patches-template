package app.revanced.extension.dcinside.patches.hook.patch;

import app.revanced.extension.dcinside.patches.hook.json.BaseJsonHook;
import org.json.JSONObject;
import org.jetbrains.annotations.NotNull;

/**
 * Dummy hook to reserve a register in [JsonHookPatch.hooks] list.
 */
public final class DummyHook extends BaseJsonHook {
    public static final DummyHook INSTANCE = new DummyHook();

    private DummyHook() {
    }

    @Override
    public String apply(@NotNull String json) {
        return json;
    }
}