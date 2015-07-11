package com.myhub.spotifystreamer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class TrackItem implements Parcelable {
    // parcel keys
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE_URL = "imageurl";
    private static final String KEY_FULLIMAGE_URL = "fullimageurl";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_PREVIEW_URL = "previewurl";

    private String id;
    private String name;
    private String imageUrl;
    private String imageFullUrl;
    private String album;
    private String preview_url;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageFullUrl() {
        return imageFullUrl;
    }

    public String getAlbum() {
        return album;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public TrackItem(String id, String name, String imageUrl, String imageFullUrl, String album, String preview_url) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.imageFullUrl = imageFullUrl;
        this.album = album;
        this.preview_url = preview_url;
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
        bundle.putString(KEY_IMAGE_URL, imageUrl);
        bundle.putString(KEY_FULLIMAGE_URL, imageFullUrl);
        bundle.putString(KEY_ALBUM, album);
        bundle.putString(KEY_PREVIEW_URL, preview_url);

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }

    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<TrackItem> CREATOR = new Creator<TrackItem>() {

        @Override
        public TrackItem createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            return new TrackItem(
                    bundle.getString(KEY_ID),
                    bundle.getString(KEY_NAME),
                    bundle.getString(KEY_IMAGE_URL),
                    bundle.getString(KEY_FULLIMAGE_URL),
                    bundle.getString(KEY_ALBUM),
                    bundle.getString(KEY_PREVIEW_URL));
        }

        @Override
        public TrackItem[] newArray(int size) {
            return new TrackItem[size];
        }

    };
}
