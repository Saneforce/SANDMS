package com.saneforce.dms.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saneforce.dms.adapter.DataAdapter;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.R;

import java.util.List;

public class CustomListViewDialog extends Dialog implements View.OnClickListener, DMS.UpdateUi {

    public CustomListViewDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public CustomListViewDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public Context context;
    public Activity activity;
    EditText searchView;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    //RecyclerView.Adapter adapter;
    public DataAdapter da;
    int type;
    List<Common_Model> mDataset;
    public Button no;
    TextView productdata;

    public CustomListViewDialog(Activity a, List<Common_Model> wk, int type) {
        super(a);
        this.activity = a;
        this.context = a;
        this.type = type;
        //this.adapter = adapter;
        this.mDataset = wk;

        this.da = new DataAdapter(mDataset, activity, type);

    }

    public CustomListViewDialog(Context applicationContext, List<Common_Model> productCodeOffileData, int i) {
        super(applicationContext);

        this. context= applicationContext;
        this.type = i;
        //this.adapter = adapter;
        this.mDataset =productCodeOffileData;


        this.da = new DataAdapter(mDataset, activity, type);
    }
    public CustomListViewDialog(Context aa, List<Common_Model> wk, int type, TextView productdata) {
        super(aa);
        this.context= aa;
        this.type = type;
        //this.adapter = adapter;
        this.mDataset = wk;
        this.productdata=productdata;

        this.da = new DataAdapter(mDataset, activity, type);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_layout);
        no = (Button) findViewById(R.id.no);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        no.setOnClickListener(this);
        recyclerView.setAdapter(da);
        da.notifyDataSetChanged();
        searchView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                da.getFilter().filter(s);
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }

                return false;
            }
        });
    }

    private void performSearch() {
        searchView.clearFocus();
        InputMethodManager in = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @Override
    public void update(int value, int pos) {
        Log.e("Custom_Dialog_Calling", "");
        dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.no:
                dismiss();
                break;
            default:
                break;
        }

    }
}