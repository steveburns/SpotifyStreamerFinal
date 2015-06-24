package com.myhub.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.app.ToolbarActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/* Here's a word on using AppCompatActivity:

So ActionBarActivity is deprecated since version 22.1.0.
Instead, google encourages developers to use AppCompatActivity.
For more information, read these two pages:
http://android-developers.blogspot.ca/2015/04/android-support-library-221.html
and
http://developer.android.com/tools/support-library/index.html
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_ARTIST_LIST = "artists";
    private static final String KEY_ARTIST_SEARCH = "search_term";
    private String mSavedSearchText = "";
    private ArrayList<ArtistItem> mArtistList = null;
    private ArtistAdapter mArtistAdapter = null;

    @InjectView(R.id.listView) ListView myListView;
    @InjectView(R.id.artistSearchText) EditText artistSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }

        if(mArtistList == null) {
            mArtistList = new ArrayList<>();
        }
        if (mArtistAdapter == null) {
            mArtistAdapter = new ArtistAdapter(getBaseContext(), mArtistList);
        }

        myListView.setAdapter(mArtistAdapter);
        artistSearchText.addTextChangedListener(getTextWatcher(this));

        final Context activityContext = this;
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistItem artist = (ArtistItem) myListView.getAdapter().getItem(position);
                Intent intent = new Intent(activityContext, TracksActivity.class);
                intent.putExtra("artist_id", artist.id);
                intent.putExtra("artist_name", artist.name);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_ARTIST_LIST, mArtistList);
        outState.putString(KEY_ARTIST_SEARCH, mSavedSearchText);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        restoreInstanceState(savedState);
    }

    public void restoreInstanceState(Bundle savedState) {
        mArtistList = savedState.getParcelableArrayList(KEY_ARTIST_LIST);
        mSavedSearchText = savedState.getString(KEY_ARTIST_SEARCH);
    }

    private TextWatcher getTextWatcher(final Activity context) {

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentText = artistSearchText.getText().toString();
                if (!currentText.equals(mSavedSearchText)) {
                    mSavedSearchText = currentText;
                    Log.d("afterTextChanged", mSavedSearchText);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new searchArtists().execute(mSavedSearchText);
                        }
                    });
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Task class to get list of artists using the search term(s) the user entered.
    //
    private class searchArtists extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPostExecute(Integer numArtists) {

            // This method runs on the UI thread so we can update the UI.

            // Notify the adapter that the list of artists has changed.
            // Good post about how this operates under the covers:
            // http://stackoverflow.com/questions/3669325/notifydatasetchanged-example
            mArtistAdapter.notifyDataSetChanged();

            if (numArtists < 1) {
                // There was a search string, but nothing was returned
                Toast.makeText(getApplicationContext(), "No artists found", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {

            mArtistList.clear();

            // Get artists on a background thread
            String artistSearchText = params[0];
            if (!TextUtils.isEmpty(artistSearchText)) {
                try {
                    SpotifyApi api = new SpotifyApi();
                    SpotifyService spotify = api.getService();
                    ArtistsPager results = spotify.searchArtists(artistSearchText);

                    if (results.artists != null) {
                        if (results.artists.items.size() > 0) {
                            // We have results, size the array exactly.
                            for (Artist artist : results.artists.items) {
                                String url = null;
                                if (artist.images != null && artist.images.size() > 0) {
                                    url = artist.images.get(artist.images.size() - 1).url;
                                }
                                mArtistList.add(new ArtistItem(artist.id, artist.name, url));
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Exception logged", e.getMessage());
                }
            }
            return mArtistList.size();
        }
    }
}
