package com.myhub.spotifystreamer.fragments;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.myhub.spotifystreamer.ArtistItem;
import com.myhub.spotifystreamer.R;
import com.myhub.spotifystreamer.TrackAdapter;
import com.myhub.spotifystreamer.TrackItem;
import com.myhub.spotifystreamer.utils.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopTracksFragment extends Fragment {

    private static final String KEY_TRACK_LIST = "tracks";
    private static final String KEY_ARTIST_ID = "artist_id";
    private static final String KEY_ARTIST_NAME = "artist_name";

    private String mSavedArtistId = "";
    private String mSavedArtistName = "";
    private ArrayList<TrackItem> mTracksList = null;
    private TrackAdapter mTracksAdapter = null;

    private OnFragmentInteractionListener mListener;

    @InjectView(R.id.listTracksView) ListView myListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public TopTracksFragment() {
        // Required empty public constructor
    }

    public static TopTracksFragment newInstance(ArtistItem artistItem) {
        TopTracksFragment fragment = new TopTracksFragment();

        Log.d("newInstance", "TopTracksFragment");

        // Supply artistItem input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(AppConstants.ARTIST_ITEM, artistItem);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            // being restore after a rotation?
            restoreInstanceState(savedInstanceState);
            Log.d("onCreateView", "savedInstanceState is NOT null");
        }

        if (mTracksList == null) {
            mTracksList = new ArrayList<>();
        }
        if (mTracksAdapter == null) {
            mTracksAdapter = new TrackAdapter(getActivity(), mTracksList);
        }

        ArtistItem artistItem = null;
        Bundle arguments = getArguments();
        if (arguments != null && mTracksList.size() < 1) {
            artistItem = arguments.getParcelable(AppConstants.ARTIST_ITEM);
            if (artistItem != null) {
                Log.d("onCreateView", "We have an Artist");
                mSavedArtistId = artistItem.id;
                mSavedArtistName = artistItem.name;
                new TopTracksAsyncTask().execute(mSavedArtistId);
            }
        }

        myListView.setAdapter(mTracksAdapter);

        /* TODO
        ActionBar actionBar = getActivity(). getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(mSavedArtistName);
        }*/

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*                TrackItem track = (TrackItem) myListView.getAdapter().getItem(position);
                Intent i = new Intent(track.getId(), null, getActivity(), PlayerActivity.class);
                i.putExtra("artist", mSavedArtistName);
                i.putExtra("album", track.getAlbum());
                i.putExtra("track", track.getName());
                i.putExtra("imageurl", track.getImageFullUrl());
                Log.d("previewURL", track.getPreview_url());
                startActivity(i);*/

                mListener.onTrackSelected(mSavedArtistName, mTracksList, position);
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_TRACK_LIST, mTracksList);
        outState.putString(KEY_ARTIST_ID, mSavedArtistId);
        outState.putString(KEY_ARTIST_NAME, mSavedArtistName);
        super.onSaveInstanceState(outState);
    }

    public void restoreInstanceState(Bundle savedState) {
        mTracksList = savedState.getParcelableArrayList(KEY_TRACK_LIST);
        mSavedArtistId = savedState.getString(KEY_ARTIST_ID);
        mSavedArtistName = savedState.getString(KEY_ARTIST_NAME);
    }

    private class TopTracksAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPostExecute(Integer numTracks) {

            // This method runs on the UI thread so we can update the UI.

            // Notify the adapter that the list of tracks has changed.
            // Good post about how this operates under the covers:
            // http://stackoverflow.com/questions/3669325/notifydatasetchanged-example
            mTracksAdapter.notifyDataSetChanged();

            if (numTracks < 1) {

                // Artist has no tracks
                Toast.makeText(getActivity(), R.string.no_tracks_found, Toast.LENGTH_SHORT).show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onTrackSelected(String artistName, ArrayList<TrackItem> tracksList, int position);
    }

}
