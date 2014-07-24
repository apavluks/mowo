package com.cummins.mowo.conrollers.timecardentry;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.conrollers.timecard.TimecardController;
import com.cummins.mowo.model.GlobalState;
import com.cummins.mowo.model.TimecardModel;

public class TimecardEntryUnlockedState extends TimecardEntryState implements OnInitListener, OnSharedPreferenceChangeListener{
	
	private static final String TAG = TimecardEntryUnlockedState.class.getSimpleName();
	private boolean allowClick = false;
	private boolean allowVoice = false;
	private boolean allowVibrate = false;
	private int voiceInterval = -1;
	private Vibrator vibrator;
	private TextToSpeech voice;
	private boolean voiceAvailable = false;
	private SoundPool soundPool;
	private float volume;
	private int clickId = -1;

	public TimecardEntryUnlockedState(TimecardEntryController controller) {
		super(controller);
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		setPrefs();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		setPrefs();
	}
	
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			Locale locale = new Locale(Locale.getDefault().getLanguage());
			int result = voice.setLanguage(locale);
			if (result == TextToSpeech.LANG_AVAILABLE) {
				voiceAvailable = true;
			} else {
				Log.i(TAG, "Language not available.");
			}
		} else {
			Log.i(TAG, "Voice service not available");
		}
		
	}	
	@Override
	public void dispose() {
		super.dispose();
		if (voice != null) {
			voice.stop();
			voice.shutdown();
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GlobalState.getContext());
		prefs.unregisterOnSharedPreferenceChangeListener(this);		
	}
	
	@Override
	public boolean handleMessage(int what) {
		switch(what) {
			default:
				return super.handleMessage(what);
		}
		
	}

	@Override
	public boolean handleMessage(int what, Object data) {
		switch(what) {
			case TimecardController.MESSAGE_UPDATE_LOCK:
				updateLock((Boolean)data);
				return true;
			default:
				return super.handleMessage(what, data);
		}
	}
	
	private void updateLock(boolean lock) {
		model.setLocked(lock);
		controller.setMessageState(new TimecardEntryLockedState(controller));
	}
	
	
	private void handleClickSensors(String speachstr) {
		
		vibrator.vibrate(100);
		
		if (allowVoice && voiceAvailable && voiceInterval > -1) {

				voice.speak(speachstr, TextToSpeech.QUEUE_FLUSH, null);		

		}
		if (allowClick && clickId > -1) {
			soundPool.play(clickId, volume, volume, 1, 0, 1);
		}
	}
	
	private void setPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GlobalState.getContext());
		prefs.registerOnSharedPreferenceChangeListener(this);
		
		allowClick = prefs.getBoolean("click", true);
		allowVoice = prefs.getBoolean("speak", true);
		allowVibrate = prefs.getBoolean("vibrate", true);
		voiceInterval = new Integer(prefs.getString("speak_interval", "-1"));
		volume = prefs.getInt("volume", 70);
		volume /= 100;
		
		if (allowVibrate) {
			vibrator = (Vibrator)GlobalState.getContext().getSystemService(Context.VIBRATOR_SERVICE);
		}

		Log.i(MainActivity.DEBUGTAG, "allowvoice " + allowVoice);
		if (allowVoice) {

			voice = new TextToSpeech(GlobalState.getContext(), this);
		}
		
		if (allowClick) {
			clickId = soundPool.load(GlobalState.getContext(), R.raw.klik, 1);
		}		
	}

	
}
