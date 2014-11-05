package com.example.sona.tourist;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


public class ActivityMain extends FragmentActivity {
    static String ServerURL = "http://192.168.53.155:3000";
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RoomsFragment rooms_fragment = new RoomsFragment();
        Bundle args = new Bundle();

        rooms_fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentToSwap, rooms_fragment, "homepage").commit();
        new GetDataInAsyncTask(){
            @Override
            protected void onPostExecute(String v){
                output =v;
                Log.d("output", output);
            }
        }.execute(ActivityMain.ServerURL + "/api/showdata", "");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
