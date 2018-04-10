package com.example.ramsey.myCloud;

import android.content.Context;
import android.content.Intent;
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
 * Created by hello on 2018/4/3.
 */

public class sActionAdapter extends RecyclerView.Adapter<sActionAdapter.ViewHolder>
{
    private Context mContext;
    private List<sAction> mActionList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView sActionAction;
        TextView sActionSection;
        ImageView sFeedbackImage;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            sActionAction = (TextView) view.findViewById(R.id.saction_action);
            sActionSection = (TextView) view.findViewById(R.id.saction_section);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public sActionAdapter(List<sAction> actionList) {
        mActionList = actionList;
    }
    public sActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.saction_item, parent, false);
        final sActionAdapter.ViewHolder holder = new sActionAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                sAction saction = mActionList.get(position);
                String action_uid=saction.getUid();
                ActionActivity.ActionActivityStart(mContext,action_uid);
            }
        });
        return  holder;
    }
    public void onBindViewHolder(sActionAdapter.ViewHolder holder, int position) {
        sAction saction = mActionList.get(position);
        holder.sActionAction.setText(saction.getSolution());
        holder.sActionSection.setText(saction.getSection());
        Glide.with(mContext).load(saction.getFeedback_image_url())
                .error(R.drawable.ic_error_black_24dp)
                .placeholder(R.drawable.ic_loading)
                .override(100,100)
                .into(holder.sFeedbackImage);
    }
    public int getItemCount() {
        return mActionList.size();
    }

}