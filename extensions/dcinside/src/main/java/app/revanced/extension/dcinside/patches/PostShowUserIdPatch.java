package app.revanced.extension.dcinside.patches;

import android.util.Log;
import android.content.Context;
import android.graphics.Color;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

public class PostShowUserIdPatch {
    private PostShowUserIdPatch() {}

    private static final int DEFAULT_USER_ID_COLOR = Color.parseColor("#9E9E9E");

    private static final String TAG = "ReVanced_DCInside";

    public static void setUserId(View rootView, String userId) {
        setUserId(rootView, userId, null);
    }

    public static void setUserId(View view, String userId, CharSequence charSequence) {
        if (view == null) return;

        try {
            Context context = view.getContext();

            int targetTextViewId = context.getResources().getIdentifier(
                    "custom_user_id", "id", context.getPackageName()
            );

            if (targetTextViewId == 0) return;

            TextView userIdTextView = view.findViewById(targetTextViewId);
            if (userIdTextView == null) return;

            if (!TextUtils.isEmpty(userId)) {
                userIdTextView.setText("(" + userId + ") ");
                int color = extractMemoColor(charSequence);
                Log.d(TAG, "memo color: " + color);
                userIdTextView.setTextColor(color != 0 ? color : DEFAULT_USER_ID_COLOR);
                userIdTextView.setVisibility(View.VISIBLE);
            } else {
                userIdTextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setUserId", e);
        }
    }

    public static String addUserId(String userName, String userId, String userIp) {
        return userName + " (" + userId + userIp + ")";
    }

    private static int extractMemoColor(CharSequence charSequence) {
        if (!(charSequence instanceof Spanned)) return 0;
        Spanned spanned = (Spanned) charSequence;
        ForegroundColorSpan[] spans = spanned.getSpans(0, spanned.length(), ForegroundColorSpan.class);
        if (spans.length == 0) return 0;
        return spans[0].getForegroundColor();
    }
}