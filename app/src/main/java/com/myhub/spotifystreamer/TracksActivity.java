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

    private static final String KEY_TRACK_LIST = "tracks";
    private static final String KEY_ARTIST_ID = "artist_id";
    private static final String KEY_ARTIST_NAME = "artist_name";

    private String mSavedArtistId = "";
    private String mSavedArtistName = "";
    private ArrayList<TrackItem> mTracksList = null;
    private TrackAdapter mTracksAdapter = null;


    @InjectView(R.id.listTracksView) ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        ButterKnife.inject(this);

        Log.d("onCreate", "called for TracksActivity");

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
            Log.d("onCreate", "savedInstanceState is NOT null");
        }

        if (mTracksList == null) {
            mTracksList = new ArrayList<>();
        }
        if (mTracksAdapter == null) {
            mTracksAdapter = new TrackAdapter(getBaseContext(), mTracksList);
        }

        Intent intent = getIntent();
        if (intent != null && mTracksList.size() < 1) {
            Log.d("onCreate", "We have an intent!!!");
            mSavedArtistId = intent.getStringExtra("artist_id");
            mSavedArtistName = intent.getStringExtra("artist_name");
            new TopTracks().execute(mSavedArtistId);
        }

        myListView.setAdapter(mTracksAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(mSavedArtistName);
        }

        /*

         PLEASE IGNORE THE ITEM CLICK FOR NOW!!!
         I GOT ANXIOUS AND BEGAN WORKING ON IT BEFORE I FULLY COMPLETED STAGE 1.

         */

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrackItem track = (TrackItem) myListView.getAdapter().getItem(position);
                Intent i = new Intent(track.id, null, getApplicationContext(), PlayerActivity.class);
                i.putExtra("artist", mSavedArtistName);
                i.putExtra("album", track.album);
                i.putExtra("track", track.name);
                i.putExtra("imageurl", track.imageFullUrl);
                Log.d("previewURL", track.preview_url);
                startActivity(i);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_TRACK_LIST, mTracksList);
        outState.putString(KEY_ARTIST_ID, mSavedArtistId);
        outState.putString(KEY_ARTIST_NAME, mSavedArtistName);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        restoreInstanceState(savedState);
    }

    public void restoreInstanceState(Bundle savedState) {
        mTracksList = savedState.getParcelableArrayList(KEY_TRACK_LIST);
        mSavedArtistId = savedState.getString(KEY_ARTIST_ID);
        mSavedArtistName = savedState.getString(KEY_ARTIST_NAME);
    }

    private class TopTracks extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPostExecute(Integer numTracks) {

            // This method runs on the UI thread so we can update the UI.

            // Notify the adapter that the list of tracks has changed.
            // Good post about how this operates under the covers:
            // http://stackoverflow.com/questions/3669325/notifydatasetchanged-example
            mTracksAdapter.notifyDataSetChanged();

            if (numTracks < 1) {

                // Artist has no tracks
                Toast.makeText(getApplicationContext(), R.string.no_tracks_found, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {

            // Get tracks on a background thread
            if (!TextUtils.isEmpty(mSavedArtistId)) {
                try {
                    SpotifyApi api = new SpotifyApi();
                    SpotifyService spotify = api.getService();
                    Map<String, Object> options = new HashMap<>();
                    options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());
                    Tracks tracks = spotify.getArtistTopTrack(mSavedArtistId, options);

                    if (tracks != null && tracks.tracks != null) {
                        if (tracks.tracks.size() > 0) {
                            // We have results, size the array exactly.
                            for (Track track : tracks.tracks) {
                                String smallImageUrl = null;
                                String largeImageUrl = null;
                                String albumName = "";
                                if (track.album != null) {
                                    albumName = track.album.name;
                                    largeImageUrl = track.album.images.get(0).url;
                                    smallImageUrl = track.album.images.get(track.album.images.size() - 1).url;
                                }
                                mTracksList.add(new TrackItem(track.id, track.name, smallImageUrl, largeImageUrl, albumName, track.preview_url));
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Exception logged", e.getMessage());
                }
            }
            return mTracksList.size();
        }
    }
}
