# Udacity-CapstoneStageII

Android app that helps aquarium owners set up their new tanks and conduct a 'fishless cycle'. This is a submission for the second and final phase of the Capstone project on the Udacity Android Developers Nanodegree. Some of the rubric specifications were met in the following way:

**App integrates a third-party library:** App uses the [Glide](https://github.com/bumptech/glide) and [MPAndroidChartLibraries](https://github.com/PhilJay/MPAndroidChart).

**App integrates two or more Google services. Google service integrations can be a part of Google Play Services or Firebase:**
App uses Admob and Analytics.

**App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path:** App passwords are included inside the app's [build.gradle](https://github.com/PPartisan/Udacity-CapstoneStageII/blob/master/app/build.gradle) file.

**If it needs to pull or send data to/from a web service or API only once, or on a per request basis (such as a search application), app uses an IntentService to do so:** [SettingsActivity](https://github.com/PPartisan/Udacity-CapstoneStageII/blob/master/app/src/main/java/com/github/ppartisan/fishlesscycle/ui/SettingsActivity.java) uses an [IntentService](https://github.com/PPartisan/Udacity-CapstoneStageII/blob/master/app/src/main/java/com/github/ppartisan/fishlesscycle/service/LoadImagePackService.java) to query a [ContentProvider](https://github.com/PPartisan/Udacity-CapstoneStageII/blob/master/app/src/main/java/com/github/ppartisan/fishlesscycle/data/Provider.java). In all other cases a `Loader` is used.

##Fishless Cycle
![icon](https://github.com/PPartisan/Udacity-CapstoneStageII/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png?raw=true)
