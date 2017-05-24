package com.tinyshorts.kodiak.newsfire;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.CircularProgressButton;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ShimmerTextView tv;
    Shimmer shimmer;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> urls = new ArrayList<>();

    private ArrayList<String> rssNames = new ArrayList<>();
    CircularProgressButton btn;
    CircularProgressButton deletebtn;
//String[] urls = {"http://www.androidcentral.com/feed","https://www.geo.tv/rss/1/0"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // we initialize the database that we will be using
        TinyDB tinyDB = new TinyDB(getApplicationContext());


        //if the user is logged in then no need to show the login or signup page when app is run
        if(tinyDB.getBoolean("Login") == true)
        {
        tinyDB.putBoolean("Login",true);
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        }
        tv = (ShimmerTextView) findViewById(R.id.textView);

        //just starts the shimmer text o newsFire
        shimmer = new Shimmer();
        shimmer.start(tv);

        // setting the colors for the buttons on the main activity
        btn = (CircularProgressButton) findViewById(R.id.btn);
        btn.setBackgroundColor(Color.parseColor("#4CAF50"));
        btn.setStrokeColor(Color.parseColor("#4CAF50"));

        deletebtn =(CircularProgressButton) findViewById(R.id.deletebtn);
        deletebtn.setBackgroundColor(Color.parseColor("#D32F2F"));
        deletebtn.setStrokeColor(Color.parseColor("#D32F2F"));
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoDeleteRss = new Intent(getApplicationContext(),DeleteRss.class);
                startActivity(gotoDeleteRss);
            }
        });

//      using sharedpreferences to add some rss feeds on the apps first run only
        SharedPreferences ratePrefs = getSharedPreferences("First Update", 0);

      if (!ratePrefs.getBoolean("FirstTime", false)) {

            urls.add("http://www.androidcentral.com/feed");
            urls.add("https://www.geo.tv/rss/1/0");
            Toast.makeText(getApplicationContext(),"Added links to urls array",Toast.LENGTH_LONG).show();


             rssNames.add("Android Central");
             rssNames.add("Geo tv");
             rssNames.add("Add More");
             saveUrlNamesInDb();

            SharedPreferences.Editor edit = ratePrefs.edit();
            edit.putBoolean("FirstTime", true);
            edit.commit();
            saveURLInDB();

      }

        urls = getUrlInDB();

 //   ratePrefs.edit().clear().commit();
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        // setting up what to do when an item is clicked on in the app drawer
        // if rss name is selected then it goes to the view artciles activity
        // if add more is selected we take the user back to the main page to add more rss feeds
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                String check = "" +position;
                //If user clicks on add more option
                if(Integer.parseInt(check) == (rssNames.size()-1))
                {
                    Intent gotoNews = new Intent(getApplicationContext(),MainActivity.class);
                    //gotoNews.putExtra("rsslink",urls[position]);
                    startActivity(gotoNews);
                }
                else
                {
                    Intent gotoNews = new Intent(getApplicationContext(),NewsFire.class);
                    gotoNews.putExtra("rsslink",urls.get(position));
                    gotoNews.putExtra("rssname",rssNames.get(position));
                    startActivity(gotoNews);
                }
            }
        });



    }


    // when we go back to the mainactivity from some other activity update the list items.
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        addDrawerItems();
        mAdapter.notifyDataSetChanged();
        deletebtn.setProgress(0);

    }

    // this just adds the rss names to the navigation drawer
    private void addDrawerItems(){
       // String[] rssNames = { "Android Central", "Geo tv","Add More" };

        rssNames = getUrlNamesInDB();


        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rssNames){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };
        mDrawerList.setAdapter(mAdapter);
    }

 // called when add rss button is rpessed and checks if the user entered bth rss name and url else show error
    public void addRss(View v)
    {




        // 1- implement a check on empty edittext of rss name and link
        EditText rsslink = (EditText) findViewById(R.id.urltext);
        EditText rssName = (EditText) findViewById(R.id.urlname);


        String rssurl = rsslink.getText().toString();
        String rssname = rssName.getText().toString();

        if( rssname.matches("") || rssurl.matches("https://www."))
        {
            btn.setProgress(-1);
            if(rssname.matches(""))
            {
                rssName.setError("Please enter a Valid Name");
            }
            if(rssurl.matches("https://www."))
            {
                rsslink.setError("Please enter a valid rss link");
            }

        }
        else {
            urls.add(rssurl);
            //String temp = rssNames.get(rssNames.size()-1);
            rssNames.add(rssNames.size() - 1, rssname);
            btn.setProgress(100);
            //rssNames.add("Add More");


            //mAdapter.clear();
            //mAdapter.add(rssNames);
            mAdapter.notifyDataSetChanged();
            //mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rssNames);

            Toast.makeText(getApplicationContext(), "Rss Successfully added", Toast.LENGTH_LONG).show();
            saveURLInDB();
            saveUrlNamesInDb();




        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn.setProgress(0);
                btn.setBackgroundColor(Color.parseColor("#4CAF50"));
                btn.setStrokeColor(Color.parseColor("#4CAF50"));
            }
        }, 3000);
    }

    public void saveURLInDB()
    {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        tinyDB.putListString("savedUrls",urls);

    }


    public ArrayList<String> getUrlInDB()
    {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        ArrayList<String> getUrlList= tinyDB.getListString("savedUrls");
        return getUrlList;
    }

    public void saveUrlNamesInDb()
    {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        tinyDB.putListString("savedNames", rssNames);
    }


    public ArrayList<String> getUrlNamesInDB()
    {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        ArrayList<String> getNamesList = tinyDB.getListString("savedNames");

        return getNamesList;
    }

}