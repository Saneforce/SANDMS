package com.saneforce.dms.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.saneforce.dms.model.ParentMyOrderModel;
import com.saneforce.dms.R;

import java.util.List;

public class ParentMyOrderAdapter extends RecyclerView.Adapter<ParentMyOrderAdapter.MainMenuViewHolder> {
    private List<ParentMyOrderModel> cacheMenuRes;
    private Context context;
    ParentMyOrderModel md;

    public ParentMyOrderAdapter(List<ParentMyOrderModel> cacheMenuRes, Context context) {
        this.cacheMenuRes = cacheMenuRes;
        this.context = context;
    }

    @NonNull
    @Override
    public MainMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_parent_myorder_item, parent, false);
        return new MainMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainMenuViewHolder holder, @SuppressLint("RecyclerView") int position) {
        md = cacheMenuRes.get(position);
        holder.custName.setText(md.getSubCategoryName());

        holder.btnApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("CATEGORY_NAME", "APPROVED  " + position);


            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("CATEGORY_NAME", " REJECTED  " + position);
            }
        });


        ChildMyOrderAdapter subMenuAdapter = new ChildMyOrderAdapter(md.getChildMyOrderModels(), context);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.subMenuRecycler.setLayoutManager(layoutManager);
        holder.subMenuRecycler.setItemViewCacheSize(20);

        holder.subMenuRecycler.setAdapter(subMenuAdapter);

    }

    @Override
    public int getItemCount() {
        return cacheMenuRes.size();
    }

    public class MainMenuViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView subMenuRecycler;
        private TextView custName, prodId;
        private Button btnApproved, btnReject;

        public MainMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            subMenuRecycler = itemView.findViewById(R.id.sub_recyclerview_myoder);
            custName = itemView.findViewById(R.id.txt_customer_name);
            prodId = itemView.findViewById(R.id.txt_order_id);
            btnApproved = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
