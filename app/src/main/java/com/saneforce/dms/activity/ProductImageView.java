package com.saneforce.dms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;


import com.saneforce.dms.R;
import com.squareup.picasso.Picasso;

public class ProductImageView extends Activity {

    ImageView ProductZoomImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_product_image_view);
        ProductZoomImage = findViewById(R.id.product_image);

        Intent intent = getIntent();
        String ImageUrl = intent.getStringExtra("ImageUrl");
        Log.e("ImageView",ImageUrl);
        Picasso.with(this)
                .load(ImageUrl)
                .error(R.drawable.icon_placeholder)
                .into(ProductZoomImage);
    }
}