package com.myhub.spotifystreamer.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import com.myhub.spotifystreamer.TrackItem;
import com.myhub.spotifystreamer.utils.AppConstants;
import com.myhub.spotifystreamer.utils.MediaPlayerSingleton;

import java.util.Arrays;

/**
 * Created by sburns on 7/10/15.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mMediaPlayer;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        mMediaPlayer = MediaPlayerSingleton.getInstance().getMediaPlayer();
        super.onCreate();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        Log.d("MusicService", "in onPrepared");
        mMediaPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
/*      Parcelable[] parcelables = intent.getParcelableArrayExtra(Constants.TRACKS);
        mTracks = Arrays.copyOf(parcelables, parcelables.length, ParcelableTrack[].class);
        mPosition = intent.getIntExtra(Constants.POSITION, 0);
        mSeek = intent.getIntExtra(Constants.SEEK, 0);
        mIsOrientationChange = intent.getBooleanExtra(Constants.ORIENTATION, false);
        mIsNowPlaying = intent.getBooleanExtra(Constants.NOW_PLAYING, false);
        playTrack(mTracks[mPosition]); */

        Log.d("MusicService", "in onStartCommand");
        playTrack((TrackItem) intent.getParcelableExtra(AppConstants.TRACK_ITEM));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void playTrack(TrackItem track) {
        try {
            Log.d("MusicService", "in playTrack");
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayerSingleton.getInstance().getMediaPlayer();
            }

            // Don't reset the media player if it's an orientation change or coming from the now playing button
 //           if (!mIsOrientationChange && !mIsNowPlaying) {
                mMediaPlayer.reset();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(track.getPreview_url());
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.prepareAsync();
 //           }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void seekTo (int pos){
        mMediaPlayer.seekTo(pos);
    }

    public void playMusic(){
        mMediaPlayer.start();
    }

    public void pauseMusic(){
        mMediaPlayer.pause();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
