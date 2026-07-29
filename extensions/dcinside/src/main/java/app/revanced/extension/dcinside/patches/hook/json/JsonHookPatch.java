package app.revanced.extension.dcinside.patches.hook.json;

import android.util.Log;
import app.revanced.extension.dcinside.patches.hook.patch.DummyHook;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public final class JsonHookPatch {
    public static final JsonHookPatch INSTANCE = new JsonHookPatch();

    private static final String TAG = "ReVanced_DCInside";

    public static boolean ManagerSkill = false;

    private static final List<JsonHook> hooks;

    static {
        hooks = new ArrayList<>();
        hooks.add(DummyHook.INSTANCE);
    }

    private JsonHookPatch() {
    }

    public static String jsonHook(String json) {
        try {
            JSONArray root = new JSONArray(json);
            JSONObject response = root.getJSONObject(0);

            if (response.has("gall_info")) {
                JSONObject gallInfo = response.getJSONArray("gall_info").getJSONObject(0);
                JsonHookPatch.ManagerSkill = gallInfo.optBoolean("managerskill", false);
            }
        } catch (Exception e) {
            Log.e(TAG, "jsonHook: failed to parse JSON", e);
        }

        for (JsonHook hook : hooks) {
            json = hook.hook(json);
        }
        return json;
    }
}