package com.saneforce.dms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saneforce.dms.model.DispatchModel;
import com.saneforce.dms.R;

import java.util.List;

public class PartialDispatchAdapter extends ArrayAdapter<DispatchModel> {
    private Context context;
    private List<DispatchModel> userList;

    public PartialDispatchAdapter(Context context, int resource, List<DispatchModel> objects) {
        super(context, resource, objects);
        userList = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_partial_edit, parent, false);
        TextView tv_product_name = rowView.findViewById(R.id.tv_product_name);
        TextView tv_order = rowView.findViewById(R.id.tv_order);
        TextView tv_dispatch = rowView.findViewById(R.id.tv_dispatch);
        TextView tv_pending = rowView.findViewById(R.id.tv_pending);
        DispatchModel dispatchModel = userList.get(position);

        tv_product_name.setText(dispatchModel.getProductName());
        tv_order.setText(dispatchModel.getTotal_ordered());
        tv_dispatch.setText(dispatchModel.getTotal_dipatched());
        tv_pending.setText(dispatchModel.getTotal_pending());

        return rowView;
    }

}
