package com.example.stockfinal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context mContext;
    private ArrayList<News> mExampleList;
    private  OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick (int position);
    }

    public void setOnItemClickListener (OnItemClickListener listener){
        mListener = listener;
    }
    public NewsAdapter (Context context, ArrayList<News> exampleList)
    {
        mContext = context;
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from (mContext).inflate (R.layout.news_item, parent, false);
        return new NewsViewHolder(v);
        //    return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News currentItem = mExampleList.get(position);
        String imageUrl = currentItem.getUrlToImage();
        String publisherName= currentItem.getPublisher();
        String daysGone = currentItem.getPublishedAt();
        String title = currentItem.getTitle();
        String url  = currentItem.getUrl();
        holder.mTitle.setText(title);
        holder.mPublisher.setText(publisherName);
        holder.mDate.setText(daysGone);
        holder.mUrl.setText(url);
        Picasso.with(mContext).load (imageUrl).fit().centerInside().into (holder.mImageView);


    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mPublisher;
        public TextView mDate;
        public TextView mTitle;
        public TextView mUrl;
        public TextView mDescription;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.newsImage);
            mPublisher = itemView.findViewById(R.id.publisher);
            mDate = itemView.findViewById(R.id.daysAgo);
            mTitle = itemView.findViewById(R.id.newsTitle);
            mUrl = itemView.findViewById(R.id.newsUrl);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                    {

                        String urlString = mUrl.toString();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            // Chrome browser presumably not installed so allow user to choose instead
                            intent.setPackage(null);
                            mContext.startActivity(intent);
                        }
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                        {
                            mListener.onItemClick (position);
                        }
                    }
                }
            });
        }
    }
}
