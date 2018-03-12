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

import java.util.List;

/**
 * Created by young on 2018/3/11.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private static final String TAG = "QuestionAdapter";

    private Context mContext;

    private List<Question> mQuestionList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView QuestionSummary;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            QuestionSummary = (TextView) view.findViewById(R.id.question_summary);
        }
    }

    public QuestionAdapter(List<Question> questionList) {
        mQuestionList = questionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Question question = mQuestionList.get(position);
                Intent intent = new Intent(mContext, QuestionActivity.class);
                intent.putExtra(QuestionActivity.Question_Summary, question.getSummary());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question question = mQuestionList.get(position);
        holder.QuestionSummary.setText(question.getSummary());
    }

    @Override
    public int getItemCount() {
        return mQuestionList.size();
    }

}
