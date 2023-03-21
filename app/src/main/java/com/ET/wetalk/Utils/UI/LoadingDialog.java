package com.ET.wetalk.Utils.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ET.wetalk.R;

import org.xmlpull.v1.XmlPullParser;

public class LoadingDialog {

    Activity activity;
    AlertDialog dialog;
    TextView contentLoadingView;
    public LoadingDialog(Activity myActivity)
    {
        activity = myActivity;
        init();
    }

    private void init()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        contentLoadingView = dialogView.findViewById(R.id.loadingContent);
        builder.setView(dialogView);
        builder.setCancelable(false);
        dialog = builder.create();

    }

    public void startLoadingDialog()
    {

        dialog.show();
    }

    public void dismissDialog()
    {
        dialog.dismiss();
    }

    public void setContent(String content)
    {
        contentLoadingView.setText(content);
    }

}
