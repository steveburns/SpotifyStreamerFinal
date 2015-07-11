package com.myhub.spotifystreamer;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TrackAdapter extends ArrayAdapter<TrackItem> {

    public TrackAdapter(Context context, List<TrackItem> trackItems) {
        super(context, 0, trackItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        TrackItem trackItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.track, parent, false);
        }

        // Get views to set
        TextView tvName = (TextView) convertView.findViewById(R.id.textView);
        TextView tvAlbumName = (TextView) convertView.findViewById(R.id.albumTextView);
        ImageView ivUrl = (ImageView) convertView.findViewById(R.id.url);

        // Populate the data into the template view using the data object
        tvName.setText(trackItem.getName());
        tvAlbumName.setText(trackItem.getAlbum());

        // Populate the image view with the artist image using picasso.
        if (!TextUtils.isEmpty(trackItem.getImageUrl())) {
            Picasso.with(this.getContext())
                    .load(trackItem.getImageUrl())
                    .into(ivUrl);
        }

        return convertView;
    }
}
