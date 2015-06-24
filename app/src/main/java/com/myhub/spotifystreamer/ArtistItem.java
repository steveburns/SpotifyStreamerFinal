package com.myhub.spotifystreamer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class ArtistItem implements Parcelable {

    // parcel keys
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGEURL = "imageurl";

    public String id;
    public String name;
    public String imageUrl;

    public ArtistItem(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();

        // insert the key value pairs to the bundle
        bundle.putString(KEY_ID, id);
        bundle.putString(KEY_NAME, name);
        bundle.putString(KEY_IMAGEURL, imageUrl);

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }

    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<ArtistItem> CREATOR = new Creator<ArtistItem>() {

        @Override
        public ArtistItem createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            return new ArtistItem(
                    bundle.getString(KEY_ID),
                    bundle.getString(KEY_NAME),
                    bundle.getString(KEY_IMAGEURL));
        }

        @Override
        public ArtistItem[] newArray(int size) {
            return new ArtistItem[size];
        }

    };
}
