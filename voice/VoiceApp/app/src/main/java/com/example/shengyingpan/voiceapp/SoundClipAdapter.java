package com.example.shengyingpan.voiceapp;

import android.widget.*;
import android.view.*;
import android.content.*;
import java.util.*;
import android.media.*;
import android.util.Log;
import java.io.*;

public class SoundClipAdapter extends ArrayAdapter<SoundClip> {
	private MediaPlayer player;
	public SoundClipAdapter(Context context, ArrayList<SoundClip> clips, MediaPlayer player) {
		super(context, 0, clips);
		this.player = player;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final SoundClip clip = getItem(position);
		final SoundClipAdapter self = this;

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.sound_clip, parent, false);
       	}

       	TextView tvLength = (TextView) convertView.findViewById(R.id.clip_length);
       	tvLength.setText(String.valueOf(clip.getLength()) + " seconds");


       	//bind buttons
       	Button btnPlay = (Button)convertView.findViewById(R.id.btn_play);
       	Button btnDelete = (Button)convertView.findViewById(R.id.btn_delete);

       	btnPlay.setOnClickListener(new View.OnClickListener() {
       		@Override
       		public void onClick(View v) {
       			//play 
       			player.reset();
       			try {
       				player.setDataSource(clip.getPath());
       				player.prepare();
       				player.start();
       			} catch (IOException e) {
       				Log.e("playing back", "failed", e);
       			}
       		}
       	});

       	btnDelete.setOnClickListener(new View.OnClickListener() {
       		@Override
       		public void onClick(View v) {
       			//doing nothing for now
       			File file = new File(clip.getPath());
       			file.delete();
   				//now we can remove the item from the list
       			self.remove(clip);

       		}
       	});

       	return convertView;
	}
}