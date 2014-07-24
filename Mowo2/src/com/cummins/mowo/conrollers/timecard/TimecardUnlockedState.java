package com.cummins.mowo.conrollers.timecard;

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
import android.text.format.Time;
import android.util.Log;

import com.cummins.mowo.R;
import com.cummins.mowo.activity.MainActivity;
import com.cummins.mowo.model.GlobalState;
import com.cummins.mowo.model.TimecardModel;
import com.cummins.mowo.vos.TimecardEntry;

public class TimecardUnlockedState extends TimecardState implements OnInitListener, OnSharedPreferenceChangeListener{
	
	private static final String TAG = TimecardUnlockedState.class.getSimpleName();
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

	public TimecardUnlockedState(TimecardController controller) {
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
			case TimecardController.MESSAGE_CLOCK_IN:
				 handleClockIn();
				 return true;
			case TimecardController.MESSAGE_CLOCK_OUT:
				 handleClockOut();
				 return true;		
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
			case TimecardController.MESSAGE_END_ENTRY:
			     handleEndEntry((TimecardEntry) data);
				 return true;	
			case TimecardController.MESSAGE_ADD_ENTRY:
			     handleAddEntry((TimecardEntry) data);
				 return true;	
			default:
				return super.handleMessage(what, data);
		}
	}
	
	private void updateLock(boolean lock) {
		model.setLocked(lock);
		controller.setMessageState(new TimecardLockedState(controller));
	}
	
	@Override
	public void handleAddEntry(TimecardEntry entry) {
		int tcStatus = model.getTimecardStatus();
		
		if (tcStatus == TimecardModel.TIMESHEET_STATUS_ENDED) {
			controller.notifyOutboxHandlers(TimecardController.MESSAGE_SHOW_TOAST, R.string.clockbtn_already_clocked_out, 0, null);
		} else if (model.getTimecardEntryList() != null) {
			// if first entry then treat it as clock in in case person has not clocked in yet. 
			if (model.getTimecardEntryListCount() == 0 && tcStatus == TimecardModel.TIMESHEET_STATUS_NOTSTARTED) {
				controller.notifyOutboxHandlers(TimecardController.MESSAGE_SHOW_TOAST, R.string.clockbtn_not_started_yet, 0, null);
			} else if (tcStatus == TimecardModel.TIMESHEET_STATUS_STARTED) {
				if (1==2) { // model.isAnyActivityRunning()) {
					controller.notifyOutboxHandlers(TimecardController.MESSAGE_SHOW_TOAST, R.string.activity_end_running_activities, 0, null); 	
				} else {
				   model.addTimecardEntry(entry);
				   String str = GlobalState.getContext().getResources().getString(R.string.timecard_add_activity_action_speach);
				   //controller.notifyOutboxHandlers(TimecardController.MESSAGE_SAVE_NOW, 0, 0, null);
				   super.handleAddEntry(entry);
				   handleClickSensors(str);
				}

			}
		}
	}	
	
	private void handleEndEntry(TimecardEntry entry) {
		if (entry.getTimeEndString() != null) {
			controller.notifyOutboxHandlers(TimecardController.MESSAGE_SHOW_TOAST, R.string.activity_already_ended, 0, null);		
		}
		entry.setTimeEndNow();
		controller.notifyOutboxHandlers(TimecardController.MESSAGE_SAVE_NOW, 0, 0, null);
	}
	
	private void handleClockIn() {
		
		int tcStatus = model.getTimecardStatus();
		
		if (tcStatus == TimecardModel.TIMESHEET_STATUS_STARTED) {
			controller.notifyOutboxHandlers(TimecardController.MESSAGE_SHOW_TOAST, R.string.clockbtn_already_clocked_in, 0, null);	
		} else if (tcStatus == TimecardModel.TIMESHEET_STATUS_ENDED) {
			controller.notifyOutboxHandlers(TimecardController.MESSAGE_SHOW_TOAST, R.string.clockbtn_already_clocked_out, 0, null);	
		} else if (tcStatus == TimecardModel.TIMESHEET_STATUS_NOTSTARTED) {
			model.setTimeInNow();
			model.setTimecardStatus(TimecardModel.TIMESHEET_STATUS_STARTED);
			//model.addEmptyEntry();
			controller.notifyOutboxHandlers(TimecardController.MESSAGE_SAVE_NOW, 0, 0, null);
			String speachstr = GlobalState.getContext().getResources().getString(R.string.speach_clocked_in);
			speachstr = speachstr + " " + model.getClockInTimeString();
			handleClickSensors(speachstr);

		} 
	}
	
	private void handleClockOut() {
		
		 int tcStatus = model.getTimecardStatus();
		 
	     if (tcStatus == TimecardModel.TIMESHEET_STATUS_ENDED) {
	    	 controller.notifyOutboxHandlers(TimecardController.MESSAGE_SHOW_TOAST, R.string.clockbtn_already_clocked_out, 0, null);	
	     } else if (tcStatus == TimecardModel.TIMESHEET_STATUS_NOTSTARTED) {
	    	 controller.notifyOutboxHandlers(TimecardController.MESSAGE_SHOW_TOAST, R.string.clockbtn_not_started_yet, 0, null);	
	     } else if (tcStatus == TimecardModel.TIMESHEET_STATUS_STARTED) {
		    model.setTimeOutNow();
		    model.setTimecardStatus(TimecardModel.TIMESHEET_STATUS_ENDED);
		   // model.addEmptyEntry();
		    model.stopActiveEntry();
		    controller.notifyOutboxHandlers(TimecardController.MESSAGE_SAVE_NOW, 0, 0, null);
		    String speachstr = GlobalState.getContext().getResources().getString(R.string.speach_clocked_out);
		    speachstr = speachstr + " " + model.getClockOutTimeString();
		    handleClickSensors(speachstr);

		} 
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
