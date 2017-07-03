
package com.echo.reactandroidtts;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Map;
import java.util.Locale;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

public class RNAndroidTextToSpeechModule extends ReactContextBaseJavaModule {

	private static TextToSpeech tts;
	private boolean IS_READY;

	private static boolean IS_DUCKING = false;
	private AudioManager audioManager;
	private AudioManager.OnAudioFocusChangeListener afChangeListener;

	public RNAndroidTextToSpeechModule(ReactApplicationContext reactContext) {
		super(reactContext);
		audioManager = (AudioManager) reactContext.getApplicationContext().getSystemService(reactContext.AUDIO_SERVICE);
		tts = new TextToSpeech(getReactApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.SUCCESS) {
					IS_READY = false;
				} else {
					IS_READY = true;
				}
			}
		});

		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
			@Override 
			public void onStart(String utteranceId) {
				sendEvent("tts-start", utteranceId);
			}

			@Override
			public void onDone(String utteranceId) {
				if(IS_DUCKING) {
					audioManager.abandonAudioFocus(afChangeListener);
				}
				sendEvent("tts-done", utteranceId);
			}

			@Override
			public void onError(String utteranceId) {
				if(IS_DUCKING) {
					audioManager.abandonAudioFocus(afChangeListener);
				}
				sendEvent("tts-error", utteranceId);
			}

			@Override
			public void onStop(String utteranceId, boolean interrupted) {
				if(IS_DUCKING) {
					audioManager.abandonAudioFocus(afChangeListener);
				}
				sendEvent("tts-stopped", utteranceId);
			}
		});
	}

	@Override
	public String getName() {
		return "AndroidTextToSpeech";
	}

	@ReactMethod
	public void speak(String utterance, String queueMode, Promise promise) {
		if(notReady(promise)) return;

		if(IS_DUCKING) {
			int amResult = audioManager.requestAudioFocus(afChangeListener, 
															AudioManager.STREAM_MUSIC, 
															AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

			if(amResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
				promise.reject("error", "Android AudioManager error, failed to request audio focus");
		}

		String utteranceId = Integer.toString(utterance.hashCode());

		int mode = TextToSpeech.QUEUE_ADD;
		if(queueMode.equals("ADD")) 
			mode = TextToSpeech.QUEUE_ADD;
		else if(queueMode.equals("FLUSH")) 
			mode = TextToSpeech.QUEUE_FLUSH;

		int speakResult = speak(utterance, mode, utteranceId);
		if(speakResult == TextToSpeech.SUCCESS) {
			promise.resolve(utteranceId);
		} else {
			promise.reject("error", "Unable to play. Error at speak(String utterance, String queueMode)");
		}
	}

	@ReactMethod
	public void stop(Promise promise) {
		if(notReady(promise)) return;

		int result = tts.stop();
		if(result == TextToSpeech.SUCCESS) {
			promise.resolve("success");
		} else {
			promise.reject("error", "Unknown error code at stop()");
		}
	}

	@ReactMethod 
	public void getAvailableLocales(Promise promise) {
		if(notReady(promise)) return;
		
		try {
			WritableArray localeList = Arguments.createArray();
			Locale[] localesArray = Locale.getAvailableLocales();
			for(Locale locale: localesArray) {
				int isAvailable = tts.isLanguageAvailable(locale);
				if(isAvailable == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
					WritableMap newLocale = returnMapForLocale(locale);
					localeList.pushMap(newLocale);
				}
			}

			promise.resolve(localeList);
		} catch(Exception e) {
			promise.reject("error", "Unable to retrieve locales for getAvailableLocales()", e);
		}
	}

	@ReactMethod
	public void getDefaultLocale(Promise promise) {
		if(notReady(promise)) return;
		
		try {
			Locale defaultLocale;

			if(Build.VERSION.SDK_INT >= 21)	
				defaultLocale = tts.getDefaultVoice().getLocale();
			else 
				defaultLocale = tts.getDefaultLanguage();

			WritableMap map = returnMapForLocale(defaultLocale);
			promise.resolve(map);
		} catch(Exception e) {
			promise.reject("error", "Unable to retrieve locale for getDefaultLocale()", e);
		}
	}

	@ReactMethod
	public void getCurrentLocale(Promise promise) {
		if(notReady(promise)) return;
		
		try {
			Locale currentLocale;

			if(Build.VERSION.SDK_INT >= 21)	
				currentLocale = tts.getVoice().getLocale();
			else 
				currentLocale = tts.getLanguage();

			WritableMap map = returnMapForLocale(currentLocale);
			promise.resolve(map);
		} catch(Exception e) {
			promise.reject("error", "Unable to retrieve locale for getCurrentLocale()", e);
		}
	}

	@ReactMethod 
	public void getCurrentVoice(Promise promise) {
		if(notReady(promise)) return;

		try {
			if(Build.VERSION.SDK_INT >= 21) {
				Voice currentVoice = tts.getVoice();
				WritableMap map = returnMapForVoice(currentVoice);

				promise.resolve(map);
			} else {
				promise.reject("error", "Method not availaible for SDK version " + Build.VERSION.SDK_INT + ". Requires(SDK version >= 21)");
			}
		} catch(Exception e) {
			promise.reject("error", "Unable to retrieve voice for getCurrentVoice()", e);
		}
	}

	@ReactMethod
	public void getAvailableVoices(Promise promise) {
		if(notReady(promise)) return;

		try {
			WritableArray voicesList = Arguments.createArray();
			Voice[] array = tts.getVoices().toArray(new Voice[tts.getVoices().size()]);
			for(Voice voice: array) {
				WritableMap newVoice = returnMapForVoice(voice);
				voicesList.pushMap(newVoice);
			}

			promise.resolve(voicesList);
		} catch(Exception e) {
			promise.reject("not_found", "Unable to retrieve voices for getAvailableVoices()", e);
		}
	}

	@ReactMethod
	public void getEnginesInfo(Promise promise) {
		if(notReady(promise)) return;

		try {
			WritableArray ttsInfo = Arguments.createArray();

			List<TextToSpeech.EngineInfo> engineList = tts.getEngines();
			Iterator iterator = engineList.iterator();
			while(iterator.hasNext()) {
				ttsInfo.pushString(iterator.next().toString());
			}

			promise.resolve(ttsInfo);
		} catch(Exception e) {
			promise.reject("error", "Unable to retrieve TTS Engine info for getEnginesInfo()", e);
		}
	}

	@ReactMethod
	public void getCurrentEngineInfo(Promise promise) {
		if(notReady(promise)) return;

		try {
			String info = tts.	getDefaultEngine().toString();

			promise.resolve(info);
		} catch(Exception e) {
			promise.reject("error", "Unable to retrieve TTS Engine info for getCurrentEngineInfo()", e);
		}
	}

	@ReactMethod
	public void setDucking(Boolean ducking, Promise promise) {
		if(notReady(promise)) return;
		this.IS_DUCKING = ducking;

		promise.resolve("success");
	}

	@ReactMethod
	public void setDefaultLangauge(String languageCode, Promise promise) {
		if(notReady(promise)) return;

		Locale locale = null;

		if(languageCode.indexOf("-") != -1) {
			String[] parts = languageCode.split("-");
			locale = new Locale(parts[0], parts[1]);
		} else {
			locale = new Locale(languageCode);
		}

		int resultForAvailability = tts.isLanguageAvailable(locale);
		switch(resultForAvailability) {
			case TextToSpeech.LANG_AVAILABLE:
            case TextToSpeech.LANG_COUNTRY_AVAILABLE:
            case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
            	tts.setLanguage(locale);
            	promise.resolve("Success");
            	break;
            case TextToSpeech.LANG_MISSING_DATA:
            	promise.reject("not_found", "Language data is missing");
            	break;
            case TextToSpeech.LANG_NOT_SUPPORTED:
            	promise.reject("not_found", "Language is not supported");
            	break;
            default:
            	promise.reject("error", "Unkown error code");
            	break;
		}
	}

	@ReactMethod
	public void setDefaultPitch(float pitch, Promise promise) {
		if(notReady(promise)) return;

		int result = tts.setPitch(pitch);
		if(result == TextToSpeech.SUCCESS) {
			WritableMap map = Arguments.createMap();
			map.putString("status", "Success");
			map.putDouble("pitch", (double)pitch);
			promise.resolve(map);
		} else {
			promise.reject("error", "Unable to set pitch");
		}
	}

	@ReactMethod
	public void setDefaultSpeechRate(float speechRate, Promise promise) {
		if(notReady(promise)) return;

		int result = tts.setSpeechRate(speechRate);
		if(result == TextToSpeech.SUCCESS) {
			WritableMap map = Arguments.createMap();
			map.putString("status", "Success");
			map.putDouble("speechRate", (double)speechRate);
			promise.resolve(map);
		} else {
			promise.reject("error", "Unable to set speech rate");
		}
	}

	private WritableMap returnMapForLocale(Locale locale) {
		WritableMap localeData = Arguments.createMap();
		localeData.putString("languageName", locale.getDisplayName());
		localeData.putString("languageCode", locale.getISO3Language());
		localeData.putString("languageString", locale.toString());
		localeData.putString("countryName", locale.getDisplayCountry());
		localeData.putString("countryCode", locale.getISO3Country());

		return localeData;
	}

	private WritableMap returnMapForVoice(Voice voice) {
		WritableMap voiceData = Arguments.createMap();
		voiceData.putString("voiceName", voice.getName());
		voiceData.putString("languageName", voice.getLocale().getDisplayLanguage());
		voiceData.putString("languageCode", voice.getLocale().getISO3Language());
		voiceData.putString("languageString", voice.getLocale().toString());
		voiceData.putString("countryName", voice.getLocale().getDisplayCountry());
		voiceData.putString("countryCode", voice.getLocale().getISO3Country());

		return voiceData;
	}

	@SuppressWarnings("deprecation") 
	private int speak(String utterance, int queueMode, String utteranceId) {
		if(Build.VERSION.SDK_INT >= 21) {
			return tts.speak(utterance, queueMode, null, utteranceId);
		} else {
			HashMap<String, String> params = new HashMap();
			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
			return tts.speak(utterance, queueMode, params);
		}
	}

	private boolean notReady(Promise promise) {
		if(!IS_READY) {
			promise.reject("not_ready", "TTS Engine Not Ready");
			return true;
		} else {
			return false;	
		}
	}

	private void sendEvent(String eventName, String utteranceId) {
		WritableMap params = Arguments.createMap();
		params.putString("utteranceId", utteranceId);
		getReactApplicationContext()
			.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
			.emit(eventName, params);
	}
}