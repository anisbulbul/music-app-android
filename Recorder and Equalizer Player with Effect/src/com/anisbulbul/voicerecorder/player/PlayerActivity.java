package com.anisbulbul.voicerecorder.player;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.anisbulbul.voicerecorder.R;
import com.anisbulbul.voicerecorder.R.drawable;
import com.anisbulbul.voicerecorder.VoiceRecorderActivity;
import com.anisbulbul.voicerecorder.about.AboutActivity;
import com.anisbulbul.voicerecorder.allfunctions.AllFunctions;
import com.anisbulbul.voicerecorder.assets.Assets;
import com.anisbulbul.voicerecorder.play_list.PlayListActivity;

public class PlayerActivity extends Activity implements OnSeekBarChangeListener {

	private MediaPlayer mediaPlayer;
	private MediaPlayer[] soundEffect;
	private int effectIndex = 0;
	public TextView songName, duration;
	private double timeElapsed = 0, finalTime = 0;
	private Handler durationHandler = new Handler();
	private SeekBar seekbar;
	File sdCardRoot;
	File rootDir;
	
	ImageButton classicButton;
	ImageButton indoorButton;
	ImageButton roomButton;
	ImageButton smallRoomButton;
	ImageButton stadiumButton;
	ImageButton normalButton;
	ImageButton effectButton;
	
	ImageButton playButton;
	ImageButton deleteButton;
	private String tempName;
	double timeRemaining;
	int imageIDPlay = android.R.drawable.ic_media_play;
	int imageIDPause = android.R.drawable.ic_media_pause;
	private int iconID = drawable.alert_icon;

	
	private Equalizer mEqualizer;

	private LinearLayout band_layout;
	private LinearLayout button_layout;
	private TextView band_box_text;
	private TextView effect_box_text;
	
	private SeekBar[] bar;
	private LinearLayout lv;
	
	short min, max;
	short bands;
	private short[] bandsChange;
	
	private final String soundEffectText[] = { "RAIN", "DOGS", "DRUM", "GUITAR", "NONE" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		// set the layout of the PlayerActivity screen
		setContentView(R.layout.audio_record_player);
		
		AllFunctions.setContext(this);
	
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		band_layout = (LinearLayout) findViewById(R.id.band_layout);
		button_layout = (LinearLayout) findViewById(R.id.button_layout);
		
		// initialize views
		initializeViews();
		
			
	}

	public void initializeViews() {
		
		// audio effects initialization
		soundEffect = new MediaPlayer[4];
		soundEffect[0] = MediaPlayer.create(this, R.raw.rain);
		soundEffect[1] = MediaPlayer.create(this, R.raw.dog);
		soundEffect[2] = MediaPlayer.create(this, R.raw.drum);
		soundEffect[3] = MediaPlayer.create(this, R.raw.guitar);
		effectIndex = 4;
		
		// song name
		songName = (TextView) findViewById(R.id.songName);
		// sd card root
		sdCardRoot = Environment.getExternalStorageDirectory();
		// voice recorder root directory
		rootDir = new File(sdCardRoot, Assets.AUDIO_RECORDER_FOLDER);
		// initialize media player to play audio
		mediaPlayer = MediaPlayer.create(
				this,
				Uri.parse(rootDir.getAbsolutePath()
						+ "/"
						+ Assets.record_list_array.get(
								Assets.currentSongPosition).getVoiceName()));
		
		

		
		band_box_text = new TextView(this);
		band_box_text.setText("Sound Bands");
		band_layout.addView(band_box_text);
		
		setupEqualizer();
		mEqualizer.setEnabled(true);
						
		// audio item time duration
		duration = (TextView) findViewById(R.id.songDuration);
		// seekbar to dynamic display of duration
		seekbar = (SeekBar) findViewById(R.id.seekBar);

		// getting audio item time duration
		finalTime = mediaPlayer.getDuration();

		classicButton = (ImageButton) findViewById(R.id.classic);
		indoorButton = (ImageButton) findViewById(R.id.indoor);
		roomButton = (ImageButton) findViewById(R.id.room);
		smallRoomButton = (ImageButton) findViewById(R.id.small_room);
		stadiumButton = (ImageButton) findViewById(R.id.stadium);
		normalButton = (ImageButton) findViewById(R.id.normal);
		effectButton = (ImageButton) findViewById(R.id.btnEffect);
		
		// play button
		playButton = (ImageButton) findViewById(R.id.media_play);
		playButton.setImageResource(imageIDPlay);
		// delete button
		deleteButton = (ImageButton) findViewById(R.id.btnDelete);

		seekbar.setMax((int) finalTime);
		seekbar.setClickable(false);

		tempName = Assets.record_list_array.get(Assets.currentSongPosition)
				.getVoiceName(); // title audio with file format

		
		songName.setText(tempName.substring(0, tempName.length() - 4)); // audio
																		// title
																		// (
																		// removing
																		// file
																		// format
																		// )

		// get current position of duration
		timeElapsed = mediaPlayer.getCurrentPosition();
		// set seekbar progress
		seekbar.setProgress((int) timeElapsed);
		// set time remaing
		timeRemaining = finalTime - timeElapsed;
		// set duration remaining
		duration.setText(String.format(
				"%d min, %d sec",
				TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
				TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes((long) timeRemaining))));

		seekbar.setOnSeekBarChangeListener(this);

		playButton.setOnClickListener(btnClick);
		deleteButton.setOnClickListener(btnClick);
		
		classicButton.setOnClickListener(btnClick);
		indoorButton.setOnClickListener(btnClick);
		roomButton.setOnClickListener(btnClick);
		smallRoomButton.setOnClickListener(btnClick);
		stadiumButton.setOnClickListener(btnClick);
		normalButton.setOnClickListener(btnClick);
		effectButton.setOnClickListener(btnClick);


		
	}

	public void effect(short bandTemp, short maxTemp){
		mEqualizer.setBandLevel(bandTemp, maxTemp);
	}

	public void setupEqualizer() {
		mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
		mEqualizer.setEnabled(true);
		bands = mEqualizer.getNumberOfBands();
		bar = new SeekBar[bands];
		bandsChange = new short[bands];
		min = mEqualizer.getBandLevelRange()[0];
		max = mEqualizer.getBandLevelRange()[mEqualizer.getBandLevelRange().length-1];
		for (short i = 0; i < bands; i++) {
			final short band = i;
			bandsChange[i] = i;
			lv = new LinearLayout(this);
			lv.setOrientation(LinearLayout.HORIZONTAL);
			
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			param.weight = 1;
			bar[i] = new SeekBar(this);
			bar[i].setLayoutParams(param);
			bar[i].setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress));
	
			bar[i].setMax(max - min);
			bar[i].setProgress(mEqualizer.getBandLevel(band));
			bar[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					mEqualizer.setBandLevel(band, (short) (progress + min));

				}
			});
			
			lv.addView(bar[i]);
			band_layout.addView(lv);
		}

	}
	
	// button click listener
	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// play button listener
			case R.id.media_play: {

				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					playButton.setImageResource(imageIDPlay);
				} else {
					playButton.setImageResource(imageIDPause);
					mediaPlayer.start();
					timeElapsed = mediaPlayer.getCurrentPosition();
					seekbar.setProgress((int) timeElapsed);
					durationHandler.postDelayed(updateSeekBarTime, 100);
				}
				
				
				break;
			}
			// delete button listener
			case R.id.btnDelete: {
				confirmDeleteDialog(); // call delete confirmation alert box
				break;
			}
			
			// effect button listener
			case R.id.btnEffect: {
				displaySoundEffectDialog(); // call sound effect alert box
				break;
			}
			
			case R.id.classic: {
				
				effect((short)0, (short)(max/4));
				effect((short)1, (short)(max/4));
				effect((short)2, (short)(max/4));
				effect((short)3, (short)max);
				effect((short)4, (short)max);
				
				break;
			}
			
			case R.id.indoor: {
				
				effect((short)0, (short)max);
				effect((short)1, (short)max);
				effect((short)2, (short)max);
				effect((short)3, (short)0);
				effect((short)4, (short)0);
				
				break;
			}

			case R.id.room: {
				
				effect((short)0, (short)(max/2));
				effect((short)1, (short)(max/2));
				effect((short)2, (short)(max/2));
				effect((short)3, (short)(max/2));
				effect((short)4, (short)(max/2));
				
				break;
			}
			
			case R.id.stadium: {
				
				effect((short)0, (short)(max));
				effect((short)1, (short)(4*max/5));
				effect((short)2, (short)(3*max/5));
				effect((short)3, (short)(2*max/5));
				effect((short)4, (short)(max/5));
				
				break;
			}
			
			case R.id.normal: {
				
				effect((short)0, (short)(2*max/3));
				effect((short)1, (short)(2*max/3));
				effect((short)2, (short)(2*max/3));
				effect((short)3, (short)(2*max/3));
				effect((short)4, (short)(2*max/3));
				
				break;
			}
			
			case R.id.small_room: {
				
				effect((short)0, (short)(max/3));
				effect((short)1, (short)(max/3));
				effect((short)2, (short)(max/3));
				effect((short)3, (short)(max/3));
				effect((short)4, (short)(max/3));
				
				break;
			}
			
			}
		}
	};

	// back key press action, goes on the record list activity
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(mediaPlayer.isPlaying()){
				mediaPlayer.pause();
			}
			Intent i = new Intent(PlayerActivity.this, PlayListActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	// delete audio file method
	public void deleteAudio() {
		String deletePath = rootDir.getAbsolutePath()
				+ "/"
				+ Assets.record_list_array.get(Assets.currentSongPosition)
						.getVoiceName();
		File deleteFile = new File(deletePath);
		deleteFile.delete();
//		new PlayListActivity();
		Assets.record_list_array.remove(Assets.currentSongPosition);
		if (Assets.record_list_array.size() > 0) {
			if (Assets.record_list_array.size() <= Assets.currentSongPosition) {
				Assets.currentSongPosition = 0;
			}
			audioLoadToPlay(); // load audio to play after deleting
		} else {
			Assets.record_list_array = AllFunctions.getRecordList();
		}

	}

	// audio format changing dialog display
	private void displaySoundEffectDialog() {
		AlertDialog.Builder fileFormat = new AlertDialog.Builder(this);
		fileFormat.setIcon(iconID); // dialog icon
		fileFormat.setTitle("Choose Effect"); // dialog
																		// icon
		fileFormat.setSingleChoiceItems(soundEffectText, effectIndex,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(effectIndex >=0 && effectIndex <=3 && soundEffect[effectIndex].isPlaying()){
							soundEffect[effectIndex].stop();
						}
						effectIndex = which; // set current audio format
						dialog.dismiss(); // destroy dialog
					}
				}).show();
	}
	
	// handler to change seekBarTime
	private Runnable updateSeekBarTime = new Runnable() {
		public void run() {
			// get current position
			timeElapsed = mediaPlayer.getCurrentPosition();
			// set seekbar progress
			seekbar.setProgress((int) timeElapsed);
			// set time remaing
			timeRemaining = finalTime - timeElapsed;
			duration.setText(String.format(
					"%d min, %d sec",
					TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
					TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes((long) timeRemaining))));
			// repeat this that again in 100 miliseconds
			durationHandler.postDelayed(this, 100);

			if (!mediaPlayer.isPlaying()) {
				playButton.setImageResource(imageIDPlay); // play button image
			}
			if (timeElapsed >= finalTime) {
				seekbar.setProgress((int) finalTime); // seek to progressBar
			}
			if(effectIndex >=0 && effectIndex<=3){
				if(mediaPlayer.isPlaying()){
					if(!soundEffect[effectIndex].isPlaying()){
						soundEffect[effectIndex].start();
						soundEffect[effectIndex].setVolume(0.3f, 0.3f);
					}
				}
				else {
					if(soundEffect[effectIndex].isPlaying()){
						soundEffect[effectIndex].pause();
					}
				}				
			}
		}
	};

	// forward one
	public void forward(View view) {

		Assets.currentSongPosition++;
		if (Assets.record_list_array.size() <= Assets.currentSongPosition) {
			Assets.currentSongPosition = 0;

		}

		tempName = Assets.record_list_array.get(Assets.currentSongPosition)
				.getVoiceName();
		songName.setText(tempName.substring(0, tempName.length() - 4));

		mediaPlayer.stop();
		mediaPlayer.reset();
		mediaPlayer.release();
		mEqualizer.release();

		mediaPlayer = MediaPlayer.create(
				this,
				Uri.parse(rootDir.getAbsolutePath()
						+ "/"
						+ Assets.record_list_array.get(
								Assets.currentSongPosition).getVoiceName()));

//		setupEqualizer();
		mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
		mEqualizer.setEnabled(true);
		
		finalTime = mediaPlayer.getDuration();
		seekbar.setMax((int) finalTime);

		// get current position
		timeElapsed = mediaPlayer.getCurrentPosition();
		// set seekbar progress
		seekbar.setProgress((int) timeElapsed);
		// set time remaing

		timeRemaining = finalTime - timeElapsed;

		duration.setText(String.format(
				"%d min, %d sec",
				TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
				TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes((long) timeRemaining))));

	}

	// confirm delete alert box
	public void confirmDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(iconID);
		builder.setTitle("Delete");
		builder.setMessage("Are you sure?");
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteAudio();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	// audio loading method to play
	protected void audioLoadToPlay() {

		tempName = Assets.record_list_array.get(Assets.currentSongPosition)
				.getVoiceName();
		songName.setText(tempName.substring(0, tempName.length() - 4));

		mediaPlayer.stop();
		mediaPlayer.reset();
		mediaPlayer.release();
		mEqualizer.release();

		mediaPlayer = MediaPlayer.create(
				this,
				Uri.parse(rootDir.getAbsolutePath()
						+ "/"
						+ Assets.record_list_array.get(
								Assets.currentSongPosition).getVoiceName()));

//		setupEqualizer();
		mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
		mEqualizer.setEnabled(true);
		
		finalTime = mediaPlayer.getDuration();
		seekbar.setMax((int) finalTime);

		// get current position
		timeElapsed = mediaPlayer.getCurrentPosition();
		// set seekbar progress
		seekbar.setProgress((int) timeElapsed);
		// set time remaing

		timeRemaining = finalTime - timeElapsed;

		duration.setText(String.format(
				"%d min, %d sec",
				TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
				TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes((long) timeRemaining))));

	}

	// reward one
	public void rewind(View view) {

		Assets.currentSongPosition--;
		if (Assets.currentSongPosition < 0) {
			Assets.currentSongPosition = Assets.record_list_array.size() - 1;

		}
		tempName = Assets.record_list_array.get(Assets.currentSongPosition)
				.getVoiceName();
		songName.setText(tempName.substring(0, tempName.length() - 4));

		mediaPlayer.stop();
		mediaPlayer.reset();
		mediaPlayer.release();
		mEqualizer.release();

		mediaPlayer = MediaPlayer.create(
				this,
				Uri.parse(rootDir.getAbsolutePath()
						+ "/"
						+ Assets.record_list_array.get(
								Assets.currentSongPosition).getVoiceName()));

//		setupEqualizer();
		mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
		mEqualizer.setEnabled(true);
		
		finalTime = mediaPlayer.getDuration();
		seekbar.setMax((int) finalTime);

		// get current position
		timeElapsed = mediaPlayer.getCurrentPosition();
		// set seekbar progress
		seekbar.setProgress((int) timeElapsed);
		// set time remaing

		timeRemaining = finalTime - timeElapsed;

		duration.setText(String.format(
				"%d min, %d sec",
				TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
				TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes((long) timeRemaining))));

	}

	// Share the text
	public void launchShare() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent
				.putExtra(
						Intent.EXTRA_TEXT,
						getString(R.string.share_text)
								+ "Visit: http://play.google.com/store/apps/details?id="
								+ this.getPackageName());
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	// rated method
	public void launchMarket() {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ this.getPackageName())));
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, " Sorry, Not able to open!",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onProgressChanged(SeekBar agr0, int progress, boolean fromUser) {

		if (fromUser) {
			mediaPlayer.seekTo((int) progress);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		// menu item for home
		MenuItem voiceMenu = menu.findItem(R.id.voiceRecorder);
		voiceMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem mItem) {

						Intent i = new Intent(PlayerActivity.this,
								VoiceRecorderActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);

						return true;
					}
				});

		// menu item for record list
		MenuItem recordListMenu = menu.findItem(R.id.recordList);
		recordListMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem mItem) {

						Intent i = new Intent(PlayerActivity.this,
								PlayListActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);

						return true;
					}
				});

		// menu item for rate
		MenuItem rateMenu = menu.findItem(R.id.rate);
		rateMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mItem) {

				launchMarket();
				return true;

			}
		});
		// menu item for share
		MenuItem shareMenu = menu.findItem(R.id.share);
		shareMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem mItem) {

						launchShare();
						return true;

					}
				});

		// menu item for about screen
		MenuItem aboutMenu = menu.findItem(R.id.about);
		aboutMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem mItem) {

						Intent i = new Intent(PlayerActivity.this,
								AboutActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);

						return true;
					}
				});

		// menu item for back
		MenuItem exitMenu = menu.findItem(R.id.exit);
		exitMenu.setTitle(R.string.back_menu_item);
		exitMenu.setIcon(getResources().getDrawable(R.drawable.back_menu_icon));
		exitMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mItem) {

				Intent i = new Intent(PlayerActivity.this,
						PlayListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);

				return true;
			}
		});

		return true;
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// no need

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// Tno need

	}

}
