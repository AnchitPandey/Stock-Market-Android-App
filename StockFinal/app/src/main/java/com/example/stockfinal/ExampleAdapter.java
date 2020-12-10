package com.example.stockfinal;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExampleAdapter extends  RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>{
    private ArrayList<ExampleItem> mExampleList;
    private  OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick (int position);

    }
    public void setOnItemClickListener (OnItemClickListener listener){

        mListener =listener;
    }


    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        public ImageView mTrendImage, mRightImage, mTrendImagePositive;
        public TextView tickerSymbol, shares, lastP, chang, changPositive, changZero;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTrendImage = itemView.findViewById(R.id.trendingIcon);
            mRightImage = itemView.findViewById(R.id.rightArrowIcon);
            tickerSymbol = itemView.findViewById(R.id.ticker);
            shares = itemView.findViewById(R.id.shares);
            lastP = itemView.findViewById(R.id.lastPrice);
            chang = itemView.findViewById(R.id.change);
            changPositive = itemView.findViewById(R.id.changePositive);
            changZero = itemView.findViewById(R.id.changeZero);
            mTrendImagePositive = itemView.findViewById(R.id.trendingIconPositive);

            /*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                    {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(position);
                    }
                }
            });
            */


            mRightImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Toast.makeText(view.getContext(), "I am being clicked", Toast.LENGTH_LONG).show();
                    if (listener != null)
                    {

                        // Toast.makeText(view.getContext(), "I am being clicked", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent (view.getContext(), MainActivity2.class);
                        intent.putExtra("ticker", tickerSymbol.getText());

                        Activity origin = (Activity)view.getContext();
                        origin.startActivity(intent);
                        //origin.startActivityForResult(intent,2);

                        //(Activity)view.getContext().startActivityForResult(intent, 2);


                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(position);
                    }
                    else
                        Toast.makeText(view.getContext(), "Listener is null !", Toast.LENGTH_LONG).show();

                }
            });

        }
    }

    public ExampleAdapter (ArrayList<ExampleItem> exampleList) {
        mExampleList = exampleList;

    }



    @Override
    public ExampleViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item,parent,false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ExampleItem currentItem = mExampleList.get(position);
        if (Float.parseFloat(currentItem.getChange()) > 0)
        {
            holder.mTrendImage.setVisibility(View.GONE);
            holder.chang.setVisibility(View.GONE);
            holder.changZero.setVisibility(View.GONE);

        }
        else if (Float.parseFloat(currentItem.getChange()) < 0)
        {
            holder.mTrendImagePositive.setVisibility(View.GONE);
            holder.changPositive.setVisibility(View.GONE);
            holder.changZero.setVisibility(View.GONE);
        }
        else
        {
            holder.mTrendImage.setVisibility(View.GONE);
            holder.mTrendImagePositive.setVisibility(View.GONE);
            holder.chang.setVisibility(View.GONE);
            holder.changPositive.setVisibility(View.GONE);
        }



        //holder.mTrendImage.setImageResource(currentItem.getmTrendingImageResource());
        //holder.mRightImage.setImageResource(R.id.rightArrowIcon);
        holder.mRightImage.setImageResource(R.drawable.right_button_icon);
        //holder.mRightImage.setImageResource(currentItem.getmRightButtonImageResource());
        holder.tickerSymbol.setText(currentItem.getTicker());
        //holder.chang.setText(currentItem.getChange());
        holder.lastP.setText(currentItem.getLastPrice());
        holder.shares.setText(currentItem.getnumShares());

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
        //        return 0;
    }
}
