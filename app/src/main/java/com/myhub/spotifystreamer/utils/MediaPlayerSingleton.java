package com.myhub.spotifystreamer.utils;

import android.media.MediaPlayer;

/**
 * Created by sburns on 7/10/15.
 */
public class MediaPlayerSingleton {
    private static MediaPlayerSingleton instance = null;
    private final MediaPlayer sMediaPlayer;
    protected MediaPlayerSingleton() {
        sMediaPlayer = new MediaPlayer();
    }
    public static MediaPlayerSingleton getInstance() {
        if(instance == null) {
            instance = new MediaPlayerSingleton();
        }
        return instance;
    }

    public MediaPlayer getMediaPlayer() {
        return sMediaPlayer;
    }
}