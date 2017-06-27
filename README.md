
# react-native-android-text-to-speech

## Getting started

```shell
npm install --save react-native-android-text-to-speech
```

### Mostly automatic installation

```shell
react-native link react-native-android-text-to-speech
```

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.echo.reactandroidtts.RNAndroidTextToSpeechPackage;` to the imports at the top of the file
  - Add `new RNAndroidTextToSpeechPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-android-text-to-speech'
  	project(':react-native-android-text-to-speech').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-android-text-to-speech/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-android-text-to-speech')
  	```


## Usage

This wrapper library's function uses Promises instead of callbacks. The ".then()" or es2016's `async await`(recommended) keywords should be used. 

### Imports

```js
import AndroidTextToSpeech from 'react-native-tts';
``` 

### Speaking

1) Use the function `AndroidTextToSpeech.speak(utterance, queueMode)` to use the default TTS engine to speak. Where
'utterance' is of type string.
'queueMode' can have values -
			1. "ADD" - to add to the TTS play queue.
			2. "FLUSH" - to interrupt the TTS play queue with the utterance and then flush it.
Returns a promise with utteranceId.

Example -
```js
AndroidTextToSpeech.speak('Hello, world!', 'ADD');
```

2) Use the function `AndroidTextToSpeech.stop()` to stop speaking and flush the TTS play queue.
Return a promise with "success".

```js
AndroidTextToSpeech.stop();
```

### Ducking

Ducking is meant to lower other applications output sound level while speaking.
It can be enabled by using function `AndroidTextToSpeech.setDucking(enable)`. Where 'enable' is a boolean value.
Return a promise with success.

Example-
```js
AndroidTextToSpeech.setDucking(true)
```

### List Attribute Information

Various lists can be retrieved regarding the TTS Engine.

1) Installed Engines Info: Use function 
```js
AndroidTextToSpeech.getEnginesInfo();
```
to list all the available TTS Engines on the android device.
Returns a promise with list containing strings of package names of the installed engines.


2) Current Engine Info: Use function
```js
AndroidTextToSpeech.getCurrentEngineInfo();
```
to get the name of current TTS Engine being used.
Returns a promise with string of the package name of currently in use TTS engine.


3) All Available Locales: Use function
```js
AndroidTextToSpeech.getAvailableLocales();
```
to get details of all the available languages of the engine.
Returns a promise with an object of the form - 
```js
{
	languageName: string;	//Language display name as given by android 
	languageCode: string;	//Language code according to ISO 639-2 standards
	coutryName: string;		//Country display name as given by android
	countryCode: string;	//Country code according to ISO 3166-1 alpha-3 standards
}
```

4) Default Locale: Use Function
```js
AndroidTextToSpeech.getDefaultLocale();
```
to get details of the default locale being used by the engine.
Returns a promise with an object of the form - 
```js
{
	languageName: string;	//Language display name as given by android 
	languageCode: string;	//Language code according to ISO 639-2 standards
	coutryName: string;		//Country display name as given by android
	countryCode: string;	//Country code according to ISO 3166-1 alpha-3 standards
}
```

5) Current Locale: Use Function
```js
AndroidTextToSpeech.getCurrentLocale();
```
to get details of the current locale being used by the engine.
Returns a promise with an object of the form - 
```js
{
	languageName: string;	//Language display name as given by android 
	languageCode: string;	//Language code according to ISO 639-2 standards
	coutryName: string;		//Country display name as given by android
	countryCode: string;	//Country code according to ISO 3166-1 alpha-3 standards
}
```


6) All Available Voices: Use function
```js
AndroidTextToSpeech.getAvailableVoices();
```
to get details of all the available voices in the engine.
Returns a promise with an object of the form - 
```js
{
	voiceName: string;		//Name of the voice.
	languageName: string;	//Language display name as given by android 
	languageCode: string;	//Language code according to ISO 639-2 standards
	coutryName: string;		//Country display name as given by android
	countryCode: string;	//Country code according to ISO 3166-1 alpha-3 standards
}
```

7) Current Voice: Use function
```js
AndroidTextToSpeech.getAvailableVoices();
```
to get details of the current voice being used by the engine.
Returns a promise with an object of the form - 
```js
{
	voiceName: string;		//Name of the voice.
	languageName: string;	//Language display name as given by android 
	languageCode: string;	//Language code according to ISO 639-2 standards
	coutryName: string;		//Country display name as given by android
	countryCode: string;	//Country code according to ISO 3166-1 alpha-3 standards
}
```


### Set Speech Attributes

Various speech attributes can be set

1) Language: Use function
```js
AndroidTextToSpeech.setDefaultLangauge(languageCode);
```
to set the language/locale to be used by the engine. Where 'languageCode' is in the format of ISO 639-2 standards.
Returns a promise with either "success" or with error depending on the language being found.


2) Pitch: Use function
```js
AndroidTextToSpeech.setDefaultPitch(pitch);
```
to set the voice pitch to be used by the engine. Where 'pitch' is speech pitch. 1.0 is the normal pitch, lower values lower the tone of the synthesized voice, greater values increase it.
Returns a promise with "success".


3) Speech Rate: Use function
```js
AndroidTextToSpeech.setDefaultSpeechRate(speechRate);
```
to set the playback speed to be used by the engine. Where 'speechRate' is speech rate. 1.0 is the normal speech rate, lower values slow down the speech (0.5 is half the normal speech rate), greater values accelerate it (2.0 is twice the normal speech rate).
Returns a promise with "success".


### Events

Subscibe to TTS events.

```js
AndroidTextToSpeech.addEventListener('tts-start', (event) => console.log("start", event));
AndroidTextToSpeech.addEventListener('tts-finish', (event) => console.log("finish", event));
AndroidTextToSpeech.addEventListener('tts-cancel', (event) => console.log("cancel", event));
```

### Example Code
```js
async function sayHello() {
	let result = await AndroidTextToSpeech.speak("Hello World!", "ADD");
	console.log(result);
	return result;
}

async function getLanguageDetails() {
	let list = await AndroidTextToSpeech.getAvailableLocales();
	console.log(list);
	return list;
}
```


## License

        DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO.