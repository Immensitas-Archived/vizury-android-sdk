![Vizury Logo](https://github.com/vizury/vizury-android-sdk/blob/master/resources/VizuryLogo.png)
## Summary
 This is Android SDK integration guide.
 
## Components

  * [Example app](#example-app)
  * [Basic Integration](#basic-integration)
	* [Getting the SDK](#sdk-get)
	* [Add Google Play Services](#gps-get)
	* [Manifest File Changes](#manifest-changes)
	* [Vizury SDK Initialization](#sdk-init)
	* [Event Logging](#event-logging)
  * [Push Notifications](#push-notifications)
  	* [Enabling FCM](#enabling-fcm)
  	* [Configuring Application](#config-app)
  * [Additional configurations](#additional-config)
  	* [Offline caching](#offline-caching)
  	* [Controlling fcm registration and message handling](#fcm-handling)

## <a id="example-app"></a>Example app

Examples on how the Vizury SDK can be integrated.

`examples/HelloVizury` is a sample app having a basic integration with vizury SDK.


## <a id="basic-integration"></a>Basic Integration

### <a id="sdk-get"></a>Getting the SDK
Vizury Android Library is available as a maven dependency. Add jcenter in repositories, in your top level build.gradle file
```
repositories {
    jcenter()
}
```
Add the following dependency in your build.gradle file under app module

```
implementation 'com.vizury.mobile:VizurySDK:6.0.0'
```

### <a id="gps-get"></a>Add Google Play Services

Since the 1st of August of 2014, apps in the Google Play Store must use the [Google Advertising ID][google_ad_id] to 
uniquely identify devices. To allow the vizury SDK to use the Google Advertising ID, you must integrate the 
[Google Play Services][google_play_services]. If you haven't done this yet, follow these steps:

Open the build.gradle file of your app and find the dependencies block. Add the following line:
```
implementation 'com.google.android.gms:play-services-base:15.0.0'
implementation 'com.google.android.gms:play-services-ads:15.0.0'
```

### <a id="manifest-changes"></a>Manifest File Changes

In the Package Explorer open the AndroidManifest.xml of your Android project and make the following changes


Add the below meta tags, replacing the place holders with associated values.

```xml
<meta-data
	android:name="Vizury.VRM_ID"
	android:value="{PACKAGE_ID}" />
<meta-data
	android:name="Vizury.SERVER_URL"
	android:value="{SERVER_URL}" />
<meta-data
	android:name="Vizury.DEBUG_MODE"
	android:value="{true/false}" /> 
```

`PACKAGE_ID` and `SERVER_URL` will be provided by the vizury account manager.
`DEBUG_MODE` is used to show vizury debug/error/info logs. Set this to false when going to production.

### <a id="sdk-init"></a>Vizury SDK Initialization

In your MainActivity.java inside onCreate(Bundle SavedInstance) add following code snippet

```java
import com.vizury.mobile.VizuryHelper;
@Override
protected void onCreate(Bundle savedInstanceState) {
	//your code		    
	VizuryHelper.getInstance(getApplicationContext()).init();
	//your code
}
```

### <a id="event-logging"></a>Event Logging

When a user browse through the app, various activities happen e.g. visiting a product, adding the product to cart, making purchase, etc. These are called events. Corresponding to each event, app needs to pass certain variables to the SDK which the SDK will automatically pass to Vizury servers.

Add the following code in each view where the event is to be tracked - 

```java
 VizuryHelper.getInstance(context).logEvent(eventname, attributes);
```
```
Where 
 eventname   : name of the event
 attributes  : AttributeBuilder object containing the attributes. You can set multiple attributes.
```

Eg.
```java
import com.vizury.mobile.VizuryHelper;

AttributeBuilder builder = new AttributeBuilder.Builder()
	.addAttribute("pid", "AFGEMSBBLL")
	.addAttribute("quantity", "1")
	.addAttribute("price", "876")
	.addAttribute("category","clothing")
	.build();

VizuryHelper.getInstance(context).logEvent("product page", builder);
```

You can also pass a JSONObject or a JSONArray as an attribute. 
For passing a JSONObject or JSONArray you can do something like this :

```java
JSONObject attrs;           // your attributes are in a JSONObject.
AttributeBuilder builder = new AttributeBuilder.Builder()
	.addAttribute("productAttributes", attrs.toString())
	.build(); 
```

## <a id="push-notifications"></a>Push Notifications

Vizury sends push notifications to Android devices using [Firebase Cloud Messaging][FCM].

## <a id="enabling-fcm"></a>Enabling FCM

Create a FCM[fcm_console] project if you dont already have one. After creating your project, click the `Add App` button and select the option to add Firebase to your app. After this enter your app's package id. Follow the subsequent steps to download a `google-services.json` file. After this click on the `Cloud Messaging` Tab and note down the server key. This key has to be entered in the vizury dashboard.

## <a id="config-app"></a>Configuring Application

### Add Firebase SDK

a) Place the `google-services.json` that you had downloaded from the FCM console in the app module.

![add_google_services](https://github.com/vizury/vizury-android-sdk/blob/master/resources/add_google_services_json.png)


b) Gradle changes

Project-level build.gradle (`<project>/build.gradle`):

```
buildscript {
  dependencies {
    // Add this line
    classpath 'com.google.gms:google-services:3.3.1'
  }
}
```
App-level build.gradle (`<project>/<app-module>/build.gradle`):

```
dependencies {
  // Add this line
  compile 'com.google.firebase:firebase-core:15.0.2'
}
...
// Add to the bottom of the file
apply plugin: 'com.google.gms.google-services'
```


### Manifest changes

In the Package Explorer open the AndroidManifest.xml of your Android project and and make the following changes

Add the following meta-tags
```xml
<meta-data
	android:name="Vizury.NOTIFICATION_ICON"
	android:resource="{NOTIFICATION_ICON}" />
<meta-data
	android:name="Vizury.NOTIFICATION_ICON_SMALL"
	android:resource="{NOTIFICATION_ICON_SMALL}" />
```

`NOTIFICATION_ICON` is the icon that comes at the left of a notification

`NOTIFICATION_ICON_SMALL` is the icon that comes at the notification bar. This should be white icon on a transparent background

Refer [Android Notifications][android_notifications] and [Style Icons][style_icons] for more details.

Add the following services for receiving the fcm token and messages.

```xml
<service
	android:name="com.vizury.mobile.Push.VizFirebaseMessagingService">
        <intent-filter>
		<action android:name="com.google.firebase.MESSAGING_EVENT"/>
	</intent-filter>
</service>

<service
	android:name="com.vizury.mobile.Push.VizFirebaseInstanceIdService">
	<intent-filter>
		<action android:name="com.google.firebase.INSTANCE_ID_EVENT"/>
	</intent-filter>
</service>
```

`Mandatory intent service for doing the heavy lifting`
```xml
<service 
	android:name="com.vizury.mobile.Push.VizIntentService"
	android:exported="false">
</service>
```

## <a id="additional-config"></a>Additional configurations

### <a id="offline-caching"></a>Offline caching

If your app supports offline features and you want to send the user behaviour data to vizury while he is offline, add the following tag in the manifest.

```xml
<meta-data
	android:name="Vizury.DATA_CACHING"
	android:value="true"/>
```
  	

### <a id="fcm-handling"></a>Controlling fcm registration and message handling

If you want to control FCM registration or FCM message handling then make these changes. In the Package Explorer open the AndroidManifest.xml of your Android project.

a) Remove the `VizFirebaseMessagingService` and `VizFirebaseInstanceIdService` from the manifest if added.

b) Make the following code changes

`Pass the FCM token to vizury. Make sure that you call this everytime FCM Token is refreshed` 

```java
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
	String refreshedToken = FirebaseInstanceId.getInstance().getToken();
	VizuryHelper.getInstance(context).setGCMToken(refreshedToken);
	...
    }	
```

`On receving any push message call the vizury api for checking and parsing the payload`
```java
public class MyFirebaseMessagingService extends FirebaseMessagingService {
	@Override
    	public void onMessageReceived(RemoteMessage message){
	        Map data = message.getData();
        	if (Utils.getInstance(getApplicationContext()).isPushFromVizury(data))
			PushHandler.getInstance(getApplicationContext()).handleNotificationReceived(data);
        	else {
            		// your own logic goes here
        	}
    	}
}
```

        

[google_ad_id]:                 https://support.google.com/googleplay/android-developer/answer/6048248?hl=en
[google_play_services]:         http://developer.android.com/google/play-services/setup.html
[google_developer_console]:	https://developers.google.com/mobile/add?platform=android
[fcm-console]:			https://console.firebase.google.com/
[android_notifications]:	https://material.google.com/patterns/notifications.html
[style_icons]:			https://material.google.com/style/icons.html
[FCM]:				https://firebase.google.com/docs/cloud-messaging/
[firebase-link]:		https://firebase.google.com/docs/cloud-messaging/android/client
[FCMTokenReader]:		https://github.com/vizury/vizury-android-sdk/blob/master/examples/HelloVizury-FCM/app/src/main/java/com/vizury/hellovizuryfcm/FCMTokenReader.java
