package com.anisbulbul.voicerecorder.play_list;

import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.anisbulbul.voicerecorder.R;
import com.anisbulbul.voicerecorder.VoiceRecorderActivity;
import com.anisbulbul.voicerecorder.about.AboutActivity;
import com.anisbulbul.voicerecorder.allfunctions.AllFunctions;
import com.anisbulbul.voicerecorder.assets.Assets;
import com.anisbulbul.voicerecorder.player.PlayerActivity;

public class PlayListActivity extends Activity {
	private ListItemAdapter adapter;
	EditText editsearch;
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
	
	// back key press action, goes on the record list activity
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent i = new Intent(PlayListActivity.this, VoiceRecorderActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return false;
		}
		return super.onKeyDown(keyCode, event);
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

	// Called when the activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.audio_record_list);
		setTitle("Record List of RAEPWE");
		AllFunctions.setContext(this);

		Assets.record_list_array = AllFunctions.getRecordList(); // get record list

		editsearch = (EditText) findViewById(R.id.inputSearch);
		ListView lv1 = (ListView) findViewById(R.id.listV_main); // record
																		// listView
																		// layout

		adapter = new ListItemAdapter(this, Assets.record_list_array);
		lv1.setAdapter(adapter); // recored list
																// Adapter

//		editsearch.setFocusableInTouchMode(false);
//		
//		editsearch.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				editsearch.setFocusableInTouchMode(true);
//				
//			}
//		});
		
		// Capture Text in EditText
		editsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
				String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
				adapter.filter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}
			

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
			
			
		});
		
		// record list item action to play screen
		lv1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				Assets.currentSongPosition = position;

				Intent i = new Intent(PlayListActivity.this,
						PlayerActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
	}

	// menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		// menu item for home
		MenuItem voiceMenu = menu.findItem(R.id.voiceRecorder);
		voiceMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem mItem) {

						Intent i = new Intent(PlayListActivity.this,
								VoiceRecorderActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);

						return true;
					}
				});

		// menu item for about screen
		MenuItem aboutMenu = menu.findItem(R.id.about);
		aboutMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem mItem) {

						Intent i = new Intent(PlayListActivity.this,
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

		// menu item for back
		MenuItem exitMenu = menu.findItem(R.id.exit);
		exitMenu.setTitle(R.string.back_menu_item);
		exitMenu.setIcon(getResources().getDrawable(R.drawable.back_menu_icon));
		exitMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mItem) {

				Intent i = new Intent(PlayListActivity.this,
						VoiceRecorderActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);

				return true;
			}
		});

		return true;
	}

}
