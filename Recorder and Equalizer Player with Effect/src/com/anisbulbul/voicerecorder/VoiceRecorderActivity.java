package com.anisbulbul.voicerecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.anisbulbul.voicerecorder.R.drawable;
import com.anisbulbul.voicerecorder.about.AboutActivity;
import com.anisbulbul.voicerecorder.allfunctions.AllFunctions;
import com.anisbulbul.voicerecorder.assets.Assets;
import com.anisbulbul.voicerecorder.play_list.PlayListActivity;
import com.anisbulbul.voicerecorder.player.PlayerActivity;
import com.anisbulbul.voicerecorder.vumeter.VUMeter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

@SuppressLint("WorldReadableFiles")
public class VoiceRecorderActivity extends Activity {

	private InterstitialAd interstitialAd;
	protected AdView adViewMain;
	protected View recorderView;
	AdView adView;
	RelativeLayout layout;
	private boolean IS_ADMOB_ACTIVE = true;
	private static final String AUDIO_RECORDER_FOLDER = Assets.AUDIO_RECORDER_FOLDER; // file
																						// store
																						// root
	private static final String AD_UNIT_ID_BANNER = "ca-app-pub-8606268298440779/7190429247";
	private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-8606268298440779/5713696049";
	// directory

	// audio formats
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp"; // 3gp
																		// file
																		// format
	private static final String AUDIO_RECORDER_FILE_EXT_MP3 = ".mp3"; // mp3
																		// file
																		// format
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4"; // mp4
																		// file
																		// format
	private static final String AUDIO_RECORDER_FILE_EXT_AMR = ".amr"; // amr
																		// file
																		// format
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav"; // wav
																		// file
																		// format

	// sampling rates : indicates audio quality
	private static int SAMPLING_RATE_PHONE = 8000; // 8KHz
	private static int SAMPLING_RATE_11 = 11000; // 11KHz
	private static int SAMPLING_RATE_16 = 15000; // 15KHz
	private static int SAMPLING_RATE_FM = 22000; // 22KHz
	private static int SAMPLING_RATE_32 = 32000; // 32KHz
	private static int SAMPLING_RATE_CD = 441000; // 44.1KHz

	// audio channels
	private int CHANNEL_MONO = 1; // mono channels (one side)
	private int CHANNEL_STEREO = 2; // two channels (two side)

	// app state default
	private int currentFormat = 0; // current format indicates
	private int currentChannel = 0; // current channel indicates
	private int currentSamplingRate = 0; // current sampling rate indicates
	public static int VUMeterIndex = 0; // Volume Unit Meter index for changing
										// meter (Analog = 0, Digital = 1,
										// Circle = 1)

	// timer attributes for controlling timer status
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	private long timeInMilliseconds = 0L;
	private long timeSwapBuff = 0L;
	private long updatedTime = 0L;
	// end timer attributes

	public static MediaRecorder recorder = null; // media recorder declaration

	// file formats caption value
	private String formats[] = { "AMR", "WAV", "MP3", "MPEG 4", "3GPP" };
	// file formats extension for output file
	private int output_formats[] = { MediaRecorder.OutputFormat.THREE_GPP,
			MediaRecorder.OutputFormat.THREE_GPP,
			MediaRecorder.OutputFormat.THREE_GPP,
			MediaRecorder.OutputFormat.MPEG_4,
			MediaRecorder.OutputFormat.THREE_GPP };
	// file formats extension for saving file
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_AMR,
			AUDIO_RECORDER_FILE_EXT_WAV, AUDIO_RECORDER_FILE_EXT_MP3,
			AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };

	// audio channels caption value
	private String channels[] = { "MONO", "STEREO" };
	// audio channels for output file
	private int output_channels[] = { CHANNEL_MONO, CHANNEL_STEREO };

	// audio channels sampling rate for alert dialog item
	private String samplingRate[] = { "8KHz (phone)", "11KHz (phone)",
			"16KHz (phone)", "22KHz (FM radio)", "32KHz (CD)", "44.1KHz (CD)" };
	// audio channels sampling rate for caption
	private String samplingRateCaption[] = { "8KHz", "11KHz", "16KHz", "22KHz",
			"32KHz", "44.1KHz" };
	// audio channels sampling rate for audio file
	private int output_samplingRate[] = { SAMPLING_RATE_PHONE,
			SAMPLING_RATE_11, SAMPLING_RATE_16, SAMPLING_RATE_FM,
			SAMPLING_RATE_32, SAMPLING_RATE_CD };

	// voice recorder buttons declaration
	private Button startButton; // start button
	private Button stopButton; // stop button
	private Button formatButton; // format change button
	private Button channelButton; // channel change button
	private Button samplingRateButton; // sampling rate change button
	private Button timerValue; // timer button
	private Button listButton; // list button
	private Button playerButton; // player button

	private String fileName; // output audio file path

	private File file2; // use to rename file

	private int iconID = drawable.alert_icon; // apps icon id

	private VUMeter mVUMeter; // Volume unit meter display class

	private String appStateFile = "app_state"; // internal status file name

	private int mins = 0, secs = 0; // timer minutes & seconds

	// runnable for displaying timer value
	private Runnable updateTimerThread = new Runnable() {

		public void run() {

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime; // update
																			// timer
																			// value

			updatedTime = timeSwapBuff + timeInMilliseconds;

			secs = (int) (updatedTime / 1000);

			mins = secs / 60;
			secs = secs % 60;

			timerValue.setText("" + String.format("%02d", mins) + ":"
					+ String.format("%02d", secs)); // set timer value to the
													// timer component

			customHandler.postDelayed(this, 0);
		}

	};

	// mediaRecorder error listener shows you a toast message to understand
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(VoiceRecorderActivity.this,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	// mediaRecorder info listener shows you a toast message to understand info
	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(VoiceRecorderActivity.this,
					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
					.show();
		}
	};

	// button actions
	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showOrLoadInterstital();
			switch (v.getId()) {
			case R.id.btnStart: {

				startRecording();

				timerValue.setEnabled(false);

				startButton.setEnabled(false);
				startButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				stopButton.setEnabled(true);
				stopButton.setTextColor(getResources().getColor(
						R.color.button_active_text_color));

				formatButton.setEnabled(false);
				formatButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				channelButton.setEnabled(false);
				channelButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				samplingRateButton.setEnabled(false);
				samplingRateButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				playerButton.setEnabled(false);
				playerButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				listButton.setEnabled(false);
				listButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);

				setTitle(getResources().getString((R.string.recording_state)));

				break;
			}

			case R.id.timerValue: {

				startRecording();

				timerValue.setEnabled(false);

				startButton.setEnabled(false);
				startButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				stopButton.setEnabled(true);
				stopButton.setTextColor(getResources().getColor(
						R.color.button_active_text_color));

				formatButton.setEnabled(false);
				formatButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				channelButton.setEnabled(false);
				channelButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				samplingRateButton.setEnabled(false);
				samplingRateButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				playerButton.setEnabled(false);
				playerButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				listButton.setEnabled(false);
				listButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);

				setTitle(getResources().getString((R.string.recording_state)));

				break;
			}

			case R.id.btnStop: {

				stopRecording();
				displayFileRenameDialog();

				timerValue.setEnabled(true);

				startButton.setEnabled(true);
				startButton.setTextColor(getResources().getColor(
						R.color.button_active_text_color));

				stopButton.setEnabled(false);
				stopButton.setTextColor(getResources().getColor(
						R.color.button_inactive_text_color));

				formatButton.setEnabled(true);
				formatButton.setTextColor(getResources().getColor(
						R.color.button_active_text_color));

				channelButton.setEnabled(true);
				channelButton.setTextColor(getResources().getColor(
						R.color.button_active_text_color));

				samplingRateButton.setEnabled(true);
				samplingRateButton.setTextColor(getResources().getColor(
						R.color.button_active_text_color));

				playerButton.setEnabled(true);
				playerButton.setTextColor(getResources().getColor(
						R.color.button_active_text_color));

				listButton.setEnabled(true);
				listButton.setTextColor(getResources().getColor(
						R.color.button_active_text_color));

				timeInMilliseconds = 0L;
				timeSwapBuff = 0L;
				updatedTime = 0L;
				startTime = 0L;

				timeSwapBuff += timeInMilliseconds;
				customHandler.removeCallbacks(updateTimerThread);

				setTitle(getResources().getString((R.string.app_name)));

				break;
			}
			case R.id.btnFormat: {
				displayFormatDialog();
				break;
			}
			case R.id.vuMeter: {
				VUMeterIndex = (VUMeterIndex + 1) % 2;
				saveState();
				break;
			}
			case R.id.btnChannel: {
				displayAudioChannelDialog();
				break;
			}
			case R.id.btnSamplingRate: {
				displaySamplingRateDialog();
				break;
			}
			case R.id.btnList: {
				Intent i = new Intent(VoiceRecorderActivity.this,
						PlayListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				break;
			}
			case R.id.btnPlayer: {

				Intent i = new Intent(VoiceRecorderActivity.this,
						PlayerActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				break;
			}
			}
		}
	};

	// audio channel changing dialog display
	private void displayAudioChannelDialog() {
		AlertDialog.Builder audioChannel = new AlertDialog.Builder(this);
		audioChannel.setIcon(iconID); // dialog icon
		audioChannel.setTitle("Select Channel"); // dialog title
		audioChannel.setSingleChoiceItems(channels, currentChannel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						currentChannel = which;
						setChannelButtonCaption(); // method to change channel
													// button caption
						saveState(); // save app state
						dialog.dismiss(); // destroy dialog
					}
				}).show();
	}

	// audio file rename dialog display
	private void displayFileRenameDialog() {
		AlertDialog.Builder renameFile = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setHint(R.string.rename_hint);
		renameFile.setView(input);
		renameFile.setIcon(iconID); // dialog icon
		renameFile.setTitle(R.string.rename_dialog); // dialog icon
		renameFile.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Assets.record_list_array = AllFunctions.getRecordList();// save
																				// record
																				// list
																				// in
																				// Assets
						String tempName = input.getText().toString().trim();
						if (!tempName.equals("")) {
							setFileRename(tempName); // audio file rename with
														// the name of tempName
						}
					}
				});
		renameFile.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
						Assets.record_list_array = AllFunctions.getRecordList();// save
																				// record
																				// list
																				// in
																				// Assets
					}
				});
		renameFile.show();
	}

	// audio format changing dialog display
	private void displayFormatDialog() {
		AlertDialog.Builder fileFormat = new AlertDialog.Builder(this);
		fileFormat.setIcon(iconID); // dialog icon
		fileFormat.setTitle(getString(R.string.choose_format_title)); // dialog
																		// icon
		fileFormat.setSingleChoiceItems(formats, currentFormat,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						currentFormat = which; // set current audio format
						setFormatButtonCaption(); // method to change format
													// button caption
						saveState(); // save app state
						dialog.dismiss(); // destroy dialog
					}
				}).show();
	}

	// audio sampling rate changing dialog display
	private void displaySamplingRateDialog() {
		AlertDialog.Builder sampleRate = new AlertDialog.Builder(this);
		sampleRate.setIcon(iconID); // dialog icon
		sampleRate.setTitle(getString(R.string.sampling_rate)); // dialog title
		sampleRate.setSingleChoiceItems(samplingRate, currentSamplingRate,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						currentSamplingRate = which; // set current audio
														// sampling rate
						setSampleingRateButtonCaption(); // method to change
															// samplingRate
															// button caption
						saveState(); // save app state
						dialog.dismiss(); // destroy dialog
					}
				}).show();
	}

	// audio file name creating and return it
	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath(); // sd
																				// card
																				// root
		File file = new File(filepath, AUDIO_RECORDER_FOLDER); // app directory
																// root
		if (!file.exists()) { // check if directory is exist
			file.mkdirs(); // if not exists then create it
		}
		fileName = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]); // audio
																											// file
																											// path
																											// to
																											// save
																											// audio
																											// file
		file2 = new File(fileName); // creating file object
		return fileName; // return audio file path
	}

	// return recorder maximum amplitude for running VUMeter
	public int getMaxAmplitude() {
		if (startButton.isEnabled())
			return 0;
		return recorder.getMaxAmplitude();
	}

	// initilize all components
	private void initializeComponents() {

		// get components from layout
		mVUMeter = (VUMeter) findViewById(R.id.vuMeter);
		startButton = (Button) findViewById(R.id.btnStart);
		stopButton = (Button) findViewById(R.id.btnStop);
		formatButton = (Button) findViewById(R.id.btnFormat);
		channelButton = (Button) findViewById(R.id.btnChannel);
		samplingRateButton = (Button) findViewById(R.id.btnSamplingRate);
		timerValue = (Button) findViewById(R.id.timerValue);
		listButton = (Button) findViewById(R.id.btnList);
		playerButton = (Button) findViewById(R.id.btnPlayer);

		// set buttons click listener
		startButton.setOnClickListener(btnClick);
		stopButton.setOnClickListener(btnClick);
		formatButton.setOnClickListener(btnClick);
		channelButton.setOnClickListener(btnClick);
		samplingRateButton.setOnClickListener(btnClick);
		timerValue.setOnClickListener(btnClick);
		listButton.setOnClickListener(btnClick);
		playerButton.setOnClickListener(btnClick);
		mVUMeter.setOnClickListener(btnClick);

		// set button enable
		startButton.setEnabled(true);
		timerValue.setEnabled(true);
		stopButton.setEnabled(false);
		formatButton.setEnabled(true);
		channelButton.setEnabled(true);
		samplingRateButton.setEnabled(true);
		listButton.setEnabled(true);
		playerButton.setEnabled(true);

		// set VUMete enable
		mVUMeter.setEnabled(true);
		mVUMeter.setRecorder(this);

	}

	public void addLayout() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (interstitialAd.isLoaded()) {
						layout.addView(adView);
					}
				}
			});
		} catch (Exception e) {
		}
	}
	
	public void showOrLoadInterstital() {
		if (IS_ADMOB_ACTIVE) {

			try {
				runOnUiThread(new Runnable() {

					public void run() {

						addLayout();
						
						if (interstitialAd.isLoaded()) {

							interstitialAd.show();
							// layout.addView(admobView);
//							Toast.makeText(getApplicationContext(),
//									"Showing Interstitial", Toast.LENGTH_SHORT)
//									.show();
						} else {
							AdRequest interstitialRequest = new AdRequest.Builder()
									.build();
							interstitialAd.loadAd(interstitialRequest);
//							Toast.makeText(getApplicationContext(),
//									"Loading Interstitial", Toast.LENGTH_SHORT)
//									.show();
						}
					}
				});
			} catch (Exception e) {
			}

		}
	}

	private AdView createAdView() {
		this.adViewMain = new AdView(this);
		this.adViewMain.setAdSize(AdSize.SMART_BANNER);
		this.adViewMain.setAdUnitId(AD_UNIT_ID_BANNER);
		this.adViewMain.setId(12345);
		LayoutParams var1 = new LayoutParams(-1, -2);
		var1.addRule(12, -1);
		var1.addRule(14, -1);
		this.adViewMain.setLayoutParams(var1);
		this.adViewMain.setBackgroundColor(-16777216);
		return this.adViewMain;
	}

	private View createRecorderView() {
		LayoutInflater inflater = getLayoutInflater();
//		this.recorderView = (LinearLayout) findViewById(R.id.recorder_acticity);
		this.recorderView = inflater.inflate(R.layout.audio_recorder_main, null);
		LayoutParams var2 = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		var2.addRule(10, -1);
		var2.addRule(14, -1);
		var2.addRule(2, this.adViewMain.getId());
		this.recorderView.setLayoutParams(var2);
		return this.recorderView;
	}

	private void startAdvertising(AdView var1) {
		var1.loadAd((new AdRequest.Builder()).build());
	}
	
	// app activity method
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 setContentView(R.layout.audio_recorder_main); // set to your layout
		// file
		
		layout = new RelativeLayout(this);
		layout.setLayoutParams(new LayoutParams(-1, -1));

		adView = this.createAdView();
		// layout.addView(adView);
//		recorderView = (LinearLayout) findViewById(R.id.recorder_acticity);
		
		layout.addView(createRecorderView());

		this.setContentView(layout);
		this.startAdvertising(adView);
		
//		 final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
//		
//		 String deviceId = tm.getDeviceId();
//		
//		 AdRequest adRequest = new AdRequest.Builder()
//		 .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//		 .addTestDevice(deviceId)
//		 .build();
//		
//		 adView = (AdView) findViewById(R.id.adView);
//		 adView.loadAd(adRequest);
		

		AllFunctions.setContext(this);
		Assets.record_list_array = AllFunctions.getRecordList();// save record
																// list in
																// Assets
		loadState(); // load app state

		initializeComponents();

		setFormatButtonCaption(); // set format button caption
		setChannelButtonCaption(); // set channel button caption
		setSampleingRateButtonCaption(); // set samplingRate button caption

		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
//				Toast.makeText(getApplicationContext(),
//						"Finished Loading Interstitial", Toast.LENGTH_SHORT)
//						.show();
			}

			@Override
			public void onAdClosed() {
//				Toast.makeText(getApplicationContext(), "Closed Interstitial",
//						Toast.LENGTH_SHORT).show();
			}
		});
		
		showOrLoadInterstital();

	}

	// creating app menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflater = getMenuInflater();
		menuinflater.inflate(R.menu.main, menu);

		// menu item for record list
		MenuItem recordListMenu = menu.findItem(R.id.recordList);
		recordListMenu.setIcon(drawable.list_menu_icon);
		recordListMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem mItem) {

						Intent i = new Intent(VoiceRecorderActivity.this,
								PlayListActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);

						return true;
					}
				});

		// menu item for about app
		MenuItem aboutMenu = menu.findItem(R.id.about);
		aboutMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem mItem) {

						Intent i = new Intent(VoiceRecorderActivity.this,
								AboutActivity.class);
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

		// menu item for exit from app
		MenuItem exitMenu = menu.findItem(R.id.exit);
		exitMenu.setTitle(R.string.exit_menu_item);
		exitMenu.setIcon(getResources().getDrawable(R.drawable.exit_menu_icon));
		exitMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mItem) {

				System.exit(0);
				finish();

				return true;
			}
		});

		return true;
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

	// method to load app state
	public void loadState() {
		try {

			FileInputStream fin = openFileInput(appStateFile);

			int c;
			String temp = "";
			while ((c = fin.read()) != -1) {
				temp = temp + Character.toString((char) c);
			}

			String tempState[] = temp.split(" ");

			currentFormat = Integer.parseInt(tempState[0].trim());
			currentChannel = Integer.parseInt(tempState[1].trim());
			currentSamplingRate = Integer.parseInt(tempState[2].trim());
			VUMeterIndex = Integer.parseInt(tempState[3].trim());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// method to save app state in appStateFile internal file
	public void saveState() {

		try {
			FileOutputStream fOut = openFileOutput(appStateFile,
					MODE_WORLD_READABLE);
			fOut.write((currentFormat + " " + currentChannel + " "
					+ currentSamplingRate + " " + VUMeterIndex).getBytes());
			fOut.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// audio file rename method
	private void setFileRename(String name) {

		String filepath2 = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath2, AUDIO_RECORDER_FOLDER);
		file2.renameTo(new File(file.getAbsolutePath() + "/" + name
				+ file_exts[currentFormat]));

	}

	// channel button caption changing button
	private void setChannelButtonCaption() {
		channelButton.setText(channels[currentChannel]);
	}

	// audio format caption changing button
	private void setFormatButtonCaption() {
		formatButton.setText(formats[currentFormat]);
	}

	// sampling rate button caption changing button
	private void setSampleingRateButtonCaption() {
		samplingRateButton.setText(samplingRateCaption[currentSamplingRate]);
	}

	// record audio method
	private void startRecording() {

		timerValue.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.timer_on)); // set timer background time_on

		recorder = new MediaRecorder();

		// set recording properties
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // audio source
		recorder.setOutputFormat(output_formats[currentFormat]); // audio format
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // audio
																		// encoder
		recorder.setOutputFile(getFilename()); // audio file name
		recorder.setAudioSamplingRate(output_samplingRate[currentSamplingRate]); // sampling
																					// rate
																					// means
																					// quality

		if (Build.VERSION.SDK_INT < 10) { // checking android version for not
											// supported audio sampling rate
			recorder.setAudioChannels(output_channels[0]);
		} else {
			recorder.setAudioChannels(output_channels[currentChannel]);
		}
		recorder.setOnErrorListener(errorListener); // recorder error listener
		recorder.setOnInfoListener(infoListener); // recorder info listener

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mVUMeter.setRecorder(this); // VUMeter setting

	}

	// stop audio recording
	private void stopRecording() {
		timerValue.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.timer_off)); // set timer background time_on
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();

			recorder = null;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}

	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}
}