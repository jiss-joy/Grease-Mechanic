package com.rsa.greasemechanic;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;

public class LoadingDialog {
    private final Activity mActivity;
    private final Dialog dialog;

    public LoadingDialog(Activity activity) {
        this.mActivity = activity;
        dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.custom_loading_dialog);
    }

    public void showLoadingDialog(String mMessage) {
        TextView message_tv = dialog.findViewById(R.id.loading_dialog_text);
        message_tv.setText(mMessage);

        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissLoadingDialog() {
        dialog.dismiss();
    }
}
