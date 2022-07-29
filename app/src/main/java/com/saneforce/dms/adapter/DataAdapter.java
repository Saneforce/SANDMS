package com.saneforce.dms.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saneforce.dms.activity.AddNewRetailer;
import com.saneforce.dms.activity.FinanceActivity;
import com.saneforce.dms.activity.PaymentVerified;
import com.saneforce.dms.listener.DMS;
import com.saneforce.dms.R;
import com.saneforce.dms.utils.Common_Model;
import com.saneforce.dms.utils.CustomListViewDialog;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.FruitViewHolder> implements Filterable {
    List<Common_Model> contactList;
    DMS.Master_Interface updateUi;
    int typeName;
    private List<Common_Model> contactListFiltered;
    Context context;

    public DataAdapter(List<Common_Model> myDataset, Context context, int type) {
        contactList = myDataset;
        typeName = type;
        contactListFiltered = myDataset;
        updateUi = ((DMS.Master_Interface) context);
        context=this.context;

    }
    public DataAdapter(List<Common_Model> myDataset,Context context){
        contactList=myDataset;
        this.context=context;
        contactListFiltered = myDataset;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinnear_search_item, parent, false);
        FruitViewHolder vh = new FruitViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FruitViewHolder fruitViewHolder, int i) {
        Common_Model contact = contactListFiltered.get(i);
        fruitViewHolder.mTextName.setText(contact.getName());
        fruitViewHolder.mTextName.setText(contact.getId());

        if(contact.getAddress()!=null && !contact.getAddress().equals("")){
            fruitViewHolder.tv_address.setText(contact.getAddress());
            fruitViewHolder.tv_address.setVisibility(View.VISIBLE);
        }
        else
            fruitViewHolder.tv_address.setVisibility(View.GONE);

        if(contact.getFlag()!=null && !contact.getFlag().equals("") && !contact.getFlag().equalsIgnoreCase("flag")){
            fruitViewHolder.tv_scheme.setText(contact.getFlag());
            fruitViewHolder.tv_scheme.setVisibility(View.VISIBLE);

        }
        else {
            fruitViewHolder.tv_scheme.setVisibility(View.GONE);
            fruitViewHolder.ib_edit.setVisibility(View.GONE);
            new View.OnClickListener() {



                @Override
                public void onClick(View view) {

                }
            };
        }


    }

    @Override
    public int getItemCount() {
       return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {

                    List<Common_Model> filteredList = new ArrayList<>();
                    for (Common_Model row : contactList) {
                        if (row.getName().toLowerCase().trim().replaceAll("\\s", "").contains(charString.toLowerCase().trim().replaceAll("\\s", ""))) {
                            filteredList.add(row);
                        }
                    }
                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Common_Model>) filterResults.values;
                Log.e("FILTERED_RESULT", String.valueOf(contactListFiltered.size()));
                notifyDataSetChanged();
            }
        };

    }

    public class FruitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextName, mTextPhone,mTextAddress, tv_address, tv_scheme;
        ImageButton ib_edit;

        public FruitViewHolder(View v) {
            super(v);
            mTextName = v.findViewById(R.id.txt_name);
            mTextPhone = v.findViewById(R.id.txt_phone);
            mTextAddress = v.findViewById(R.id.txt_address);
            tv_address = v.findViewById(R.id.tv_address);
            tv_scheme = v.findViewById(R.id.tv_scheme);
            ib_edit=v.findViewById(R.id.ib_edit);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(updateUi!=null){
                updateUi.OnclickMasterType(contactListFiltered, this.getAdapterPosition(), typeName);
            }else {
                Intent intent =new Intent(context,AddNewRetailer.class);
                intent.putExtra("retailerId",contactListFiltered.get(this.getAbsoluteAdapterPosition()).getId());
                context.startActivity(intent);



            }
        }
    }

    public static boolean isNullOrEmpty(String str) {
        if (str != null && !str.isEmpty())
            return false;
        return true;
    }

}
