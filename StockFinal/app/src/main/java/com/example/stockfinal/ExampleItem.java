package com.example.stockfinal;

public class ExampleItem {

    private int mTrendingImageResource, mRightButtonImageResource;
    private String lastPrice, numShares, change, ticker;

    public ExampleItem(int mTrendingImageResource, int mRightButtonImageResource, String lastPrice, String numShares, String change, String ticker)
    {
        this.mRightButtonImageResource = mRightButtonImageResource;
        this.mTrendingImageResource = mTrendingImageResource;
        this.lastPrice = lastPrice;
        this.numShares = numShares;
        this.change = change;
        this.ticker = ticker;
    }

    public int getmTrendingImageResource ()
    {
        return mTrendingImageResource;
    }

    public int getmRightButtonImageResource ()
    {
        return mRightButtonImageResource;
    }

    public String getLastPrice ()
    {
        return lastPrice;
    }
    public String getnumShares()
    {
        return numShares;
    }
    public String getChange(){
        return change;
    }

    public String getTicker(){
        return ticker;
    }

}
