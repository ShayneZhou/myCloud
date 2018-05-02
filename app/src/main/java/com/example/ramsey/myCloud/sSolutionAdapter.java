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

import java.util.List;

/**
 * Created by young on 2018/5/1.
 */

public class sSolutionAdapter extends RecyclerView.Adapter<sSolutionAdapter.ViewHolder> {

    private Context mContext;

    private List<sSolution> mSolutionList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView sSolution;


        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            sSolution = (TextView) view.findViewById(R.id.solution);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                }
            });

        }
    }

    public sSolutionAdapter(List<sSolution> solutionList) {
        mSolutionList = solutionList;
    }


    @Override
    public sSolutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.solution_item, parent, false);
        final sSolutionAdapter.ViewHolder holder = new sSolutionAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                sSolution solution = mSolutionList.get(position);
                holder.cardView.setCardBackgroundColor(Color.parseColor("#b71c1c"));
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(sSolutionAdapter.ViewHolder holder, int position) {
        sSolution solution = mSolutionList.get(position);
        holder.sSolution.setText(solution.getSolution());
    }

    @Override
    public int getItemCount() {
        return mSolutionList.size();
    }
}
