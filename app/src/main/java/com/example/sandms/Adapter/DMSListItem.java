package com.example.sandms.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandms.Interface.onDMSListItemClick;
import com.example.sandms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DMSListItem  extends RecyclerView.Adapter<DMSListItem.ViewHolder> {
    private static final String TAG = "ShiftList";
    private JSONArray mlist = new JSONArray();
    private Context mContext;
    static onDMSListItemClick payClick;
    public DMSListItem(JSONArray mlist, Context mContext) {
        this.mlist = mlist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public DMSListItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dms_list_item, parent, false);
        DMSListItem.ViewHolder holder = new DMSListItem.ViewHolder(view);
        return holder;

    }
    public static void SetListItemClickListener(onDMSListItemClick mPayClick){
        payClick=mPayClick;
    }
    @Override
    public void onBindViewHolder(@NonNull DMSListItem.ViewHolder holder, int position) {

        JSONObject itm = null;
        try {
            itm = mlist.getJSONObject(position);
            holder.line1.setText(itm.getString("Name"));
            holder.line2.setText(itm.getString("Address"));
            holder.line3.setText(itm.getString("Mobile"));

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {

        return mlist.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView line1,line2,line3,lblStatus;
        LinearLayout parentLayout;
        //CardView secondarylayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            line1 = itemView.findViewById(R.id.itml1);
            line2 = itemView.findViewById(R.id.itml2);
            line3 = itemView.findViewById(R.id.itml3);
            lblStatus= itemView.findViewById(R.id.itml1c1);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            //secondarylayout=itemView.findViewById(R.id.secondary_layout);
        }
    }

}
