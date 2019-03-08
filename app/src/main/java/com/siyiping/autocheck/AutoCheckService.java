package com.siyiping.autocheck;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class AutoCheckService extends AccessibilityService {

    private AccessibilityNodeInfo changeLoginBtn;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("jihuo","service create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("jihuo","service destroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("jihuo","service Unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo info = accessibilityEvent.getSource();
        if (info == null) {
            return;
        }

        try {
            int eventType = accessibilityEvent.getEventType();
            String className = accessibilityEvent.getClassName().toString();
            Log.i("jihuo", "eventType " + eventType + "  class name  " + accessibilityEvent.getClassName());
            switch (eventType) {
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    clickViewById("com.bankcomm.maidanba:id/fpt_dialog_bottom_cancel");
                    inputPwd();
                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    if (className.equals("com.bankcomm.maidanba.activity.MainActivity")) {
                        clickViewById("com.bankcomm.maidanba:id/center_image");
                    } else if (className.equals("com.bankcomm.maidanba.activity.FingerprintLoginActivity")) {
                        if (changeLoginBtn != null) {
                            changeLoginBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                    break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    clickChangeLogin("com.bankcomm.maidanba:id/tv_change_login");
                    if (changeLoginBtn != null) {
                        changeLoginBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onInterrupt() {

    }

    private void clickViewById(String viewId) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId(viewId);
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            Log.i("jihuo", "点击成功  " + viewId);
            AccessibilityNodeInfo nodeInfo = nodeInfos.get(0);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (viewId.equals("com.bankcomm.maidanba:id/fpt_dialog_bottom_cancel")) {
                List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId("com.bankcomm.maidanba:id/tv_change_login");
                if (nodes != null && !nodes.isEmpty()) {
                    Log.i("jihuo", "find  changeLoginBtn");
                    changeLoginBtn = nodes.get(0);
                }
            }
        }
    }

    private void clickChangeLogin(String viewId) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        Log.i("jihuo", "root " + root.toString());
        if (root == null) return;
        List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId(viewId);
        Log.i("jihuo", nodeInfos.toString());
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            AccessibilityNodeInfo nodeInfo = nodeInfos.get(0);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void inputPwd() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        AccessibilityNodeInfo pwdText = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (pwdText != null) {
            pwdText.getViewIdResourceName();
            //粘贴板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", "syp0715");
            clipboard.setPrimaryClip(clip);

            CharSequence txt = pwdText.getText();
            if (txt == null) txt = "";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Bundle arguments = new Bundle();
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0);
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, txt.length());
                pwdText.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                pwdText.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments);
                pwdText.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            }
        }
    }
}
