
# react-native-android-text-to-speech

## Getting started

`$ npm install react-native-android-text-to-speech --save`

### Mostly automatic installation

`$ react-native link react-native-android-text-to-speech`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
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
```javascript
import RNAndroidTextToSpeech from 'react-native-android-text-to-speech';

// TODO: What to do with the module?
RNAndroidTextToSpeech;
```
  