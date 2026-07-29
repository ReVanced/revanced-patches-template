package app.revanced.extension.dcinside.patches.hook.patch;

import android.util.Log;
import app.revanced.extension.dcinside.patches.hook.json.BaseJsonHook;
import org.json.JSONArray;
import org.json.JSONObject;

public final class HideAdministratorNoticeHook extends BaseJsonHook {
    public static final HideAdministratorNoticeHook INSTANCE = new HideAdministratorNoticeHook();

    private static final String TAG = "ReVanced";

    private HideAdministratorNoticeHook() {
    }

    @Override
    public String apply(String json) {
        try {
            JSONArray root = new JSONArray(json);
            JSONObject response = root.getJSONObject(0);

            if (!response.has("gall_list")) return json;

            JSONArray gallList = response.getJSONArray("gall_list");
            JSONArray filtered = new JSONArray();

            for (int i = 0; i < gallList.length(); i++) {
                JSONObject post = gallList.getJSONObject(i);
                String no = post.optString("no");
                if (!no.isEmpty()) {
                    filtered.put(post);
                } else {
                    Log.d(TAG, "HideAdministratorNoticeHook: filtered out post subject=" + post.optString("subject"));
                }
            }

            response.put("gall_list", filtered);
            root.put(0, response);

            return root.toString();
        } catch (Exception e) {
            Log.e(TAG, "HideAdministratorNoticeHook: failed to parse JSON", e);
            return json;
        }
    }
}