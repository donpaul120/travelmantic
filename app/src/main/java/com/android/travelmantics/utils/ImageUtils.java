package com.android.travelmantics.utils;

import android.content.res.Resources;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageUtils {

    public static void loadImage(String url, ImageView imageView, int width, int height) {
        if (url != null && !url.isEmpty()) {
            int nWidth = width;
            int nHeight = height;
            if (nWidth == 0) {
                nWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                nHeight = width * 2 / 3;
            }
            Picasso.get()
                    .load(url)
                    .resize(nWidth, nHeight)
                    .centerCrop()
                    .into(imageView);
        }
    }
}
