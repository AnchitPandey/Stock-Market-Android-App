package com.example.stockfinal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private AutoSuggestAdapter autoSuggestAdapter;
    private AppCompatAutoCompleteTextView autoCompleteTextView;
    private SearchView searchView;
    private Handler handler;
    TextView selectedText;
    TextView dateTimeText;
    String companyName = "undefined";
    //ArrayList<Portfolio> portfolioList;
    ArrayList<ExampleItem> tempPortfolioList, tempFavoriteList;
    private int mInterval = 15000; // 15 seconds by default, can be changed later
    private Handler mHandler, fHandler;
    TextView netWorth;
    TextView tiingoLabel;

    private RecyclerView mRecyclerView, fRecyclerView;
    private ExampleAdapter mAdapter, fAdapter;
    private RecyclerView.LayoutManager mLayoutManager, fLayoutManager;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String portfolioString = "portfolioString";
    public static final String favoriteString = "favoriteString";
    public static final String userAmount = "userAmount";

    String selectedItem = "";

    private ArrayList<ExampleItem> portfolioItemList, favoriteItemList;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        tiingoLabel = findViewById(R.id.tingoLabel);
        dateTimeText = findViewById(R.id.dateTextView);
        netWorth = (TextView)findViewById(R.id.netWorth);

        LocalDate currentdate = LocalDate.now();
        int currentDay = currentdate.getDayOfMonth();
        String currentMonth = currentdate.getMonth().toString();
        String month = currentMonth.substring(1, currentMonth.length());
        month = month.toLowerCase();
        month = String.valueOf(currentMonth.charAt(0)) + month;
        int currentYear = currentdate.getYear();
        //Toast.makeText(this, String.valueOf(currentYear), Toast.LENGTH_LONG).show();
        String dateString = month+" "+ String.valueOf(currentDay)+", "+ String.valueOf(currentYear);
        dateTimeText.setText(dateString);


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String getter = sharedPreferences.getString(userAmount,"");
        if (getter.length() ==0)
            getter= "20000";


        // redundant
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userAmount, getter);
        editor.apply();


        netWorth.setText(getter);

        portfolioItemList = new ArrayList<>();
        favoriteItemList = new ArrayList<>();

        // LOAD PORTFOLIO & FAVORITES FROM LOCAL STORAGE

        getter =  sharedPreferences.getString(favoriteString,"");
       // Toast.makeText(this, "getter length size is "+getter.length(), Toast.LENGTH_LONG).show ();




        if (getter.length () !=0 ) {

            Type type = new TypeToken < ArrayList < ExampleItem >> () {}.getType();
            favoriteItemList = new Gson().fromJson(getter, type);

        }

        getter =  sharedPreferences.getString(portfolioString,"");
        if (getter.length() !=0) {
            Type type = new TypeToken < ArrayList < ExampleItem >> () {}.getType();
            portfolioItemList = new Gson().fromJson(getter, type);
        }

        tempPortfolioList = new ArrayList<ExampleItem>();


        mRecyclerView = findViewById(R.id.portfolioRecyclerView);
        fRecyclerView = findViewById(R.id.favoritesRecyclerView);

        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        fLayoutManager = new LinearLayoutManager(this);

        //Toast.makeText(this, "Size is "+favoriteItemList.size(), Toast.LENGTH_LONG).show();

        mAdapter = new ExampleAdapter(portfolioItemList);
        fAdapter = new ExampleAdapter(favoriteItemList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        fRecyclerView.setLayoutManager(fLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
        fRecyclerView.setAdapter(fAdapter);




        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //         Toast.makeText(getApplicationContext(), position+" is the position value", Toast.LENGTH_SHORT).show();
                //    mAdapter.notifyDataSetChanged();
            }
        });

        fAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //              Toast.makeText(getApplicationContext(), position+" is the position value", Toast.LENGTH_SHORT).show();
                //    mAdapter.notifyDataSetChanged();
            }
        });


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        ItemTouchHelper itemTouchHelperFav = new ItemTouchHelper(simpleCallbackFav);
        itemTouchHelperFav.attachToRecyclerView(fRecyclerView);

        tiingoLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlString = "https://www.tiingo.com/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    getApplicationContext().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    getApplicationContext().startActivity(intent);
                }
            }
        });



        mHandler = new Handler();
        fHandler = new Handler();
        //startRepeatingTask();


    }

    // Touch and swipe for Portfolio
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START |
            ItemTouchHelper.END,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(portfolioItemList,fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String temp = new Gson().toJson(portfolioItemList);
            editor.putString(portfolioString, temp);
            editor.apply();

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if(direction == ItemTouchHelper.LEFT)
            {
                portfolioItemList.remove(position);
                mAdapter.notifyItemRemoved(position);
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String temp = new Gson().toJson(portfolioItemList);
                editor.putString(portfolioString, temp);
                editor.apply();

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.delete_dustbin_icon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };



    // Touch and swipe for Favorites

    ItemTouchHelper.SimpleCallback simpleCallbackFav = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START |
            ItemTouchHelper.END,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(favoriteItemList,fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String temp = new Gson().toJson(favoriteItemList);
            editor.putString(favoriteString, temp);
            editor.apply();
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if(direction == ItemTouchHelper.LEFT)
            {
                favoriteItemList.remove(position);
                fAdapter.notifyItemRemoved(position);
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String temp = new Gson().toJson(favoriteItemList);
                editor.putString(favoriteString, temp);
                editor.apply();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.delete_dustbin_icon)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };



        Runnable mStatusChecker = new Runnable() {
            @Override
            public void run() {

                try {
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = sharedPreferences.getString(portfolioString, "");
                    if (json.length() > 0) {
                        Type type = new TypeToken<ArrayList<ExampleItem>>() {}.getType();
                        ArrayList<ExampleItem> storedPortfolio = new Gson().fromJson(json, type);
                        //tempPortfolioList = new ArrayList<Portfolio>();
                        for (int i = 0; i < storedPortfolio.size(); i += 1) {
                                ExampleItem obj = storedPortfolio.get(i);
                                makeStockRequest(obj);
                            }
                        // get stored porfolios from local storage
                    }
                } finally {
                    System.out.println("Portfolio Data called in 15 sec");
                    portfolioItemList = (ArrayList<ExampleItem>)tempPortfolioList.clone();
                    mAdapter.notifyDataSetChanged();
                    tempPortfolioList = new ArrayList<>();
                    mHandler.postDelayed(mStatusChecker, mInterval);
                }
            }
        };

    Runnable fStatusChecker = new Runnable() {
        @Override
        public void run() {

            try {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = sharedPreferences.getString(favoriteString, "");
                if (json.length() > 0) {
                    Type type = new TypeToken<ArrayList<Portfolio>>() {}.getType();
                    ArrayList<ExampleItem> storedPortfolio = new Gson().fromJson(json, type);
                    //tempPortfolioList = new ArrayList<Portfolio>();
                    for (int i = 0; i < storedPortfolio.size(); i += 1) {
                        ExampleItem obj = storedPortfolio.get(i);
                        makeStockRequest(obj);
                    }
                    // get stored porfolios from local storage
                }

            } finally {

                System.out.println("Favorite List Data called in 15 sec");
                favoriteItemList = (ArrayList<ExampleItem>)tempFavoriteList.clone();
                fAdapter.notifyDataSetChanged();
                tempFavoriteList = new ArrayList<>();
                fHandler.postDelayed(fStatusChecker, mInterval);

            }
        }
    };



    void startRepeatingTask() {
           mStatusChecker.run();
           fStatusChecker.run();
    }

    void stopRepeatingTask() {
        //  mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRepeatingTask();
    }

    @Override
    public void onResume() {

        super.onResume();
        selectedItem = "";

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

      String  getter =  sharedPreferences.getString(favoriteString,"");
        // Toast.makeText(this, "getter length size is "+getter.length(), Toast.LENGTH_LONG).show ();


        if (getter.length() <=2)
        {
        favoriteItemList = new ArrayList<>();
        }

        else {

            Type type = new TypeToken < ArrayList < ExampleItem >> () {}.getType();
            favoriteItemList = new Gson().fromJson(getter, type);

        }

        getter =  sharedPreferences.getString(portfolioString,"");
        if (getter.length() == 0)
        {
            portfolioItemList = new ArrayList<>();
        }
        else {
            Type type = new TypeToken < ArrayList < ExampleItem >> () {}.getType();
            portfolioItemList = new Gson().fromJson(getter, type);
        }

        mAdapter.notifyDataSetChanged();
        fAdapter.notifyDataSetChanged();

        // Toast.makeText(this, "On Resume called !! ",Toast.LENGTH_LONG ).show ();
        //      startRepeatingTask();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        searchView = (SearchView) menu.findItem(R.id.search_icon_image).getActionView();

        autoSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);

        //searchAutoComplete.setThreshold(3);
        searchAutoComplete.setHint("Change this to an icon...");
        searchAutoComplete.setAdapter(autoSuggestAdapter);
        searchAutoComplete.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        searchAutoComplete.setText(autoSuggestAdapter.getObject(i));
                        String[] splitter = autoSuggestAdapter.getObject(i).split("\\s+");
                        selectedItem = splitter[0];
                    }
                });
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed (TRIGGER_AUTO_COMPLETE,AUTO_COMPLETE_DELAY);
           //     if (charSequence.toString().length() > 2)
             //       Toast.makeText(getApplicationContext(), "you typin' more than 3", Toast.LENGTH_SHORT).show();
                // api call
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });



        /*
        MenuItem searchItem  = menu.findItem (R.id.search_icon_image);
        autoCompleteTextView = (AppCompatAutoCompleteTextView) MenuItemCompat.getActionView(searchItem);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        //autoCompleteTextView.setOnItemClickListener(new );
        /*
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(this, "You Clicked me ! ", Toast.LENGTH_SHORT);
        switch (item.getItemId()) {
            case R.id.search_icon_image:

                Toast.makeText(this, "You Clicked me ! ", Toast.LENGTH_LONG);
                if (selectedItem.length() !=0) {
                    Intent intent = new Intent(this, MainActivity2.class);
                    intent.putExtra("ticker", selectedItem);
                    startActivity(intent);
                    // User chose the "Settings" item, show the app settings UI...
                    return true;
                }

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void makeApiCall(String text) {

        ApiCall.make(this, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(response);
                    //JSONObject responseObject = new JSONObject(response);
                    //JSONArray array = responseObject.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        String gets = row.getString("ticker")+" "+row.getString("name");
                        stringList.add(gets);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //IMPORTANT: set data here and notify
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }


private String getStockName (final ExampleItem obj)
{
   String endpoint = "http://localhost:8080/stockdetails?keyword="+obj.getTicker();

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, endpoint,  null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                companyName = response.getString("companyName");
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "some error occured ! ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
    return companyName;
}

    private void makeStockRequest(final ExampleItem obj) {
        //      Toast.makeText(getApplicationContext(), "Reached Inside API call", Toast.LENGTH_LONG ).show ();
        // define endpoint url from node

        String endpoint = "http://localhost:8080/summary?keyword="+obj.getTicker();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, endpoint,  null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String changeString = response.getString("change");
                    String lpString = response.getString("lastPrice");
                    String numOfShares = obj.getnumShares();
                    String putterShares = "";
                    if (numOfShares.equals("0"))
                    {
                        companyName = "undefined";
                        companyName = getStockName(obj);
                        putterShares = companyName;
                    }
                    else
                    {
                        putterShares = numOfShares +" shares";
                    }
                    ExampleItem it1 = new ExampleItem(-1,-1,lpString,putterShares,changeString,obj.getTicker());
                    tempPortfolioList.add (it1);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "some error occured ! ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

}
