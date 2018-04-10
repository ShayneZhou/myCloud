package com.example.ramsey.myCloud;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hello on 2018/4/3.
 */

public class sCauseAdapter extends RecyclerView.Adapter<sCauseAdapter.ViewHolder>
{
    private Context mContext;
    private List<sCause> mCauseList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView sCauseCause;
        TextView sCauseAnalysis;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            sCauseCause = (TextView) view.findViewById(R.id.scause_cause);
            sCauseAnalysis = (TextView) view.findViewById(R.id.scause_analysis);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public sCauseAdapter(List<sCause> causeList) {
        mCauseList = causeList;
    }
        public sCauseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mContext == null) {
                mContext = parent.getContext();
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.scause_item, parent, false);
            final sCauseAdapter.ViewHolder holder = new sCauseAdapter.ViewHolder(view);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    sCause scause = mCauseList.get(position);
                    CauseActivity.CauseActivityStart(mContext,scause.getCause_uid(),scause.getProb_uid());
                }
            });
            return  holder;
        }
        public void onBindViewHolder(sCauseAdapter.ViewHolder holder, int position) {
            sCause scause = mCauseList.get(position);
            holder.sCauseCause.setText(scause.getCause());
            holder.sCauseAnalysis.setText(scause.getAnalysis());
        }
    public int getItemCount() {
        return mCauseList.size();
    }

}
