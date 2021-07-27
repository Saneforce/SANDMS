package com.example.sandms.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sandms.Interface.ApiInterface;
import com.example.sandms.R;
import com.example.sandms.Utils.ApiClient;
import com.example.sandms.Utils.Shared_Common_Pref;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.squareup.picasso.Picasso;
public class PendingVerificationDetails extends AppCompatActivity {
    String OrderID = "", PayDt = "", Amount = "", Stockist_Name = "", UTRNumber = "", Imgurl = "";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_verification_details);


        OrderID = String.valueOf(getIntent().getSerializableExtra("OrderID"));
        PayDt = String.valueOf(getIntent().getSerializableExtra("PayDt"));
        Amount = String.valueOf(getIntent().getSerializableExtra("Amount"));
        Stockist_Name = String.valueOf(getIntent().getSerializableExtra("Stockist_Name"));
        UTRNumber = String.valueOf(getIntent().getSerializableExtra("UTRNumber"));
        Imgurl = String.valueOf(getIntent().getSerializableExtra("Imgurl"));
        Log.v("img1",Imgurl);
        mShared_common_pref = new Shared_Common_Pref(this);

        txtOrderID = findViewById(R.id.product_order_id);
        txtAmount = findViewById(R.id.product_amnt);
        txtStockist_Name = findViewById(R.id.product_distributor);
        txtUTRNumber = findViewById(R.id.product_number);
        imgView = findViewById(R.id.bank_receipt);

        txtOrderID.setText(OrderID);
        txtAmount.setText(Amount);
        txtStockist_Name.setText(Stockist_Name);
        txtUTRNumber.setText(UTRNumber);

        context= imgView.getContext();
        RequestOptions myOptions = new RequestOptions()
                .fitCenter() // or centerCrop
                .override(100, 100);

        Glide.with(context)
                .asBitmap()
                .apply(myOptions)
                .load(Imgurl)
                .into(imgView);

        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                view.bringToFront();
                viewTransformation(view, event);
                return true;
            }
        });

      //  imgView.setImageURI(Uri.parse(Imgurl));

    }

    public void Cancel(View v) {
        startActivity(new Intent(PendingVerificationDetails.this,PendingVerification.class));
        finish();
    }

    public void Verify(View v) {
        verify();
    }


    public void verify() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> ca = apiInterface.getPayVerification(OrderID, mShared_common_pref.getvalue(Shared_Common_Pref.Sf_Code));

        Log.v("Product_Request", ca.request().toString());
        ca.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
              startActivity(new Intent(PendingVerificationDetails.this,PendingVerification.class));
                finish();
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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
}