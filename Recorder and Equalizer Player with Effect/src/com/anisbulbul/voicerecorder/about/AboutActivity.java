package com.anisbulbul.voicerecorder.about;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.anisbulbul.voicerecorder.R;
import com.anisbulbul.voicerecorder.VoiceRecorderActivity;
import com.anisbulbul.voicerecorder.play_list.PlayListActivity;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about); // about this app layout file
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		// menu item for voice recorder home
		MenuItem voiceMenu = menu.findItem(R.id.voiceRecorder);
		voiceMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem mItem) {

						Intent i = new Intent(AboutActivity.this,
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

						Intent i = new Intent(AboutActivity.this,
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
		
		// menu item for back
		MenuItem exitMenu = menu.findItem(R.id.exit);
		exitMenu.setTitle(R.string.back_menu_item);
		exitMenu.setIcon(getResources().getDrawable(R.drawable.back_menu_icon));
		exitMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mItem) {

				Intent i = new Intent(AboutActivity.this,
						VoiceRecorderActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				return true;
			}
		});

		return true;
	}

}
