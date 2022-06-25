package com.example.sandms.BulkSales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sandms.Adapter.DMSListItem;
import com.example.sandms.Adapter.ListItems;
import com.example.sandms.Adapter.OrderItem;
import com.example.sandms.BulkSales.Database.DatabaseHandler;
import com.example.sandms.Interface.onDMSListItemClick;
import com.saneforce.dms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.CALL_PHONE;

public class OrderIntend extends AppCompatActivity {
    private static final String TAG = "OrderIntend";
    DatabaseHandler db;
    JSONArray Prods;
    RecyclerView mRecyclerView,mUOMRecyl;
    TextView txCustName,txCustMob;
    LinearLayout btnCallMob;
    RelativeLayout rlayUOM;
    Button btnSubmit;
    String CustName,CustMob;
    EditText txSearch;
    OrderItem listItms;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_intend);
        getWindow().getAttributes().windowAnimations = R.style.Fade;
        CustName="";CustMob="";
        if (getIntent().getExtras() != null) {
            Bundle params = getIntent().getExtras();
            CustName = params.getString("CustName");
            CustMob = params.getString("CustMob");
        }
        txCustName=findViewById(R.id.CustName);
        txCustMob=findViewById(R.id.CustMobile);
        txCustName.setText(CustName);
        txCustMob.setText(CustMob);

        txSearch=findViewById(R.id.txSearch);

        rlayUOM=findViewById(R.id.rlayUOM);
        rlayUOM.setVisibility(View.GONE);
        mUOMRecyl=findViewById(R.id.rcylUOM);
        mUOMRecyl.setHasFixedSize(true);
        LinearLayoutManager uomlayoutManager = new LinearLayoutManager(this);
        mUOMRecyl.setLayoutManager(uomlayoutManager);

        mRecyclerView = findViewById(R.id.OrderList);
        mRecyclerView.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        db = new DatabaseHandler(this);
        try {
            Prods=db.getMasterData("DMSProducts");
            listItms= new OrderItem(Prods,this);
            mRecyclerView.setAdapter(listItms);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btnSubmit=findViewById(R.id.btnSubmit);
        btnCallMob=findViewById(R.id.btnCallMob);
        btnCallMob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int readReq = ContextCompat.checkSelfPermission(OrderIntend.this, CALL_PHONE);
                if (readReq != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OrderIntend.this, new String[]{CALL_PHONE}, REQUEST_PERMISSIONS_REQUEST_CODE);

                } else{
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + txCustMob.getText()));//change the number
                    startActivity(callIntent);
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,String.valueOf(listItms.getItems()));

            }
        });
        txSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//
//                JSONArray itmd= listItms.getItems();
//                for(int il=0;il<itmd.length();il++){
//                    try {
//                        if(itmd.getJSONObject(il).getString("name").indexOf(txSearch.getText().toString())<0) {
//                            if(mRecyclerView.findViewHolderForAdapterPosition(il).itemView!=null) {
//                                mRecyclerView.findViewHolderForAdapterPosition(il).itemView.setVisibility(View.GONE);
//                                mRecyclerView.findViewHolderForAdapterPosition(il).itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
                listItms.searchItems(txSearch.getText().toString());
            }
        });
        OrderItem.SetListItemClickListener(new onDMSListItemClick() {
            @Override
            public void showUOM(JSONArray items,String BUOM, Integer parentPos) {

                ListItems listUOMItms= new ListItems(items,BUOM,parentPos,OrderIntend.this);
                mUOMRecyl.setAdapter(listUOMItms);
                rlayUOM.setVisibility(View.VISIBLE);

            }
        });

        ListItems.SetListItemClickListener(new onDMSListItemClick() {
            @Override
            public void onClick(JSONObject item,Integer ParentPosition) {
                try {
                    listItms.setUOM(ParentPosition,item);
                    rlayUOM.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {

                }
        }
    }
}

/*
        int readStReq = ContextCompat.checkSelfPermission(_context, READ_EXTERNAL_STORAGE);

        return locationReq == PackageManager.PERMISSION_GRANTED && cameraReq == PackageManager.PERMISSION_GRANTED &&
                coarseReq == PackageManager.PERMISSION_GRANTED && wrteStReq == PackageManager.PERMISSION_GRANTED &&
                readStReq == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean LoactionAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean CorseAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted && cameraAccepted && ReadAccepted && LoactionAccepted && CorseAccepted ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        }

                    }
                }

        }
    }

*/