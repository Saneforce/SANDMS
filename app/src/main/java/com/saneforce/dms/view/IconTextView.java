package com.saneforce.dms.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class IconTextView {

    public class IconTextview extends androidx.appcompat.widget.AppCompatTextView {


        public IconTextview(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        public IconTextview(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public IconTextview(Context context) {
            super(context);
            init();
        }

        private void init() {

            //Font name should not contain "/".
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "IcoMoon-Free.ttf");
            setTypeface(tf);
        }
    }
}
