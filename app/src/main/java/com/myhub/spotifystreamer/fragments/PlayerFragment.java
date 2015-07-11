package com.myhub.spotifystreamer.fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.myhub.spotifystreamer.R;
import com.myhub.spotifystreamer.TrackItem;
import com.myhub.spotifystreamer.services.MusicService;
import com.myhub.spotifystreamer.utils.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends DialogFragment {
    private static final String TAG = PlayerFragment.class.getSimpleName();

    private ArrayList<TrackItem> mTracksList = null;
    private int mPosition = 0;
    private String mArtistName = "";
    private boolean mMusicServiceBound = false;
    private MusicService mMusicService;
    private ServiceConnection mConnection;

    @InjectView(R.id.artistTextView) TextView artistTextView;
    @InjectView(R.id.albumTextView) TextView albumTextView;
    @InjectView(R.id.trackTextView) TextView trackTextView;
    @InjectView(R.id.url) ImageView albumImageView;
    @InjectView(R.id.seekBar) SeekBar mSeekBar;
    @InjectView(R.id.playButton) ImageButton mPlayButton;
    @InjectView(R.id.pauseButton) ImageButton mPauseButton;
    private final Handler mHandler = new Handler();

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(String artistName, ArrayList<TrackItem> tracksList, int position) {
        PlayerFragment fragment = new PlayerFragment();

        // Supply inputs as arguments.
        Bundle args = new Bundle();
        args.putParcelableArrayList(AppConstants.TOP_TRACKS, tracksList);
        args.putString(AppConstants.ARTIST_NAME, artistName);
        args.putInt(AppConstants.TRACK_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restoreInstanceState(getArguments());
        }
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {

                MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
                mMusicService = binder.getService();
                mMusicServiceBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "PlayerFragment");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            // being restore after a rotation?
            restoreInstanceState(savedInstanceState);
            Log.d(TAG, "onCreateView: savedInstanceState is NOT null");
        }

        if (getArguments() != null) {
            restoreInstanceState(getArguments());
        }

        // Populate views
        if (!TextUtils.isEmpty(mArtistName)) artistTextView.setText(mArtistName);
        playTrack(getCurrentTrack());

        // setup listener for when user manually sets the position on the SeekBar.
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean isFromUser) {
                if (isFromUser) {
                    mMusicService.seekTo(position);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return view;
    }

    private TrackItem getCurrentTrack() {

        if (mTracksList != null && mPosition < mTracksList.size()) {
            return mTracksList.get(mPosition);
        }
        return null;
    }

    private void playTrack(TrackItem trackItem) {
        updateTrackUi(trackItem);

        if (!mMusicServiceBound) {
            startMusicService();
        }

        if (mMusicService != null) {
            mMusicService.playTrack(trackItem);
            hidePlayButton();
        }

        final Handler musicMethodsHandler = new Handler();
        Runnable musicRun = new Runnable() {

            @Override
            public void run() {
                if (mMusicServiceBound) {
                    updatePlayerControls();
                }else if(!mMusicServiceBound) {
                    Log.d(TAG, "Waiting to be bound");
                }
                musicMethodsHandler.postDelayed(this, 1000);
            }
        };
        musicMethodsHandler.postDelayed(musicRun, 1000);
    }

    private void updateTrackUi(TrackItem trackItem) {
        if (trackItem != null) {
            albumTextView.setText(trackItem.getAlbum());
            trackTextView.setText(trackItem.getName());

            if (!TextUtils.isEmpty(trackItem.getImageFullUrl())) {
                Picasso.with(getActivity())
                        .load(trackItem.getImageFullUrl())
                        .into(albumImageView);
            }
        }
    }

    @OnClick(R.id.nextTrack) public void onNextClicked() {
        moveToTrack(true);
    }

    @OnClick(R.id.previousTrack) public void onPreviousClicked() {
        moveToTrack(false);
    }

    @OnClick(R.id.playButton) public void onPlayClicked() {

        Log.d(TAG, "in onPlayClicked");
        if (!mMusicService.isPlaying()) {
            hidePlayButton();
            mMusicService.playMusic();
        }
    }

    @OnClick(R.id.pauseButton) public void onPauseClicked() {

        Log.d(TAG, "in onPauseClicked");
        if (mMusicService.isPlaying()) {
            showPlayButton();
            mMusicService.pauseMusic();
        }
    }

    private void showPlayButton() {
        setPlayPauseVisibility(true);
    }

    private void hidePlayButton() {
        setPlayPauseVisibility(false);
    }

    private void setPlayPauseVisibility(boolean showPlay) {

        int desiredPlayButtonState = showPlay ? View.VISIBLE : View.GONE;
        int desiredPauseButtonState = showPlay ? View.GONE : View.VISIBLE;
        if (mPlayButton.getVisibility() != desiredPlayButtonState)
            mPlayButton.setVisibility(desiredPlayButtonState);
        if (mPauseButton.getVisibility() != desiredPauseButtonState)
            mPauseButton.setVisibility(desiredPauseButtonState);
    }

    private void moveToTrack(boolean forward) {
        if (forward) {
            mPosition = (mPosition + 1 < mTracksList.size()) ? mPosition+1 : 0;
        } else {
            mPosition = mPosition == 0 ? mTracksList.size()-1 : mPosition-1;
        }
        playTrack(mTracksList.get(mPosition));
    }

    private void restoreInstanceState(Bundle savedState) {
        mTracksList = savedState.getParcelableArrayList(AppConstants.TOP_TRACKS);
        mArtistName = savedState.getString(AppConstants.ARTIST_NAME);
        mPosition = savedState.getInt(AppConstants.TRACK_POSITION);
    }


    private void startMusicService() {

        Intent musicService = new Intent(getActivity(), MusicService.class);
        musicService.putExtra(AppConstants.TRACK_ITEM, getCurrentTrack());
        getActivity().startService(musicService);
        getActivity().bindService(musicService, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void updatePlayerControls() {
        if (mMusicService != null) {
            if(mMusicService.isPlaying()) {
                mSeekBar.setMax(mMusicService.getDuration());
                mSeekBar.setProgress(mMusicService.getCurrentPosition());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updatePlayerControls();
                    }
                }, 20);

                // but sure play button is hidden
                hidePlayButton();
            } else {
                showPlayButton();
            }
        } else {

            showPlayButton();
        }
    }

}
