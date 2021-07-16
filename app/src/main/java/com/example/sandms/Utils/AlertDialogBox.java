package com.example.sandms.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.sandms.Interface.DMS;


public class AlertDialogBox {
    public static void showDialog(Context context, String title, String message, String positiveBtnCaption, String negativeBtnCaption, boolean isCancelable, final DMS.AlertBox target) {
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    target.PositiveMethod(dialog, id);
                }
            }).setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    target.NegativeMethod(dialog, id);
                }
            });

            AlertDialog alert = builder.create();
            alert.setCancelable(isCancelable);
            alert.show();
            if (isCancelable) {
                alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface arg0) {
                        target.NegativeMethod(null, 0);
                    }
                });
            }
        }
    }






}
