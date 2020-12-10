package com.example.stockfinal;

public class Portfolio {
    public String ticker;
    float lastPrice;
    float change;
    int sharesOwned;
    public Portfolio (String ticker, float lastPrice, int sharesOwned, float change)
    {
        this.ticker = ticker;
        this.lastPrice = lastPrice;
        this.sharesOwned = sharesOwned;
        this.change = change;
    }
    public String getTickerName() {
        return this.ticker;
    }
    public float getLastPrice () {
        return this.lastPrice;
    }
    public float getChange() {
        return this.change;
    }
    public int getNumShares () {
        return this.sharesOwned;
    }
}
