package com.example.user.googlemaps.Model;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

public class MyItem implements ClusterItem {
    private LatLng mPosition;
    private final String mTitle;
    private final String mSnippet;
    private final String mType;
    public final int profilePhoto;

    //MyItem constructor
    public MyItem(double lat, double lng, String mTitle, String mSnippet, String mType, int pictureResource) {
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
        this.mType = mType;
        profilePhoto = pictureResource;
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
    public String getSnippet(){
        return mSnippet;
    }

    public String getTitle(){
        return mTitle;
    }
    public String getType() {
        return mType;
    }

   /* public void setmPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }


    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getmStoreLogo() {
        return mStoreLogo;
    }

    public void setmStoreLogo(String mStoreLogo) {
        this.mStoreLogo = mStoreLogo;
    }*/

}


/**
 * Created by Damini on 27-Jul-16.
 */
