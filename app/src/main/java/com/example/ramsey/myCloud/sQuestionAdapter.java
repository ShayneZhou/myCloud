package com.example.ramsey.myCloud;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by young on 2018/3/11.
 */

public class sQuestionAdapter extends RecyclerView.Adapter<sQuestionAdapter.ViewHolder> {

    private Context mContext;

    private List<sQuestion> mQuestionList;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView sQuestionPositionNumber;
        TextView sQuestionTitle;
        TextView sQuestionCreatedAt;
        ImageView sQuestionImage;
        private LruCache<String, BitmapDrawable> mImageCache;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            sQuestionPositionNumber = (TextView) view.findViewById(R.id.squestion_position_num);
            sQuestionTitle = (TextView) view.findViewById(R.id.squestion_title);
            sQuestionCreatedAt = (TextView) view.findViewById(R.id.squestion_created_at);
            sQuestionImage = (ImageView)  view.findViewById(R.id.example_image_url);
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
                Intent intent = new Intent(mContext, ProblemDetail.class);
                intent.putExtra("prob_uid",squestion.getProbUid());
                mContext.startActivity(intent);
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        sQuestion squestion = mQuestionList.get(position);
        Glide.with(mContext).load(squestion.getExampleImageUrl()).into(holder.sQuestionImage);
        holder.sQuestionCreatedAt.setText("创建日期： " + squestion.getCreatedAt());
        holder.sQuestionTitle.setText(squestion.getTitle());
        holder.sQuestionPositionNumber.setText("工位： " + squestion.getPositionNumber());
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