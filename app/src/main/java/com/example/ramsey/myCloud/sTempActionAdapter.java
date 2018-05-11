package com.example.ramsey.myCloud;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by hello on 2018/5/7.
 */

public class sTempActionAdapter extends RecyclerView.Adapter<sTempActionAdapter.ViewHolder> {

    private Context mContext;

    private List<sTempAction> mTempActionList;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView sTempAction;
        TextView sTempSection;
        ImageView sTempActionImage;
        private LruCache<String, BitmapDrawable> mImageCache;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            sTempAction = (TextView) view.findViewById(R.id.stempaction);
            sTempSection = (TextView) view.findViewById(R.id.stempaction_section);
            sTempActionImage = (ImageView)  view.findViewById(R.id.stempaction_image);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                }
            });

        }
    }

    public sTempActionAdapter(List<sTempAction> tempactionList) {
        mTempActionList= tempactionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.stempaction_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                sTempAction stempaction = mTempActionList.get(position);
                TempActionActivity.TempActionStart(mContext,stempaction.getTempActionUid());
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        sTempAction stempaction = mTempActionList.get(position);
        Glide.with(mContext).load(stempaction.getFeedback_image_url()).error(R.drawable.ic_error_black_24dp)
                .placeholder(R.drawable.ic_loading).into(holder.sTempActionImage);
        holder.sTempAction.setText("临时措施： " + stempaction.getTempaction());
        holder.sTempSection.setText("工位号:  "+ stempaction.getTempsection());
        switch(stempaction.getIsdone()){
            case ("0"):
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FF4081"));
                break;
            case("1"):
                holder.cardView.setCardBackgroundColor(Color.parseColor("#428bca"));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mTempActionList.size();
    }
}