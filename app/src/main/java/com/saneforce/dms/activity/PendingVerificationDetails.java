package com.saneforce.dms.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.gson.JsonObject;
import com.saneforce.dms.adapter.DateReportAdapter;
import com.saneforce.dms.listener.ApiInterface;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.ApiClient;
import com.saneforce.dms.utils.Common_Class;
import com.saneforce.dms.utils.Shared_Common_Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class PendingVerificationDetails extends AppCompatActivity {
    String OrderID = "", PayDt = "", Amount = "", Stockist_Name = "", UTRNumber = "", Imgurl = "", paymentType = "", paymentOption = "";
    TextView txtOrderID, txtPayDt, txtAmount, txtStockist_Name, txtUTRNumber, txtImgurl;
    ImageView imgView;
    Shared_Common_Pref mShared_common_pref;
    Context context;

    //image zoom
    float[] lastEvent = null;
    float d = 0f;
    float newRot = 0f;
    private boolean isZoomAndRotate;
    private boolean isOutSide;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float oldDist = 1f;
    private float xCoOrdinate, yCoOrdinate;
//    private ScaleGestureDetector scaleGestureDetector;
//    private float mScaleFactor = 1.0f;

    TextView tv_payment_type;
    LinearLayout ll_payment_option;
    TextView tv_payment_option;

    int editMode = 0;
    LinearLayout ll_update_details;
    RecyclerView DateRecyclerView;

    String OrderType = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verification_details);

        mCommon_class = new Common_Class(PendingVerificationDetails.this);
        OrderID = String.valueOf(getIntent().getSerializableExtra("OrderID"));
        PayDt = String.valueOf(getIntent().getSerializableExtra("PayDt"));
        Amount = String.valueOf(getIntent().getSerializableExtra("Amount"));
        Stockist_Name = String.valueOf(getIntent().getSerializableExtra("Stockist_Name"));
        UTRNumber = String.valueOf(getIntent().getSerializableExtra("UTRNumber"));
        Imgurl = String.valueOf(getIntent().getSerializableExtra("Imgurl"));
        paymentType = String.valueOf(getIntent().getSerializableExtra("paymentType"));
        paymentOption = String.valueOf(getIntent().getSerializableExtra("paymentMode"));
//        Log.v("img1",Imgurl);

        if(getIntent().hasExtra("editMode"))
            editMode = getIntent().getIntExtra("editMode", 0);

        mShared_common_pref = new Shared_Common_Pref(this);

        txtOrderID = findViewById(R.id.product_order_id);
        txtAmount = findViewById(R.id.product_amnt);
        txtStockist_Name = findViewById(R.id.product_distributor);
        txtUTRNumber = findViewById(R.id.product_number);
        imgView = findViewById(R.id.bank_receipt);
        tv_payment_type= findViewById(R.id.tv_payment_type);
        ll_payment_option= findViewById(R.id.ll_payment_option);
        tv_payment_option= findViewById(R.id.tv_payment_option);
        ll_update_details= findViewById(R.id.ll_update_details);
        DateRecyclerView = findViewById(R.id.date_recycler);


        if(paymentType!=null && !paymentType.equals(""))
            tv_payment_type.setText(paymentType);

        if(paymentOption!=null && !paymentOption.equals("")){
            ll_payment_option.setVisibility(View.VISIBLE);
            tv_payment_option.setText(paymentOption);
        }else {
            ll_payment_option.setVisibility(View.GONE);
        }

        txtOrderID.setText(OrderID);
        txtAmount.setText(Amount);
        txtStockist_Name.setText(Stockist_Name);
        txtUTRNumber.setText(UTRNumber);
        getToolbar();
//        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        context= imgView.getContext();
        RequestOptions myOptions = new RequestOptions()
                .fitCenter() // or centerCrop
                .override(100, 100);

        try {
            if(Imgurl!=null && !Imgurl.equals(""))
                Glide.with(context)
                    .asBitmap()
                    .apply(myOptions)
                    .load(Imgurl)
                    .into(imgView);

        } catch (Exception e) {
            e.printStackTrace();
        }

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Imgurl!=null && !Imgurl.equals(""))
                    showZoomableImage();
            }
        });
      /*  imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                view.bringToFront();
                viewTransformation(view, event);
                return true;
            }
        });*/



      //  imgView.setImageURI(Uri.parse(Imgurl));

        if(editMode == 1){
            ll_update_details.setVisibility(View.VISIBLE);
            DateRecyclerView.setVisibility(View.GONE);
        }else {
            ll_update_details.setVisibility(View.GONE);
            DateRecyclerView.setVisibility(View.VISIBLE);

            DateRecyclerView.setHasFixedSize(true);
            DateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ViewDateReport();
        }


    }
    Common_Class mCommon_class;

    public void ViewDateReport() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> responseBodyCall;
        if (OrderType.equalsIgnoreCase("1")) {
            responseBodyCall = apiInterface.dateReport("get/ViewReport_Details", OrderID, mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        } else {
            responseBodyCall = apiInterface.dateReport("get/secviewreport_details", OrderID, mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));
        }


//        Log.v("ViewDateREquest", responseBodyCall.request().toString());

        responseBodyCall.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonRootObject = null;

                try {
                    jsonRootObject = new JSONObject(response.body().toString());
                    JSONArray jsonArray = jsonRootObject.optJSONArray("Data");
/*                    JSONObject jsonObject = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        //    OrderValueTotal=Double.valueOf(jsonObject.getString("Order_Value"));
                        if(!jsonObject.isNull("OrderVal"))
                            OrderValueTotal=Double.valueOf(jsonObject.getString("OrderVal"));
                        if(!jsonObject.isNull("taxval"))
                            OrderAmtNew= Double.valueOf(jsonObject.getString("taxval"));
                        //  TotalValue.setText("Rs."+jsonObject.getString("taxval"));//working code commented
                        String total = "0";
                        if(jsonObject.has("OrderVal") && !jsonObject.getString("OrderVal").equals(""))
                            total = Constants.roundTwoDecimals(Double.parseDouble(jsonObject.getString("OrderVal")));



                    }*/
                    DateReportAdapter mDateReportAdapter = new DateReportAdapter(PendingVerificationDetails.this, jsonArray);
                    DateRecyclerView.setAdapter(mDateReportAdapter);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(PendingVerificationDetails.this, "Something went wrong, please try again",Toast.LENGTH_SHORT ).show();
            }
        });
    }

    public void getToolbar() {

        Intent intent = getIntent();
        if(intent.hasExtra("title") && intent.getStringExtra("title")!=null){
            TextView toolbar_title = findViewById(R.id.toolbar_title);
            toolbar_title.setText(intent.getStringExtra("title"));
        }

        ImageView imgBack = (ImageView) findViewById(R.id.toolbar_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    ImageView imageView;
    public void showZoomableImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.custom_dialog, null);
        imageView = dialogLayout.findViewById(R.id.iv_image);

        try {
            Glide.with(context)
                    .asBitmap()
                    .load(Imgurl)
                    .into(imageView);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ImageView view = (ImageView) v;
                    view.bringToFront();
                    viewTransformation(view, event);
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();
    }

    public void Cancel(View v) {
//        startActivity(new Intent(PendingVerificationDetails.this,PendingVerification.class));
//        finish();
        onBackPressed();
    }
  /*  @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            if(imageView!=null){
                imageView.setScaleX(mScaleFactor);
                imageView.setScaleY(mScaleFactor);
            }
            return true;
        }
    }*/

    public void Verify(View v) {
        verify();
    }


    public void verify() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.getPayVerification(OrderID, mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));

//        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//              startActivity(new Intent(PendingVerificationDetails.this,PendingVerification.class));
//                finish();
                onBackPressed();
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(PendingVerificationDetails.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //image zoom start
    private void viewTransformation(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xCoOrdinate = view.getX() - event.getRawX();
                yCoOrdinate = view.getY() - event.getRawY();

                start.set(event.getX(), event.getY());
                isOutSide = false;
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    midPoint(mid, event);
                    mode = ZOOM;
                }

                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
                isZoomAndRotate = false;
                if (mode == DRAG) {
                    float x = event.getX();
                    float y = event.getY();
                }
            case MotionEvent.ACTION_OUTSIDE:
                isOutSide = true;
                mode = NONE;
                lastEvent = null;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isOutSide) {
                    if (mode == DRAG) {
                        isZoomAndRotate = false;
                        view.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                    }
                    if (mode == ZOOM && event.getPointerCount() == 2) {
                        float newDist1 = spacing(event);
                        if (newDist1 > 10f) {
                            float scale = newDist1 / oldDist * view.getScaleX();
                            view.setScaleX(scale);
                            view.setScaleY(scale);
                        }
                        if (lastEvent != null) {
                            newRot = rotation(event);
                            view.setRotation((float) (view.getRotation() + (newRot - d)));
                        }
                    }
                }
                break;
        }
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (int) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    //image zoom end


    @Override
    public void onBackPressed() {
        finish();
    }
}