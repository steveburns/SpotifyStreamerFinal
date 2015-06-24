package com.myhub.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


/*

 THIS CLASS AND ASSOCIATED ACTIVITY ARE NOT YET COMPLETE AS THEY ARE TO BE DONE IN STAGE 2 OF THE SPOTIFY PROJECT.
 I GOT ANXIOUS AND BEGAN WORKING ON IT BEFORE I FULLY COMPLETED STAGE 1.
 PLEASE IGNORE FOR NOW!!!

 */

public class PlayerActivity extends AppCompatActivity {

    @InjectView(R.id.artistTextView) TextView artistTextView;
    @InjectView(R.id.albumTextView) TextView albumTextView;
    @InjectView(R.id.trackTextView) TextView trackTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        if (intent != null) {
            artistTextView.setText(intent.getStringExtra("artist"));
            albumTextView.setText(intent.getStringExtra("album"));
            trackTextView.setText(intent.getStringExtra("track"));

            ImageView ivUrl = (ImageView) this.findViewById(R.id.url);
            // Populate the image view with the artist image using picasso.
            String url = intent.getStringExtra("imageurl");
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(this)
                        .load(url)
                        .into(ivUrl);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }
}
