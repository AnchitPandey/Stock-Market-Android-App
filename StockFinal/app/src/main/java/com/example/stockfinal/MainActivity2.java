package com.example.stockfinal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


class MySpannable extends ClickableSpan {

    private boolean isUnderline = true;

    /**
     * Constructor
     */
    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(isUnderline);
        ds.setColor(Color.parseColor("#1b76d3"));
    }

    @Override
    public void onClick(View widget) {

    }
}


public class MainActivity2 extends AppCompatActivity {


    private TextView mTextView;
    private Menu mOptionsMenu;
    private ExampleItem item;
    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    Button b;
    TextView aboutTV;
    News firstNewsItem;
     Context context = this;
    Button tradeButton;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String portfolioString = "portfolioString";
    public static final String favoriteString = "favoriteString";
    public static final String userAmount = "userAmount";
    ArrayList<ExampleItem> portfolioItemList, favoriteItemList;
    ArrayList<News> newsList;
    boolean favEl = false;
    String currTicker;
    Menu mena;
    private RequestQueue mRequestQueue;
    ExampleItem currentItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolBarSecondActivity);
        myToolbar.setNavigationIcon(R.drawable.back_button);



        //mRecyclerView = findViewById(R.id.newsRecycler);
        //mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        portfolioItemList = new ArrayList<>();
        favoriteItemList = new ArrayList<>();
        newsList= new ArrayList<>();

        tradeButton = (Button)findViewById(R.id.activity2_tradeButton);

        myToolbar.setTitle("Stocks");
        setSupportActionBar(myToolbar);

        //aboutTV = findViewById(R.id.activity2_aboutText);
        //String trash = "Some Textsdjsdjsdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd\n" +
        //      "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd";

//        aboutTV.setText(trash);
        //      makeTextViewResizable(aboutTV, 1,"Show More", true);

        // TODO: Make API call to back end - pass currTicker & initialize currentElement;


        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        currTicker = getIntent().getStringExtra("ticker");

        Gson gson = new Gson();
        String jsonPortFolio = sharedPreferences.getString(portfolioString, "");


        Type type = new TypeToken<ArrayList< ExampleItem >>() {}.getType();
        if (jsonPortFolio.length () >2)
            portfolioItemList = new Gson().fromJson(jsonPortFolio, type);


        String jsonFavorites = sharedPreferences.getString(favoriteString, "");

        if (jsonFavorites.length () >2)
            favoriteItemList = new Gson().fromJson(jsonFavorites, type);



        for (int i =0 ; i< favoriteItemList.size();i+=1)
        {

            ExampleItem el = favoriteItemList.get(i);

            if (currTicker.equals(el.getTicker()))
            {
                currentItem = el;
                favEl = true;
                break;
            }
        }

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                String connectionsJSONString = new Gson().toJson(portfolioItemList);
                editor.putString(portfolioString, connectionsJSONString);
                editor.commit();
                connectionsJSONString = new Gson().toJson(favoriteItemList);
                editor.putString(favoriteString, connectionsJSONString);
                editor.commit();
                finish();
            }
        });


        //parseJSON();


        tradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               final Dialog dialog = new Dialog(context);

                dialog.setContentView(R.layout.layout_dialog);
                //dialog.setTitle("Title...");

                //TextView sharesText =  dialog.findViewById(R.id.shares);
                //TextView calculationText = dialog.findViewById(R.id.calculationText);
                //sharesText.setText("shares");
                //EditText t1 = dialog.findViewById(R.id.tet1);
                //calculationText.setText("120 x 4 / share");
                TextView title1 = dialog.findViewById(R.id.titleText);
                title1.setText ("Trade Microsoft Corporation shares");
                TextView dialog_shares = dialog.findViewById(R.id.shares);
                dialog_shares.setText("shares");
                TextView dialog_cal = dialog.findViewById(R.id.calculationText);
                TextView calcText = dialog.findViewById(R.id.calcText);
                //TextView amountLeft = dialog.findViewById(R.id.amountLeft);
                Button buy = dialog.findViewById(R.id.buyButton);
                Button sell = dialog.findViewById(R.id.sellButton);
                EditText edit = dialog.findViewById(R.id.inputNumber);

                buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                sell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                dialog.show ();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_not_main, menu);
        mena=  menu;
        if (favEl)
            menu.findItem(R.id.star_icon_image).setIcon(R.drawable.star_filled);
        else
            menu.findItem(R.id.star_icon_image).setIcon(R.drawable.star_empty);
        return true;
    }


    private void parseJSON()
    {
        String url=  "http://localhost:8080/news?keyword=msft";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i =0 ; i< response.length();i+=1)
                {
                    try {


                        JSONObject obj = response.getJSONObject(i);
                        String url = obj.getString("url");
                        String publisher = obj.getString("publisher");
                        String publishedAt = obj.getString("publishedAt");
                        String description = obj.getString("description");
                        String urlToImage = obj.getString("urlToImage");
                        String title = obj.getString ("title");
                        News n = new News(url, urlToImage, publisher, publishedAt,title, description);
                        if (i ==0)
                            firstNewsItem = n;
                        else
                            newsList.add(n);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mNewsAdapter = new NewsAdapter(MainActivity2.this,newsList);
                mRecyclerView.setAdapter(mNewsAdapter);
                Toast.makeText(getApplicationContext(), "Size of list is "+ newsList.size(), Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
mRequestQueue.add(request);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.star_icon_image)
        {
            // if that stock is not in favorite list, then add it
            if (!favEl)
            {
                favoriteItemList.add(currentItem);
                favEl = true;
                mena.findItem(R.id.star_icon_image).setIcon(R.drawable.star_filled);
                return true;
            }
            // else remove it from favorite list
            else
            {
                for (int i =0; i<favoriteItemList.size();i+=1)
                {
                    if (favoriteItemList.get(i).getTicker().equals (currTicker))
                    {
                        favoriteItemList.remove(i);
                        favEl = false;
                        mena.findItem(R.id.star_icon_image).setIcon(R.drawable.star_empty);
                        break;
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false){
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, ".. See More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
        }
        return ssb;
    }
}