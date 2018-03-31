package com.example.ramsey.myCloud;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by young on 2018/3/11.
 */

public class sQuestionAdapter extends RecyclerView.Adapter<sQuestionAdapter.ViewHolder> {

    private Context mContext;

    private List<sQuestion> mQuestionList;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView sQuestionId;
        TextView sQuestionTitle;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            sQuestionId = (TextView) view.findViewById(R.id.squestion_id);
            sQuestionTitle=(TextView) view.findViewById(R.id.squestion_title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                }
            });

        }
    }

    public sQuestionAdapter(List<sQuestion> questionList) {
        mQuestionList = questionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.squestion_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                sQuestion squestion = mQuestionList.get(position);
                Intent intent = new Intent(mContext, QuestionActivity.class);
                intent.putExtra(QuestionActivity.Question_Summary, squestion.getTitle());
                mContext.startActivity(intent);
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        sQuestion squestion = mQuestionList.get(position);
        holder.sQuestionId.setText(squestion.getUnique_Id());
        holder.sQuestionTitle.setText(squestion.getTitle());
       switch(squestion.getLevel()){
           case ("A"):
               holder.cardView.setCardBackgroundColor(Color.parseColor("#FF4081"));
               break;
           case("B"):
               holder.cardView.setCardBackgroundColor(Color.parseColor("#428bca"));
               break;
           case("C") :
               holder.cardView.setCardBackgroundColor(Color.parseColor("#26ae90"));
               break;
           case("D"):
               holder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
               break;
           default:
               break;
       }
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size();
    }
}