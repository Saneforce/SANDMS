package com.example.sandms.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Interface.onDMSListItemClick;
import com.example.sandms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderItem  extends RecyclerView.Adapter<OrderItem.ViewHolder> {
    private static final String TAG = "OrderItem";
    private JSONArray mlist = new JSONArray();
    private Context mContext;
    static onDMSListItemClick payClick;
    public OrderItem(JSONArray mlist, Context mContext) {
        this.mlist = mlist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OrderItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dms_order_item, parent, false);
        OrderItem.ViewHolder holder = new OrderItem.ViewHolder(view);
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return holder;

    }
    public static void SetListItemClickListener(onDMSListItemClick mPayClick){
        payClick=mPayClick;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItem.ViewHolder holder, int position) {

        JSONObject itm = null;
        try {
            itm = mlist.getJSONObject(position);
            holder.line1.setText(itm.getString("name"));
            holder.txUOM.setText(itm.getString("UMO"));
            if(itm.has("Qty")) holder.txQty.setText(itm.getString("Qty"));

            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if(itm.has("Visible")) {
                if (itm.getBoolean("Visible") == false) {
                    /*holder.itemView.getViewTreeObserver().addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener(){

                                @Override
                                public void onGlobalLayout() {
                                    holder.itemView.getViewTreeObserver().removeGlobalOnLayoutListener( this );
                                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                    holder.itemView.requestLayout();
                                    holder.itemView.setVisibility( View.GONE );
                                }

                            });*/
                    //
//                    (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
//                          @Override
//                          public void run() {
//                              try {
                                  //if(holder!=null){
                                  //holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                    holder.itemView.setVisibility(View.GONE);
                                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                    holder.itemView.requestLayout();
//                                  }
//                              }
//                              catch (Exception e){}
//                          }
//                    }, 1000);
                    return;
                }
            }
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject itm = null;
                    try {
                        itm = mlist.getJSONObject(position);
                        if(payClick!=null) payClick.onClick(itm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.txQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(TAG,"text :" +holder.txQty.getText());
                    try {
                        mlist.getJSONObject(holder.getBindingAdapterPosition()).put("Qty",holder.txQty.getText());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.linUMO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONArray itms = null;
                    try {
                        itms = mlist.getJSONObject(position).getJSONArray("UOMList");
                        String bUOM=mlist.getJSONObject(position).getString("product_unit");
                        if(payClick!=null) payClick.showUOM(itms,bUOM,position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void searchItems(String schStr){
        try {
            for (int ik = 0; ik < mlist.length(); ik++) {
                JSONObject itm = mlist.getJSONObject(ik);
                String ItemName = itm.getString("name");
                mlist.getJSONObject(ik).put("Visible", false);
                if (ItemName.toLowerCase().indexOf(schStr.toLowerCase()) > -1 || schStr.equalsIgnoreCase("")) {
                    mlist.getJSONObject(ik).put("Visible", true);

                }
            }
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void setUOM(Integer position,JSONObject UOM){
        try {
            mlist.getJSONObject(position).put("UMO", UOM.getString("name")+" ( 1 x "+UOM.getString("ConQty")+" )");
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONArray getItems(){
        return mlist;
    }
    @Override
    public int getItemCount() {

        return mlist.length();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView line1,line2,line3,lblStatus,txUOM;
        EditText txQty;
        LinearLayout parentLayout,linUMO,RootLay;

        //CardView secondarylayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            line1 = itemView.findViewById(R.id.itml1);
            txQty = itemView.findViewById(R.id.txQty);
            txUOM = itemView.findViewById(R.id.txUMO);
            line2 = itemView.findViewById(R.id.itml2);
            line3 = itemView.findViewById(R.id.itml3);
            linUMO=itemView.findViewById(R.id.linUMO);

            lblStatus= itemView.findViewById(R.id.itml1c1);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            RootLay=itemView.findViewById(R.id.pProduct);
            //itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            //secondarylayout=itemView.findViewById(R.id.secondary_layout);


        }
    }
}
