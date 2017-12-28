package com.example.user.googlemaps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.googlemaps.Model.MyItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Activity1 extends AppCompatActivity implements ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>
        {
    private ClusterManager<MyItem> mClusterManager;

    private MyItem clickedClusterItem;


    private android.support.v7.widget.Toolbar toolbar;
    GoogleMap map;
    String json = null;


    private static final double DELHI_LAT = 24.105168, DELHI_LNG = 82.706383;
            private static final float DEFAULTZOOM =5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        //setting up the toolbar
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);




        setUpMapIfNeeded();
        //default location
        gotoLocation(DELHI_LAT, DELHI_LNG, DEFAULTZOOM);
    }
            //setting up map function
            private void setUpMapIfNeeded() {
                if (map == null) {
                    MapFragment mapFragment = (MapFragment) getFragmentManager()
                            .findFragmentById(R.id.fragment);
                    map = mapFragment.getMap();
                    if (map != null) {
                        setUpMap();
                    }


                }
            }
            //sets default location on start of app
            private void gotoLocation(double lat, double lng, float zoom) {
                LatLng ll = new LatLng(lat, lng);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
                map.moveCamera(update);
            }


            protected void setUpMap() {
                map.getUiSettings().setMapToolbarEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.setMyLocationEnabled(true);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //initializing clusterManager-
                // The ClusterManager passes the markers to the Algorithm, which transforms them into a set of clusters.
                mClusterManager = new ClusterManager<MyItem>(this,map);
                mClusterManager.setRenderer(new CustomRenderer());
                //map.setOnCameraChangeListener(mClusterManager);
                map.setOnMarkerClickListener(mClusterManager);
                map.setOnInfoWindowClickListener(mClusterManager);
                map.setInfoWindowAdapter(mClusterManager.getMarkerManager());

                mClusterManager.setOnClusterItemInfoWindowClickListener(this);


                mClusterManager
                        .setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                            @Override
                            public boolean onClusterItemClick(MyItem item) {
                                clickedClusterItem = item;
                                return false;
                            }
                        });


             //fetching information for marker setup
                new MarkerTask().execute();
                //call to change minimum cluster size


                mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(
                        new MyCustomAdapterForItems());
            }

           // The ClusterRenderer takes care of the rendering, by adding and removing clusters and individual markers.
            class CustomRenderer extends DefaultClusterRenderer<MyItem>
       {
           private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
           private final ImageView mImageView;

           public CustomRenderer() {
            super(getApplicationContext(),map, mClusterManager);
            View multiProfile = getLayoutInflater().inflate(R.layout.marker_colors, null);
            mIconGenerator.setContentView(multiProfile);
            mImageView = (ImageView) multiProfile.findViewById(R.id.image);


        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MyItem> cluster) {
            //start clustering if at least 2 items overlap
            return cluster.getSize() > 1;


        }
        @Override
        protected void onBeforeClusterItemRendered(MyItem item,
                                                   MarkerOptions markerOptions) {


            mImageView.setImageResource(item.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {

            super.onClusterItemRendered(clusterItem, marker);

        }


    }
           //customised infowindow adapter
            public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

                private final View myContentsView;
               //inflating the adpter with custom layout
                MyCustomAdapterForItems() {
                    myContentsView = getLayoutInflater().inflate(
                            R.layout.info_window, null);
                }
               //getting information for the info windows
                @Override
                public View getInfoWindow(Marker marker) {

                    TextView tvTitle = ((TextView) myContentsView
                            .findViewById(R.id.txtTitle));
                    TextView tvSnippet = ((TextView) myContentsView
                            .findViewById(R.id.txtSnippet));
                    TextView tvType = ((TextView) myContentsView
                            .findViewById(R.id.txtType));

                    tvTitle.setText(clickedClusterItem.getTitle());
                    tvSnippet.setText(clickedClusterItem.getSnippet());
                    tvType.setText(clickedClusterItem.getType());

                    return myContentsView;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }

            }
            @Override
            public void onClusterItemInfoWindowClick(MyItem myItem) {
                //Cluster item InfoWindow clicked, set title as action
                Intent i = new Intent(this, Activity2.class);
                i.setAction(myItem.getTitle());
                i.putExtra("latlng", myItem.getPosition());
                startActivity(i);
            }



   //getting data for markers by JSON parsing
    private class MarkerTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                //getting data from json file in assets folder
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


            try {
                JSONObject obj = new JSONObject(json);
                JSONArray m_jArray = obj.getJSONArray("PowerPlants");
                for (int i = 0; i < m_jArray.length(); i++) {
                    JSONObject jsonObj = m_jArray.getJSONObject(i);
                    //fetching Json data and storing in Myitem(ClusterItem) object
                    if(jsonObj.getString("Type").equals("coal")) {
                        MyItem offsetItem = new MyItem(jsonObj.getDouble("Latitude"), jsonObj.getDouble("Longitude"),
                                jsonObj.getString("Name"), jsonObj.getString("State"), jsonObj.getString("Type"), R.drawable.red);
                        mClusterManager.addItem(offsetItem);//adding the object to mclusterManager
                    }
                    else if(jsonObj.getString("Type").equals("gas")) {
                        MyItem offsetItem = new MyItem(jsonObj.getDouble("Latitude"), jsonObj.getDouble("Longitude"),
                                jsonObj.getString("Name"), jsonObj.getString("State"), jsonObj.getString("Type"), R.drawable.orange);
                        mClusterManager.addItem(offsetItem);//adding the object to mclusterManager
                    }
                    else if(jsonObj.getString("Type").equals("hydro")) {
                        MyItem offsetItem = new MyItem(jsonObj.getDouble("Latitude"), jsonObj.getDouble("Longitude"),
                                jsonObj.getString("Name"), jsonObj.getString("State"), jsonObj.getString("Type"), R.drawable.blueblack);
                        mClusterManager.addItem(offsetItem);//adding the object to mclusterManager
                    }
                    else {
                        MyItem offsetItem = new MyItem(jsonObj.getDouble("Latitude"), jsonObj.getDouble("Longitude"),
                                jsonObj.getString("Name"), jsonObj.getString("State"), jsonObj.getString("Type"), R.drawable.darkgreen);
                        mClusterManager.addItem(offsetItem);//adding the object to mclusterManager
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

}















