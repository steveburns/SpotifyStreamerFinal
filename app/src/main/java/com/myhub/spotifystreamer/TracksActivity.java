package com.myhub.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.myhub.spotifystreamer.fragments.TopTracksFragment;
import com.myhub.spotifystreamer.utils.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class TracksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        Log.d("onCreate", "called for TracksActivity");

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(AppConstants.ARTIST_ITEM, getIntent().getParcelableExtra(AppConstants.ARTIST_ITEM));

            TopTracksFragment topTracksFragment = new TopTracksFragment();
            topTracksFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_tracks_container, topTracksFragment)
                    .commit();
        }

    }

}
