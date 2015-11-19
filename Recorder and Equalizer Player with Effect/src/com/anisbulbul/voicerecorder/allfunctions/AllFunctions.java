package com.anisbulbul.voicerecorder.allfunctions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import com.anisbulbul.voicerecorder.R;
import com.anisbulbul.voicerecorder.assets.Assets;
import com.anisbulbul.voicerecorder.play_list.ItemDetails;

@SuppressLint("SimpleDateFormat")
public class AllFunctions {

	private static SimpleDateFormat formatter;
	// audio list item details attributes
	public static Date lastModified;
	private static Context mContext;
	private static MediaPlayer mediaPlayer;
	public static MediaPlayer mp;

	public static ArrayList<ItemDetails> myRecordList; // audio list item array

	// method to return record list
	public static ArrayList<ItemDetails> getRecordList() {

		myRecordList = new ArrayList<ItemDetails>();
		myRecordList.clear();

		File sdCardRoot = Environment.getExternalStorageDirectory();
		File rootDir = new File(sdCardRoot, Assets.AUDIO_RECORDER_FOLDER);

		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}

		String fileName = "";
		String fileDuration = "";
		String fileDate = "";
		String fileSize = "00KB";
		
		int fileCount = rootDir.listFiles().length;
		if(fileCount == 0){
			

		    InputStream in = mContext.getResources().openRawResource(R.raw.sample_song);
		    FileOutputStream out = null;
			try {
				out = new FileOutputStream(rootDir.getAbsolutePath()+"/sample_song.mp3");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    byte[] buff = new byte[1024];
		    int read = 0;
		    try {
		        try {
					while ((read = in.read(buff)) > 0) {
					    out.write(buff, 0, read);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    } finally {
		        try {
					in.close();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		    }
		
			

		}


		for (File f : rootDir.listFiles()) {
			if (f.isFile()) {
				fileName = f.getName().toString(); // audio file name
				lastModified = new Date(f.lastModified()); // audio file
															// creation date
				formatter = new SimpleDateFormat("dd/MM/yyyy"); // audio file
																// creation date
																// format
				fileDate = formatter.format(lastModified); // audio file
															// creation date
															// final
				float fileSizeTemp = f.length() / 1024; // audio file size in KB
				if (fileSizeTemp >= 1024) {
					fileSizeTemp = fileSizeTemp / 1024;
					fileSize = String.format("%.02f", fileSizeTemp) + "MB";
				} else {
					fileSize = (int) fileSizeTemp + "KB";
				}

				try {

					mediaPlayer = MediaPlayer.create(
							mContext,
							Uri.parse(rootDir.getAbsolutePath() + "/"
									+ fileName));
					int timeDuration = mediaPlayer.getDuration() / 1000;
					fileDuration = String.format("%02d", timeDuration / 60)
							+ ":" + String.format("%02d", timeDuration % 60);

					myRecordList.add(new ItemDetails(fileName, fileSize,
							fileDate, fileDuration));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	
		
		return myRecordList; // return the record list
	}

	
	public static void setContext(Context context) {
		mContext = context;
	}
}
