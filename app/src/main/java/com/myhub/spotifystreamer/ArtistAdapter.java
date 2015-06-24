package com.myhub.spotifystreamer;

import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistAdapter extends ArrayAdapter<ArtistItem> {

    public ArtistAdapter(Context context, List<ArtistItem> artistItems) {
        super(context, 0, artistItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        if (position >= getCount())
            return null;

        ArtistItem artistItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.artist, parent, false);
        }

        // Get views to set
        TextView tvName = (TextView) convertView.findViewById(R.id.textView);
        ImageView ivUrl = (ImageView) convertView.findViewById(R.id.url);

        // Populate the data into the template view using the data object
        tvName.setText(artistItem.name);

        // Populate the image view with the artist image using picasso.
        if (!TextUtils.isEmpty(artistItem.imageUrl)) {
            Picasso.with(this.getContext())
                    .load(artistItem.imageUrl)
                    .into(ivUrl);
        }

        return convertView;
    }
}
