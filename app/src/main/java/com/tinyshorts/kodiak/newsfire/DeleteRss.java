package com.tinyshorts.kodiak.newsfire;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import static com.tinyshorts.kodiak.newsfire.R.id.parent;

public class DeleteRss extends AppCompatActivity {


    /* Declaring the variables which we will be using


     */
    private ListView listItems;
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<String> rssNames = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_rss);

        urls = getUrlInDB();
        rssNames = getUrlNamesInDB();
        final ArrayList<String> TemprssNames = getUrlNamesInDB();

        int i = TemprssNames.size()-1;
        TemprssNames.remove(i);








        listItems = (ListView)findViewById(R.id.deletersslist);





        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TemprssNames){

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view =super.getView(position, convertView, parent);

            TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
            textView.setTextColor(Color.WHITE);

            return view;
        }
    };






        listItems.setAdapter(mAdapter);
        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                new MaterialDialog.Builder(DeleteRss.this)
                        .title("Confirmation Dialog")
                        .content("Are you sure you want to delete: " + rssNames.get(position))
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive((new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO
                               // Toast.makeText(DeleteRss.this,"Selected positive",Toast.LENGTH_LONG).show();
                                urls.remove(position);
                                rssNames.remove(position);
                                TemprssNames.remove(position);
                                saveURLInDB();
                                saveUrlNamesInDb();
                                mAdapter.notifyDataSetChanged();






                            }
                        }))
                        .onNegative((new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO
                            }
                        }))
                        .show();



            }
        });



    }


    // This just returns all the rss urls we have stored in our db to an arraylist
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
        //getNamesList.remove(getNamesList.size());

        //
      //  int i = getNamesList.size()-1;
      //  getNamesList.remove(i);


        return getNamesList;
    }

    //returns back all the urls in the arraylist and saves it back to the db.
    public void saveUrlNamesInDb()
    {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        tinyDB.putListString("savedNames", rssNames);
    }


    public void saveURLInDB()
    {
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        tinyDB.putListString("savedUrls",urls);

    }


}
