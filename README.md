![Vizury Logo](https://github.com/vizury/vizury-sdk/blob/master/VizuryLogo.png)
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
  	* [Enabling GCM](#enabling-gcm)
  	* [Configuring Application](#config-app)
  * [Additional configurations] (#additional-config)
  	* [Offline caching](#offline-caching)
  	* [FCM(Firebase cloud Messaging) Compatibility](#fcm)
  	* [Controlling gcm registration and message handling](#gcm-handling)
  	* [Getting registered gcm token] (#get-token)

## <a id="example-app"></a>Example app

Examples on how the Vizury SDK can be integrated.

`examples/HelloVizury` is a sample app having a basic integration with vizury SDK.

`examples/HelloVizury-FCM` is a sample app having FCM based integration with vizury SDK.


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
compile 'com.vizury.mobile:VizurySDK:5.4.2'
```

### <a id="gps-get"></a>Add Google Play Services

Since the 1st of August of 2014, apps in the Google Play Store must use the [Google Advertising ID][google_ad_id] to 
uniquely identify devices. To allow the vizury SDK to use the Google Advertising ID, you must integrate the 
[Google Play Services][google_play_services]. If you haven't done this yet, follow these steps:

Open the build.gradle file of your app and find the dependencies block. Add the following line:
```
compile 'com.google.android.gms:play-services:9.4.0'
```

### <a id="manifest-changes"></a>Manifest File Changes

In the Package Explorer open the AndroidManifest.xml of your Android project and make the following changes

Add the following permissions if not already present

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

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

Vizury sends push notifications to Android devices using [Google Cloud Messaging][GCM].

## <a id="enabling-gcm"></a>Enabling GCM

If you don't have a Google API project with GCM enabled then use [this][google_developer_console] to create a Google API project. Please note down the `Project number` and the `API Server Key` as this will be required later during the integration. While generating the `API server key` make sure that you leave the IP field blank so that vizury can send push notifications.

## <a id="config-app"></a>Configuring Application

In the Package Explorer open the AndroidManifest.xml of your Android projectand and make the following changes

Add the following meta-tags
```xml
<meta-data
	android:name="Vizury.PROJECT_ID"
	android:value="id:{PROJECT_ID}" />
<meta-data
	android:name="Vizury.NOTIFICATION_ICON"
	android:resource="{NOTIFICATION_ICON}" />
<meta-data
	android:name="Vizury.NOTIFICATION_ICON_SMALL"
	android:resource="{NOTIFICATION_ICON_SMALL}" />
```
`PROJECT_ID` is the project number of your google API project

`NOTIFICATION_ICON` is the icon tha come at the left of a notification

`NOTIFICATION_ICON_SMALL` is the icon that comes at the notification bar. This should be white icon on a transparent background

Refer [Android Notifications][android_notifications] and [Style Icons][style_icons] for more details.

Add the following permissions

```xml
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<permission android:name="YOUR-APPLICATION-PACKAGE-NAME.permission.C2D_MESSAGE" 
		android:protectionLevel="signature"/>
<uses-permission android:name="YOUR-APPLICATION-PACKAGE-NAME.permission.C2D_MESSAGE" />
```
Replace `YOUR-APPLICATION-PACKAGE-NAME` with the package name of your application.

Add the following receivers and services

`A declaration of GcmReceiver, which handles messages sent from GCM to your application. Make sure you replace the place holder {YOUR APPLICATION PACKAGE NAME} with your application package`
```xml
<receiver
	android:name="com.google.android.gms.gcm.GcmReceiver"
	android:exported="true"
	android:permission="com.google.android.c2dm.permission.SEND">
	<intent-filter>
		<action android:name="com.google.android.c2dm.intent.RECEIVE" />
		<category android:name="{YOUR APPLICATION PACKAGE NAME}" />
	</intent-filter>
</receiver>
```
`A declaration of GcmListenerService, which enables various aspects of handling messages`
```xml
<service
	android:name="com.vizury.mobile.Push.VizGcmService"
	android:exported="false" >
	<intent-filter>
		<action android:name="com.google.android.c2dm.intent.RECEIVE" />
		<action android:name="com.google.android.c2dm.intent.REGISTRATION" />
	</intent-filter>
</service>
```
`A service that extends InstanceIDListenerService, to handle the creation, rotation, and updating of registration tokens`
```xml
<service
	android:name="com.vizury.mobile.Push.VizInstanceIDListener"
	android:exported="false">
	<intent-filter>
		<action android:name="com.google.android.gms.iid.InstanceID"/>
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
  	
  	
### <a id="fcm"></a>FCM(Firebase cloud Messaging) Compatibility

If your app is using Firebase messaging for push notifications then use following steps for integration. For more details on FCM visit [Firebase Cloud Messaging][firebase-link]

a) Remove the `GcmReceiver`, `VizGcmService` and `VizInstanceIDListener` from the manifest if added.

b) Remove the below meta-tag if added
```xml
<meta-data
	android:name="Vizury.PROJECT_ID"
	android:value="id:{PROJECT_ID}" />
```

c) Add the below meta-tag in the manifest
```xml
<meta-data
	android:name="Vizury.SKIP_GCM_REGISTRATION"
	android:value="true" />
```

d) Make the following code changes

`Pass the firebase messaging token to vizury`
```java
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // pass the refreshed token to vizury
        VizuryHelper.getInstance(getApplicationContext()).setGCMToken(refreshedToken);
    }
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

### <a id="gcm-handling"></a>Controlling gcm registration and message handling

If you want to control GCM registration or GCM message handling then make these changes. In the Package Explorer open the AndroidManifest.xml of your Android project.

a) Remove the `VizGcmService` and `VizInstanceIDListener` from the manifest if added.

b) Remove the below meta-tag if added
```xml
<meta-data
	android:name="Vizury.PROJECT_ID"
	android:value="id:{PROJECT_ID}" />
```
c) Add the below meta-tag in the manifest
```xml
<meta-data
	android:name="Vizury.SKIP_GCM_REGISTRATION"
	android:value="true" />
``` 

d) Make the following code changes

`Pass the GCM token to vizury. Make sure that you call this everytime GCMToken is refreshed` 

```java
VizuryHelper.getInstance(context).setGCMToken(gcmToken)
```

`On receving any push message call the vizury api for checking and parsing the payload`
```java
public class MyGCMService extends GcmListenerService {
	@Override
	public void onMessageReceived(String from, Bundle extras) {
        	if (Utils.getInstance(getApplicationContext()).isPushFromVizury(extras))
			PushHandler.getInstance(getApplicationContext()).handleNotificationReceived(extras);
        	else {
            		// your own logic goes here
        	}
    	}
}
```
    	
### <a id="get-token"></a>Getting registered gcm token

If you want to use the Vizury GCMListener service, for registering to GCM and listening to the GCM messages, but want to process them on your own, like handle push notification from other sources.

a) Add the following meta data in the manifest file, replacing the macro with fully qualified service class name, that will handle and process the push notifications

```xml
<meta-data
	android:name="Vizury.APP_GCM_SERVICE"
	android:value="{GCM_SERVICE_CLASS_NAME}" />
```
        
b) If you want the GCMToken after VizurySDK registered for GCM, you need to implement the GCMRegistrationListener of the sdk. This will give a callback to the listener whenever the gcm token in refreshed.
    	
```java
GCMRegistrationListener myListener = new GCMRegistrationListener() {
	@Override
	public void onGCMRegistration(String gcmToken) {
        	// handle the GCMToken passed
        }
};
VizuryHelper.getInstance(context).setGCMRegistrationListener(myListener);
```
        

[google_ad_id]:                 https://support.google.com/googleplay/android-developer/answer/6048248?hl=en
[google_play_services]:         http://developer.android.com/google/play-services/setup.html
[google_developer_console]:	https://developers.google.com/mobile/add?platform=android
[android_notifications]:	https://material.google.com/patterns/notifications.html
[style_icons]:			https://material.google.com/style/icons.html
[GCM]:				https://developers.google.com/cloud-messaging/
[firebase-link]:		https://firebase.google.com/docs/cloud-messaging/android/client
