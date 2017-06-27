
import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const RNAndroidTextToSpeech = NativeModules.AndroidTextToSpeech;

class AndroidTextToSpeech extends NativeEventEmitter {

	constructor() {
		super(TextToSpeech);
	}

	getAvailableLocales() {
		return RNAndroidTextToSpeech.getAvailableLocales();
	}

	getDefaultLocale() {
		return RNAndroidTextToSpeech.getDefaultLocale();
	}

	getCurrentLocale() {
		return RNAndroidTextToSpeech.getCurrentLocale();
	}

	getCurrentVoice() {
		return RNAndroidTextToSpeech.getCurrentVoice();
	}

	getAvailableVoices() {
		return RNAndroidTextToSpeech.getAvailableVoices();
	}

	getEnginesInfo() {
		return RNAndroidTextToSpeech.getEnginesInfo();
	}

	getCurrentEngineInfo() {
		return RNAndroidTextToSpeech.getCurrentEngineInfo();
	}	

	setDucking(enabled) {
		return RNAndroidTextToSpeech.setDucking(enabled);
	}

	setDefaultLangauge(languageCode) {
		return RNAndroidTextToSpeech.setDefaultLangauge(languageCode);
	}

	setDefaultPitch(pitch) {
		return RNAndroidTextToSpeech.setDefaultPitch(pitch);
	}

	setDefaultSpeechRate(speechRate) {
		return RNAndroidTextToSpeech.setDefaultSpeechRate(speechRate);
	}

	speak(utterance, queueMode) {
		return RNAndroidTextToSpeech.speak(languageCode);
	}

	stop() {
		return RNAndroidTextToSpeech.stop();
	}	
}

export default new AndroidTextToSpeech();
