package app.revanced.extension.dcinside.patches.hook.patch;

import android.util.Log;
import app.revanced.extension.dcinside.patches.hook.json.BaseJsonHook;
import org.json.JSONArray;
import org.json.JSONObject;

public final class HideMustReadNoticeHook extends BaseJsonHook {
    public static final HideMustReadNoticeHook INSTANCE = new HideMustReadNoticeHook();

    private static final String TAG = "ReVanced";

    private HideMustReadNoticeHook() {
    }

    @Override
    public String apply(String json) {
        try {
            JSONArray root = new JSONArray(json);
            JSONObject response = root.getJSONObject(0);

            if (!response.has("gall_info")) return json;

            JSONObject gallInfo = response.getJSONArray("gall_info").getJSONObject(0);
            if (gallInfo.has("must_read")) {
                gallInfo.remove("must_read");
                Log.d(TAG, "HideMustReadNoticeHook: removed must_read=" + gallInfo.optString("must_read"));
            }

            return root.toString();
        } catch (Exception e) {
            Log.e(TAG, "HideMustReadNoticeHook: failed to parse JSON", e);
            return json;
        }
    }
}
