package com.saneforce.dms.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.billdesk.sdk.LibraryPaymentStatusProtocol;
import com.saneforce.dms.listener.DMS;

public class SampleCallBack implements LibraryPaymentStatusProtocol, Parcelable {
    String TAG = SampleCallBack.class.getSimpleName();
    DMS.PaymentResponseBilldesk paymentResponseBilldesk;
    Context context;

    public SampleCallBack(DMS.PaymentResponseBilldesk paymentResponseBilldesk, Context context) {
        this.paymentResponseBilldesk = paymentResponseBilldesk;
        this.context = context;
        Log.v(TAG, "CallBack()....");

    }

    public SampleCallBack(Parcel in) {
    }

    @SuppressWarnings("rawtypes")
    public static  Creator CREATOR = new Creator() {
        String TAG = "Callback --- Parcelable.Creator ::: > ";

        @Override
        public SampleCallBack createFromParcel(Parcel in) {
            Log.v(TAG, "CallBackActivity createFromParcel(Parcel in)....");
            return new SampleCallBack(in);
        }

        @Override
        public Object[] newArray(int size) {
            Log.v(TAG, "Object[] newArray(int size)....");
            return new SampleCallBack[size];
        }
    };




    @Override
    public void paymentStatus(String status, Activity context) {
        Log.v(TAG, "paymentStatus "+ status);
        paymentResponseBilldesk.onResponse(context, status);
    }

    @Override
    public void tryAgain() {
        Log.d(TAG, "tryAgain() called");
        Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception e) {
        Log.d(TAG, "onError() called with: e = [" + e + "]");
        Toast.makeText(context, "Error : "+ e, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelTransaction() {
        Log.d(TAG, "cancelTransaction() called");
        Toast.makeText(context, "Transaction Canceled ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
