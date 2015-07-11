package com.myhub.spotifystreamer.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myhub.spotifystreamer.ArtistAdapter;
import com.myhub.spotifystreamer.ArtistItem;
import com.myhub.spotifystreamer.R;
import com.myhub.spotifystreamer.TracksActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistSearchFragment extends Fragment {

    private static final String KEY_ARTIST_LIST = "artists";
    private static final String KEY_ARTIST_SEARCH = "search_term";
    private String mSavedSearchText = "";
    private ArrayList<ArtistItem> mArtistList = null;
    private ArtistAdapter mArtistAdapter = null;

    private OnFragmentInteractionListener mListener;

    @InjectView(R.id.listView) ListView myListView;
    @InjectView(R.id.artistSearchText) EditText artistSearchText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArtistSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArtistSearchFragment newInstance(String param1, String param2) {
        ArtistSearchFragment fragment = new ArtistSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ArtistSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_ARTIST_LIST, mArtistList);
        outState.putString(KEY_ARTIST_SEARCH, mSavedSearchText);
        super.onSaveInstanceState(outState);
    }

/*    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        restoreInstanceState(savedState);
    }
*/

    public void restoreInstanceState(Bundle savedState) {
        mArtistList = savedState.getParcelableArrayList(KEY_ARTIST_LIST);
        mSavedSearchText = savedState.getString(KEY_ARTIST_SEARCH);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_search, container, false);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }

        if(mArtistList == null) {
            mArtistList = new ArrayList<>();
        }
        if (mArtistAdapter == null) {
            mArtistAdapter = new ArtistAdapter(getActivity(), mArtistList);
        }

        myListView.setAdapter(mArtistAdapter);
        artistSearchText.addTextChangedListener(getTextWatcher(getActivity()));

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistItem artist = (ArtistItem) myListView.getAdapter().getItem(position);
                mListener.onArtistSelected(artist);
            }
        });

        return view;
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
                Toast.makeText(getActivity(), "No artists found", Toast.LENGTH_SHORT).show();
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
        void onArtistSelected(ArtistItem artistItem);
    }
}
