package com.android.travelmantics.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.travelmantics.DealActivity;
import com.android.travelmantics.R;
import com.android.travelmantics.TravelDeal;
import com.android.travelmantics.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RvRowAdapter extends RecyclerView.Adapter<RvRowAdapter.RvRowHolder> {

    private ArrayList<TravelDeal> travelDeals;
    private Context context;

    public RvRowAdapter(Context context, ArrayList<TravelDeal> travelDeals) {
        this.context = context;
        this.travelDeals = travelDeals;
    }

    @NonNull
    @Override
    public RvRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_row, parent, false);
        return new RvRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvRowHolder holder, int position) {
        holder.bindData(travelDeals.get(position));
    }

    @Override
    public int getItemCount() {
        return travelDeals.size();
    }


    class RvRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleText;
        private TextView mDescriptionText;
        private TextView mPriceText;
        private ImageView mImageDeal;

        RvRowHolder(@NonNull View itemView) {
            super(itemView);
            this.mTitleText = itemView.findViewById(R.id.tvTitle);
            this.mDescriptionText = itemView.findViewById(R.id.tvDescription);
            this.mPriceText = itemView.findViewById(R.id.tvPrice);
            this.mImageDeal = itemView.findViewById(R.id.imageDeal);

            itemView.setOnClickListener(this);
        }

        void bindData(TravelDeal travelDeal) {
            this.mTitleText.setText(travelDeal.getTitle());
            this.mDescriptionText.setText(travelDeal.getDescription());
            this.mPriceText.setText(travelDeal.getPrice());
            ImageUtils.loadImage(travelDeal.getImageUrl(), mImageDeal, 80, 80);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TravelDeal travelDeal = travelDeals.get(position);
            Intent intent = new Intent(context, DealActivity.class);
            intent.putExtra("deal", travelDeal);
            v.getContext().startActivity(intent);
        }
    }
}
