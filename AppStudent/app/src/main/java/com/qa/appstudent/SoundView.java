package com.qa.appstudent;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.qa.appstudent.com.qa.appstudent.sound.SoundClip;

import java.io.File;
import java.io.IOException;

public class SoundView extends LinearLayout {
	private boolean isRecording = false;
	private boolean isDoneRecording = false;
	private boolean isPlaying = false;
	private static final String FOLDER_NAME = "QA";
	private static final String ERROR_TAG = "SOUND RECORDING ERROR";


	private MediaRecorder recorder;
	private MediaPlayer player;
	private SoundClip currentSoundClip;


	private Button buttonVoice;

	private String getPath(String name) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		return path + "/" + FOLDER_NAME + "/" + name + ".3gp";
	}

	public SoundView(Context context) {
		super(context);
		init(context);
	}
	public SoundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.sound_view, this);

		//get the button
		buttonVoice = (Button)findViewById(R.id.btn_voice);

		//create folder if it doesn't exist
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), FOLDER_NAME);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		buttonVoice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isRecording && !isDoneRecording) {
					//can record
					boolean result = startRecording();
					if (result) {
						isRecording = true;
					}
				} else if (isRecording) {
					//stop now
					boolean result = stopRecording();
					if (result) {
						isRecording = false;
						isDoneRecording = true;
					}
				} else if (!isPlaying) {
					//play
					boolean result = playRecording();
					if (result) {
						isPlaying = true;
					}
				} else if (isPlaying) {
					//stop playing
					boolean result = stopPlaying();
					if (result) {
						isPlaying = false;
					}
				}
			}
		});
	}

	private boolean startRecording() {
		//try to record
		if (recorder == null) {
			recorder = new MediaRecorder();
		}
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
			Log.e(ERROR_TAG, e.getMessage(), e);
			return false;
		}

		buttonVoice.setText("STOP");
		return true;
	}

	private boolean stopRecording() {
		try {
			recorder.stop();
		} catch (Exception e) {
			Log.e(ERROR_TAG, e.getMessage(), e);
			return false;
		}

		if (currentSoundClip == null) {
			return false;
		}
		MediaPlayer tempPlayer = MediaPlayer.create(this.getContext(), Uri.parse(currentSoundClip.getPath()));
		currentSoundClip.setLength(tempPlayer.getDuration() / 1000);
		buttonVoice.setText("PLAY");
		return true;
	}

	private boolean playRecording() {
		if (player == null) {
			player = new MediaPlayer();
			try {
				player.setDataSource(currentSoundClip.getPath());
			} catch (Exception e) {
				Log.e(ERROR_TAG, e.getMessage(), e);
				return false;
			}
		}
		try {
			player.prepare();
		} catch (Exception e) {
			Log.e(ERROR_TAG, e.getMessage(), e);
			return false;
		}

		player.start();
		buttonVoice.setText("STOP");
		return true;
	}

	private boolean stopPlaying() {
		if (player == null) {
			return false;
		} else {
			player.stop();
		}

		buttonVoice.setText("PLAY");
		return true;
	}
}