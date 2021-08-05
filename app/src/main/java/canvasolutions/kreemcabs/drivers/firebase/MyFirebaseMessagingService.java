package canvasolutions.kreemcabs.drivers.firebase;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.activity.MainActivity;
import canvasolutions.kreemcabs.drivers.fragment.driver.FragmentDashboard;
import canvasolutions.kreemcabs.drivers.fragment.driver.FragmentRideCanceledDriver;
import canvasolutions.kreemcabs.drivers.fragment.driver.FragmentRideCompletedDriver;
import canvasolutions.kreemcabs.drivers.fragment.driver.FragmentRideConfirmedDriver;
import canvasolutions.kreemcabs.drivers.fragment.driver.FragmentRideNewDriver;
import canvasolutions.kreemcabs.drivers.fragment.driver.FragmentRideOnRideDriver;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String tag = remoteMessage.getData().get("tag");

        Log.d("TAGRMOTE", "onMessageReceivedTITLE1: "+remoteMessage.getData());
        generateHeadsUpNotification(title,message,tag);
    }
    private void generateHeadsUpNotification(String title, String messageBody, String tag) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        Uri soundUri = Uri.parse(
                "android.resource://" +
                        getApplicationContext().getPackageName() +
                        "/" +
                        R.raw.siren);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
        }


        String notificationChannelId = createNotificationChannel(getApplicationContext());

        Intent intent = new Intent(this, MainActivity.class);
        if(tag.length() != 0) {
            if (tag.equals("ridenewrider"))
                intent.putExtra("fragment_name", "ridenewrider");
            else
                intent.putExtra("fragment_name", "");
        }else{
            intent.putExtra("fragment_name", "");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent PendingIntent = android.app.PendingIntent.getActivity(this, 0,
                intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT);

        // 5. Build and issue the notification.

        // Because we want this to be a new notification (not updating a previous notification), we
        // create a new Builder. Later, we use the same global builder to get back the notification
        // we built here for a comment on the post.
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(
                getApplicationContext(), notificationChannelId
        );

        notificationCompatBuilder // BIG_PICTURE_STYLE sets title and content for API 16 (4.1 and after).
//                .setStyle(bps) // Title for API <16 (4.0 and below) devices.
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.ic_car_top_view)
                .setContentIntent(PendingIntent)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_pakistan))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(soundUri)
//                .setColor(ContextCompat.getColor(getApplicationContext(),R.color.purpleStart))
                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)
                .setSubText(String.valueOf(1))
                .setCategory(Notification.CATEGORY_CALL) // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(NotificationCompat.PRIORITY_MAX) // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // If the phone is in "Do not disturb mode, the user will still be notified if
        // the sender(s) is starred as a favorite.
//        for (name in BigPictureStyleMockData.mParticipants()) {
//            notificationCompatBuilder.addPerson(name)
//        }

        Notification notification = notificationCompatBuilder.build();
        MediaPlayer mp= MediaPlayer.create(this, R.raw.siren);
        mp.start();

        // TODO Step 6: Notify the user using notification id and Notification builder with notification manager.
        // START
        NotificationManagerCompat.from(getApplicationContext()).notify(888, notification);
        // END

            if (MainActivity.getCurrentFragment().getTag().equals("dashboard") && FragmentDashboard.connectionDetector.isConnectingToInternet()) {
                new FragmentDashboard.getDashboard().execute();
            }else if (MainActivity.getCurrentFragment().getTag().equals("new") && FragmentRideNewDriver.connectionDetector.isConnectingToInternet()) {
                new FragmentRideNewDriver.getRide().execute();
            } else if (MainActivity.getCurrentFragment().getTag().equals("confirmed") && FragmentRideConfirmedDriver.connectionDetector.isConnectingToInternet()) {
                new FragmentRideConfirmedDriver.getRideConfirmed().execute();
            } else if (MainActivity.getCurrentFragment().getTag().equals("on_ride") && FragmentRideOnRideDriver.connectionDetector.isConnectingToInternet()) {
                new FragmentRideOnRideDriver.getRideOnRide().execute();
            } else if (MainActivity.getCurrentFragment().getTag().equals("completed") && FragmentRideCompletedDriver.connectionDetector.isConnectingToInternet()) {
                new FragmentRideCompletedDriver.getRideCompleted().execute();
            } else if (MainActivity.getCurrentFragment().getTag().equals("canceled") && FragmentRideCanceledDriver.connectionDetector.isConnectingToInternet()) {
                new FragmentRideCanceledDriver.getRideCanceled().execute();
            }


    }
    private String createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // The id of the channel.
            String channelId = "channel_social_1";
            // The user-visible name of the channel.
            CharSequence channelName = "Sample Social";
            // The user-visible description of the channel.
            String channelDescription = "Sample Social Notifications";

            int channelImportance = NotificationManager.IMPORTANCE_HIGH;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId, channelName, channelImportance
            );
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(true);
//            notificationChannel.setSound(soundUri,audioAttributes);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return "";
        }
    }
}