package com.example.shengyingpan.voiceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.app.*;
import android.graphics.drawable.*;
import android.graphics.*;
import java.util.*;
import android.media.*;
import java.lang.System;
import java.io.*;
import android.util.Log;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {

    private Dialog popup;
    private boolean isRecording = false;
    private SoundClipAdapter adapter;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private SoundClip currentSoundClip;

    private static final String LOG_TAG = "VoiceApp";
    private static final String LIST_KEY = "Clip";
    private static final String FOLDER = "VoiceApp";


    private String getPath(String name) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FOLDER;
        if (name.length() > 0) {
            path += "/" + name + ".3gp";
        }
        return path;
    }

    private void startRecording() {
        popup.show();

        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        String fileName = String.valueOf(System.currentTimeMillis());
        String filePath = getPath(fileName);

        currentSoundClip = new SoundClip(fileName, filePath, 0);

        recorder.setOutputFile(filePath);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare failed", e);
        }



    }

    private void endRecording() {
        recorder.stop();


        //get clip length
        MediaPlayer tempPlayer = MediaPlayer.create(this, Uri.parse(currentSoundClip.getPath()));
        currentSoundClip.setLength(tempPlayer.getDuration() / 1000);

        adapter.add(currentSoundClip);
        popup.dismiss();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        File folder = new File(getPath(""));
        if (!folder.exists()) {
            folder.mkdir();
        }

        recorder = new MediaRecorder();
        player = new MediaPlayer();

        popup = new Dialog(this);
        popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup.setContentView(R.layout.popup);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        //bind list
        ListView lv = (ListView)findViewById(R.id.list);
        adapter = new SoundClipAdapter(this, new ArrayList<SoundClip>(), player);
        lv.setAdapter(adapter);

        //button stuff
        Button recordButton = (Button) findViewById(R.id.btn_record);
        recordButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motion) {
                int action = motion.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startRecording();
                } else if (action == MotionEvent.ACTION_UP) {
                    endRecording();
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
