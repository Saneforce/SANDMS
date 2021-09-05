package com.example.sandms.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ListItems  extends RecyclerView.Adapter<ListItems.ViewHolder> {
    private static final String TAG = "OrderItem";
    private JSONArray mlist = new JSONArray();
    private String BUOM;
    private Integer ParentPosition;
    private Context mContext;
    static onDMSListItemClick payClick;

    public ListItems(JSONArray mlist,String bUOM,Integer ParentPosition, Context mContext) {
        this.BUOM=bUOM;
        this.ParentPosition=ParentPosition;
        this.mlist = mlist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ListItems.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dms_row_item, parent, false);
        ListItems.ViewHolder holder = new ListItems.ViewHolder(view);
        return holder;

    }

    public static void SetListItemClickListener(onDMSListItemClick mPayClick) {
        payClick = mPayClick;
    }

    @Override
    public void onBindViewHolder(@NonNull ListItems.ViewHolder holder, int position) {

        JSONObject itm = null;
        try {
            itm = mlist.getJSONObject(position);
            holder.txItmName.setText(itm.getString("name"));
            holder.txQty.setText(itm.getString("ConQty"));

            holder.txUOMDet.setText("1 "+itm.getString("name")+" = "+itm.getString("ConQty")+" "+BUOM);

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    JSONObject itm = null;
                    try {
                        itm = mlist.getJSONObject(position);
                        if (payClick != null) payClick.onClick(itm,ParentPosition);
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
                    Log.d(TAG, "text :" + holder.txQty.getText());
                    try {
                        mlist.getJSONObject(position).put("Qty", holder.txQty.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONArray getItems() {
        return mlist;
    }

    @Override
    public int getItemCount() {

        return mlist.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txItmName, txQty, txUOMDet;
        LinearLayout parentLayout;

        //CardView secondarylayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txItmName = itemView.findViewById(R.id.itmName);
            txQty = itemView.findViewById(R.id.itmCqty);
            txUOMDet = itemView.findViewById(R.id.UOMDet);

            //lblStatus = itemView.findViewById(R.id.itml1c1);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            //secondarylayout=itemView.findViewById(R.id.secondary_layout);


        }
    }
}