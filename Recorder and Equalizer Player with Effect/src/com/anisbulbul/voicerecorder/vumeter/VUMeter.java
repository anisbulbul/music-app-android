package com.anisbulbul.voicerecorder.vumeter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.anisbulbul.voicerecorder.R;
import com.anisbulbul.voicerecorder.VoiceRecorderActivity;

public class VUMeter extends View {

	// attributes for analog vu meter
	static final float PIVOT_RADIUS = 3.5f;
	static final float PIVOT_Y_OFFSET = 10f;
	static final float SHADOW_OFFSET = 2.0f;
	static final float DROPOFF_STEP = 0.18f;
	static final float SURGE_STEP = 0.35f;
	static final long ANIMATION_INTERVAL = 70;

	// digital vu meter radius
	float radius = 0.0f;

	Paint mPaint, mShadow;
	float mCurrentAngle;

	VoiceRecorderActivity mRecorder;

	private Rect src;
	private Rect dst;

	// vu meter backgrounds
	private Bitmap bitmapBackgroundAnalog;
	private Bitmap bitmapBackgroundDefault;

	public VUMeter(Context context) {
		super(context);
		init(context);
	}

	public VUMeter(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	// initialization vu meter properties
	void init(Context context) {

		bitmapBackgroundAnalog = BitmapFactory.decodeResource(getResources(),
				R.drawable.vumeter_analog);
		bitmapBackgroundDefault = BitmapFactory.decodeResource(getResources(),
				R.drawable.vumeter_default);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
		mShadow.setColor(Color.argb(60, 0, 0, 0));

		mRecorder = null;

		mCurrentAngle = 0;
	}

	public void setRecorder(VoiceRecorderActivity recorder) {
		mRecorder = recorder;
		invalidate();
	}

	// draw vu meter method
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (new VoiceRecorderActivity().VUMeterIndex == 0) { // 0 for analog vu
																// meter

			final float minAngle = (float) Math.PI / 8;
			final float maxAngle = (float) Math.PI * 7 / 8;

			float angle = minAngle;
			if (mRecorder.recorder != null)
				angle += (float) (maxAngle - minAngle)
						* mRecorder.getMaxAmplitude() / 32768;

			if (angle > mCurrentAngle)
				mCurrentAngle = angle;
			else
				mCurrentAngle = Math.max(angle, mCurrentAngle - DROPOFF_STEP);

			mCurrentAngle = Math.min(maxAngle, mCurrentAngle);

			float w = getWidth();
			float h = getHeight();
			float pivotX = w / 2;
			float pivotY = h - PIVOT_RADIUS - PIVOT_Y_OFFSET;
			float l = h * 4 / 5;
			float sin = (float) Math.sin(mCurrentAngle);
			float cos = (float) Math.cos(mCurrentAngle);
			float x0 = pivotX - l * cos;
			float y0 = pivotY - l * sin;

			mPaint.setColor(Color.WHITE);

			src = new Rect(0, 0, bitmapBackgroundAnalog.getWidth(),
					bitmapBackgroundAnalog.getHeight());
			dst = new Rect(0, 0, (int) w, (int) h);

			canvas.drawBitmap(bitmapBackgroundAnalog, src, dst, null); // draw
																		// analog
																		// vu
																		// meter
																		// background

			canvas.drawLine(x0 + SHADOW_OFFSET, y0 + SHADOW_OFFSET, pivotX // draw
																			// analog
																			// vu
																			// meter
																			// line
																			// with
																			// shadow
					+ SHADOW_OFFSET, pivotY + SHADOW_OFFSET, mShadow);
			canvas.drawCircle(pivotX + SHADOW_OFFSET, pivotY + SHADOW_OFFSET, // draw
																				// analog
																				// vu
																				// meter
																				// circle
																				// with
																				// shadow
					PIVOT_RADIUS, mShadow);
			canvas.drawLine(x0, y0, pivotX, pivotY, mPaint); // draw analog vu
																// meter line
			canvas.drawCircle(pivotX, pivotY, PIVOT_RADIUS, mPaint); // draw
																		// analog
																		// vu
																		// meter
																		// circle

			if (mRecorder.recorder != null)
				postInvalidateDelayed(ANIMATION_INTERVAL);

		} else if (new VoiceRecorderActivity().VUMeterIndex == 1) { // 1 for
																	// digital
																	// vu meter
			float w = getWidth();
			float h = getHeight();
			float amplitude = 0;

			if (mRecorder.recorder != null)
				amplitude = (h * mRecorder.getMaxAmplitude() / 32768) / 2;

			radius += (amplitude - radius) / 3;

			// draw digital vu meter animation shadow colors
			for (float i = 44; i >= 0; i--) {
				mPaint.setColor(Color.rgb((int) i, (int) i, (int) i));
				float tempRadius = (i / 44) * h / 4;
				canvas.drawCircle(w / 2, h / 2, tempRadius, mPaint);
			}

			// draw digital vu meter animation shadow colors
			for (float i = 255; i >= 1; i--) {
				mPaint.setColor(Color.rgb((int) i, (int) i, 255));
				float tempRadius = (i / 255) * h / 2;
				if (tempRadius <= radius) {
					canvas.drawCircle(w / 2, h / 2, tempRadius, mPaint);
				}
			}
			if (mRecorder.recorder != null)
				postInvalidateDelayed(ANIMATION_INTERVAL);
		} else {
			// default background vu meter static
			float w = getWidth();
			float h = getHeight();
			src = new Rect(0, 0, bitmapBackgroundDefault.getWidth(),
					bitmapBackgroundDefault.getHeight());
			dst = new Rect(0, 0, (int) w, (int) h);
			canvas.drawBitmap(bitmapBackgroundDefault, src, dst, null);
		}
	}
}
