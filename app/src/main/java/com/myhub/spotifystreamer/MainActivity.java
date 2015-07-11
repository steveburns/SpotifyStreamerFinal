package com.myhub.spotifystreamer;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.myhub.spotifystreamer.fragments.ArtistSearchFragment;
import com.myhub.spotifystreamer.fragments.PlayerFragment;
import com.myhub.spotifystreamer.fragments.TopTracksFragment;
import com.myhub.spotifystreamer.utils.AppConstants;

import java.util.ArrayList;


/* Here's a word on using AppCompatActivity:

So ActionBarActivity is deprecated since version 22.1.0.
Instead, google encourages developers to use AppCompatActivity.
For more information, read these two pages:
http://android-developers.blogspot.ca/2015/04/android-support-library-221.html
and
http://developer.android.com/tools/support-library/index.html
 */
public class MainActivity extends AppCompatActivity implements
        ArtistSearchFragment.OnFragmentInteractionListener,
        TopTracksFragment.OnFragmentInteractionListener
{

    private boolean mTwoPane;

//    @InjectView(R.id.listView) ListView myListView;
//    @InjectView(R.id.artistSearchText) EditText artistSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.inject(this);

        if(findViewById(R.id.fragment_top_tracks_container) != null) {
            Log.d("MainActivity", "This is 2 Pane device");
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_top_tracks_container, new TopTracksFragment(), "")
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putParcelableArrayList(KEY_ARTIST_LIST, mArtistList);
//        outState.putString(KEY_ARTIST_SEARCH, mSavedSearchText);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        restoreInstanceState(savedState);
    }

    public void restoreInstanceState(Bundle savedState) {
//        mArtistList = savedState.getParcelableArrayList(KEY_ARTIST_LIST);
//        mSavedSearchText = savedState.getString(KEY_ARTIST_SEARCH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onArtistSelected(ArtistItem artist) {

        if(mTwoPane) {
            Log.d("Selected", "Artist selected in 2 pane mode");
            TopTracksFragment fragment = TopTracksFragment.newInstance(artist);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_top_tracks_container, fragment, "")
                    .commit();
        } else {
            Log.d("Selected", "Artist selected in phone mode");
            Intent intent = new Intent(this, TracksActivity.class);
            intent.putExtra(AppConstants.ARTIST_ITEM, artist);
            startActivity(intent);
        }
    }

    @Override
    public void onTrackSelected(String artistName, ArrayList<TrackItem> tracksList, int position) {
        Log.d("Selected", "Track selected. Position = " + Integer.toString(position));

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(AppConstants.PLAYER_FRAG_TAG);
        if (prev != null) {

            // TODO: this may not be the right way to do this.
            // Maybe just show existing one.
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        PlayerFragment fragment = PlayerFragment.newInstance(artistName, tracksList, position);
        fragment.show(ft, AppConstants.PLAYER_FRAG_TAG);

  //      getSupportFragmentManager().beginTransaction()
  //              .replace(android.R.id.content, fragment, "")
  //              .commit();

        /*
        Android sample ---------

    // DialogFragment.show() will take care of adding the fragment
    // in a transaction.  We also want to remove any currently showing
    // dialog, so make our own transaction and take care of that here.
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
    if (prev != null) {
        ft.remove(prev);
    }
    ft.addToBackStack(null);

    // Create and show the dialog.
    DialogFragment newFragment = MyDialogFragment.newInstance(mStackLevel);
    newFragment.show(ft, "dialog");
         */

        /* -- Jordan's code

            FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            PlayerFragment fragment = PlayerFragment.newInstance(mTracks, mPosition, false);

            if (mContext.getResources().getBoolean(R.bool.isTablet)) {
                fragment.show(fragmentManager, PLAYER_DIALOG_TAG);
            } else {
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, fragment, PLAYER_FRAG_TAG)
                        .commit();
            }

         */
    }
}
