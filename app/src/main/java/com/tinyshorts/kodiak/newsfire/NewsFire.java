package com.tinyshorts.kodiak.newsfire;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NewsFire extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private ArticleAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressWheel progressWheel;

    private ListView mDrawerList;
    private ArrayAdapter<String> mdrawerAdapter;
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<String> rssNames = new ArrayList<>();
    String urlString;
    String urlName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_fire);
        urlString = getIntent().getExtras().getString("rsslink");


        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        urlName = getIntent().getExtras().getString("rssname");
        tb.setTitle(urlName);

        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.canChildScrollUp();

        getFeed();
        urls = getUrlInDB();

        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                String check = "" +position;
                //If user clicks on add more option
                if(Integer.parseInt(check) == (rssNames.size()-1))
                {
                    Toast.makeText(getApplicationContext(),"Add more",Toast.LENGTH_LONG).show();
                    Intent gotoAddmore = new Intent(getApplicationContext(),MainActivity.class);
                    //gotoNews.putExtra("rsslink",urls[position]);
                    startActivity(gotoAddmore);
                }
                else
                {
                    Intent gotoNews = new Intent(getApplicationContext(),NewsFire.class);
                    gotoNews.putExtra("rsslink",urls.get(position));
                    startActivity(gotoNews);
                }


            }
        });



    }

    public void getFeed()
    {
        Parser parser = new Parser();
        parser.execute(urlString);
        parser.onFinish(new Parser.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<Article> list) {

                mAdapter = new ArticleAdapter(list, R.layout.row, NewsFire.this);
                mRecyclerView.setAdapter(mAdapter);
                progressWheel.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);


               // Picasso.with(getBaseContext()).load(list.get(1).getImage()).into(img);
              //  Toast.makeText(getApplicationContext(), list.get(1).getTitle(), Toast.LENGTH_SHORT).show();

                //what to do when the parsing is done
                //the Array List contains all article's data. For example you can use it for your adapter.
            }

            @Override
            public void onError() {
                //what to do in case of error
            }
        });
    }

    private void addDrawerItems(){
        // String[] rssNames = { "Android Central", "Geo tv","Add More" };

        rssNames = getUrlNamesInDB();


        mdrawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rssNames);
        mDrawerList.setAdapter(mdrawerAdapter);
    }


    public ArrayList<String> getUrlInDB()
    {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        ArrayList<String> getUrlList= tinyDB.getListString("savedUrls");
        return getUrlList;
    }

    public ArrayList<String> getUrlNamesInDB()
    {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        ArrayList<String> getNamesList = tinyDB.getListString("savedNames");

        return getNamesList;
    }




}
