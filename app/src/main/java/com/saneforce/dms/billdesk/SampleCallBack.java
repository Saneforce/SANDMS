package com.saneforce.dms.billdesk;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.billdesk.sdk.LibraryPaymentStatusProtocol;
import com.billdesk.sdk.PaymentOptions;
import com.google.gson.JsonObject;
import com.saneforce.dms.activity.PaymentDetailsActivity;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Shared_Common_Pref;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SampleCallBack implements LibraryPaymentStatusProtocol, Parcelable {
	String TAG = "Callback ::: > ";
	DMS.PaymentResponse paymentResponse;
	public SampleCallBack() {
		Log.v(TAG, "CallBack()....");

	}
	public void setPaymentResponseListener(DMS.PaymentResponse paymentResponse){
		this.paymentResponse = paymentResponse;
	}

	public SampleCallBack(Parcel in) {
		Log.v(TAG, "CallBack(Parcel in)....");
	}

	@Override
	public void paymentStatus(String status, Activity context) {
		Log.v(TAG, "paymentStatus(String status, Activity context)....::::status:::::"+ status);
//		Toast.makeText(context, "PG Response:: " + status, Toast.LENGTH_LONG).show();

		if(paymentResponse!=null){
			paymentResponse.onResponse(status);

/*			Intent mIntent = new Intent(context, StatusActivity.class);
			mIntent.putExtra("status", status);
			context.startActivity(mIntent);
			context.finish();*/
		}else {
			Toast.makeText(context, "PG Response:: " + status, Toast.LENGTH_LONG).show();
		}

	}


	@Override
	public void tryAgain() {
		Log.d(TAG, "tryAgain() called");
	}

	@Override
	public void onError(Exception e) {
		Log.d(TAG, "onError() called with: e = [" + e + "]");
	}

	@Override
	public void cancelTransaction() {
		Log.d(TAG, "cancelTransaction() called");
	}

	@Override
	public int describeContents() {
		Log.v(TAG, "describeContents()....");
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.v(TAG, "writeToParcel(Parcel dest, int flags)....");
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("rawtypes")
	public static final Creator CREATOR = new Creator() {
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
}
