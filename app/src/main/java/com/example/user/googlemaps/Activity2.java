package com.example.user.googlemaps;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Activity2 extends AppCompatActivity {
    GoogleMap googleMap;
    String json = null;
    private android.support.v7.widget.Toolbar toolbar;

    LatLng objLatLng;
    ListView listView;
    private static final float DEFAULTZOOM =4;
    ArrayList<String> listItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getting latitude and longitude from the prev activity via intent
        objLatLng=getIntent().getExtras().getParcelable("latlng");

        setContentView(R.layout.activity_2);
        //setting up toolbar
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        //calling Async task for parsing the data
        new Task().execute();
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment)).getMap();

        listView=(ListView)findViewById(R.id.listView);

        //adding marker on the map at the lat lng recieved via intent
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(new LatLng(objLatLng.latitude, objLatLng.longitude)));


        //By default location on map fragment
        LatLng ll = new LatLng(24.119445,82.695626);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, DEFAULTZOOM);
        googleMap.moveCamera(update);
        //zoom in the map to the location of the lat lng recieved
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(objLatLng.latitude,objLatLng.longitude), 15));
        }
//back button on the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //Asyntask pasrses the data from local JSON into the listview
    private class Task extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {

            try {
                //getting JSON file from assets folder
                InputStream is = getAssets().open("info.json");

                int size = is.available();

                byte[] buffer = new byte[size];

                is.read(buffer);

                is.close();

                json = new String(buffer, "UTF-8");


            } catch (IOException ex) {
                ex.printStackTrace();

            }
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            listView=(ListView)findViewById(R.id.listView);
            ArrayAdapter<String> ad = new ArrayAdapter<String>(Activity2.this,android.R.layout.simple_list_item_1,listItems);//defining path n specifying which type of data
            listView.setAdapter(ad);

            try {
                JSONObject obj = new JSONObject(json);
                JSONArray m_jArray = obj.getJSONArray("PowerPlants");//fetching data from jsonarray
                for (int i = 0; i < m_jArray.length(); i++) {
                    JSONObject jo_inside = m_jArray.getJSONObject(i);
                    if((jo_inside.getDouble("Latitude")==(objLatLng.latitude))&&(jo_inside.getDouble("Longitude")==(objLatLng.longitude)))
                    {
                        listItems.add(" Name : " + jo_inside.getString("Name"));
                        listItems.add(" State : " + jo_inside.getString("State"));
                        listItems.add(" Type : " + jo_inside.getString("Type"));
                        listItems.add(" Address : " + jo_inside.getString("Address"));
                        listItems.add(" Telephone : " + jo_inside.getString("Telephone"));
                        listItems.add(" Fax : " + jo_inside.getString("Fax"));
                        listItems.add("Approved Capacity : " + jo_inside.getString("Approved Capacity"));
                        listItems.add("Installed Capacity : " + jo_inside.getString("Installed Capacity"));

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }
    }


