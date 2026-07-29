package app.revanced.extension.dcinside.patches;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import app.revanced.extension.dcinside.patches.hook.json.JsonHookPatch;

public class QuickPostControlPatch {
    private static String galleryId = "";
    private static String appId = "";
    private static String userId = "";

    private static String TAG = "ReVanced_DCInside";

    public static void hookGalleryID(String id) {
        Log.d(TAG, "gallery id: " + id);
        galleryId = id;
    }

    public static void hookParam(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        Log.d(TAG, "[PARAM] " + key + " = " + value);

        if ("app_id".equals(key)) {
            appId = value;
        } else if ("user_id".equals(key) || "confirm_id".equals(key)) {
            userId = value;
        }
    }

    public static void setLongClickListener(View itemView, int postNo) {
        itemView.setAlpha(1.0f);
        itemView.setClickable(true);
        itemView.setLongClickable(true);

        if (!JsonHookPatch.ManagerSkill) {
            return;
        }

        Context context = itemView.getContext();
        TypedArray typedArray = context.obtainStyledAttributes(
                new int[]{android.R.attr.selectableItemBackground}
        );
        Drawable foregroundDrawable = typedArray.getDrawable(0);
        typedArray.recycle();
        itemView.setForeground(foregroundDrawable);

        itemView.setOnLongClickListener(view -> {
            showManageDialog(context, itemView, postNo);
            return true;
        });
    }

    private static int resolveDialogTheme(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(
                context.getResources().getIdentifier("alertDialogTheme", "attr", context.getPackageName()),
                typedValue,
                true
        );
        return typedValue.resourceId;
    }

    private static void showManageDialog(Context context, View itemView, int postNo) {
        int dialogThemeResId = resolveDialogTheme(context);

        AlertDialog dialog = new AlertDialog.Builder(context, dialogThemeResId)
                .setTitle("게시글 관리")
                .setMessage("이 게시글에 대한 작업을 선택하세요.")
                .setNeutralButton("차단", (d, which) -> showBlockConfirmDialog(context, postNo))
                .setNegativeButton("삭제", (d, which) -> showDeleteConfirmDialog(context, itemView, postNo))
                .setPositiveButton("취소", null)
                .create();

        dialog.show();

        Button blockButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        if (blockButton != null) {
            blockButton.setTextColor(Color.RED);
        }
    }

    private static void showBlockConfirmDialog(Context context, int postNo) {
        int dialogThemeResId = resolveDialogTheme(context);

        AlertDialog dialog = new AlertDialog.Builder(context, dialogThemeResId)
                .setTitle("차단")
                .setMessage("이 사용자를 차단하시겠습니까?")
                .setPositiveButton("확인", (d, which) -> {
                    try {
                        Intent intent = new Intent(context, Class.forName("com.dcinside.app.manager.MinorExtActivity"));
                        intent.setAction("action_block");
                        intent.putExtra("com.dcinside.app.extra.GALLERY_ID", galleryId);
                        intent.putExtra("com.dcinside.app.extra.POST_NUMBER", postNo);

                        if (context instanceof Activity) {
                            ((Activity) context).startActivityForResult(intent, 1031);
                        } else {
                            Log.e(TAG, "Context가 Activity 인스턴스가 아닙니다.");
                        }

                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "MinorExtActivity 클래스를 찾을 수 없습니다.", e);
                    }
                })
                .setNegativeButton("취소", null)
                .show();

        Button blockButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (blockButton != null) {
            blockButton.setTextColor(Color.RED);
        }
    }

    private static void showDeleteConfirmDialog(Context context, View itemView, int postNo) {
        int dialogThemeResId = resolveDialogTheme(context);

        AlertDialog dialog = new AlertDialog.Builder(context, dialogThemeResId)
                .setTitle("삭제")
                .setMessage("이 게시글을 삭제하시겠습니까?")
                .setPositiveButton("확인", (d, which) -> performDelete(context, itemView, postNo))
                .setNegativeButton("취소", null)
                .show();

        Button blockButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (blockButton != null) {
            blockButton.setTextColor(Color.RED);
        }
    }

    private static void performDelete(Context context, View itemView, int postNo) {
        String clientToken = getClientToken(context);
        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

        new Thread(() -> {
            boolean success;
            String errorMessage = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL("https://app.dcinside.com/api/gall_del.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty(
                        "Content-Type",
                        "application/x-www-form-urlencoded; charset=UTF-8"
                );

                StringBuilder body = new StringBuilder();
                appendParam(body, "user_id", userId);
                appendParam(body, "client_token", clientToken);
                appendParam(body, "id", galleryId);
                appendParam(body, "no", String.valueOf(postNo));
                appendParam(body, "mode", "board_del");
                appendParam(body, "app_id", appId);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body.toString().getBytes("UTF-8"));
                }

                int responseCode = connection.getResponseCode();

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                StringBuilder responseBody = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBody.append(line);
                    }
                }

                Log.d(TAG, "delete response: " + responseCode + " " + responseBody);

                success = responseCode == 200 && responseBody.toString().contains("\"result\":true");
            } catch (Exception e) {
                Log.e(TAG, "performDelete failed", e);
                errorMessage = e.getMessage();
                success = false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            final boolean finalSuccess = success;
            final String finalErrorMessage = errorMessage;
            mainHandler.post(() -> {
                if (finalSuccess) {
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    itemView.setAlpha(0.4f);
                    itemView.setForeground(null);
                    itemView.setClickable(false);
                    itemView.setLongClickable(false);
                    itemView.setOnLongClickListener(null);
                } else {
                    Toast.makeText(context, "삭제 실패: " + finalErrorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private static void appendParam(StringBuilder body, String key, String value) {
        try {
            if (body.length() > 0) {
                body.append("&");
            }
            body.append(URLEncoder.encode(key, "UTF-8"));
            body.append("=");
            body.append(URLEncoder.encode(value != null ? value : "", "UTF-8"));
        } catch (Exception e) {
            Log.e(TAG, "appendParam failed", e);
        }
    }

    private static String getClientToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("save", Context.MODE_PRIVATE);
        return prefs.getString("firebaseInstanceToken", null);
    }
}