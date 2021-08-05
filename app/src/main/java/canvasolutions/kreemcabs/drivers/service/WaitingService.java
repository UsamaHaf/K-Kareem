package canvasolutions.kreemcabs.drivers.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.activity.MainActivity;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.settings.AppConst;


public class WaitingService extends Service {
    private static final int LOCATION_REQUEST_CODE = 101;
    private static final String CHANNEL_ID = "waiting_channel";
    public Location currentLocation = new Location("dummyprovider10");
    LocationManager locationManager;
    double kmphSpeed,average;
    int number=0;
    int waiting_time = 0;
    String ride_id;

    public WaitingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("K-Kream Cabs")
                .setContentText("Service running..")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);


        if (intent != null) {
        if (intent.hasExtra("ride_id")) {
            ride_id = intent.getExtras().get("ride_id").toString();
            new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    Log.d("TAGSPEED", "fn_getlocation:DUMMY " + currentLocation);
                    Log.d("TAGSPEED", "onStartCommand: BACK SERVICE START");
                    againCFive();
                }
            }.start();

        }
    }else {
            Log.d("TAGSPEED", "fn_getlocation:DUMMY " + currentLocation);
            Log.d("TAGSPEED", "onStartCommand: BACK SERVICE START");
            againCFive();
        }

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void countDownFive() {
        Log.d("TAGSPEED", "onStartCommand: METHOD");
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("TAGSPEED", "onStartCommand: TIME START");
                if (number == 4){
                    number = 0;
                    average = Double.parseDouble(M.getValue(getApplicationContext(),M.SPEED1_KEY)) +
                            Double.parseDouble(M.getValue(getApplicationContext(),M.SPEED2_KEY) )+
                            Double.parseDouble(M.getValue(getApplicationContext(),M.SPEED3_KEY)) +
                            Double.parseDouble(M.getValue(getApplicationContext(),M.SPEED4_KEY));
                    average = average/4;
                   if (Double.compare(average,5.0) == -1){
                       waiting_time = waiting_time+20;
                       new CountDownTimer(1000, 1000) {
                           @Override
                           public void onTick(long millisUntilFinished) { }
                           @Override
                           public void onFinish() {
                               if (M.getValue(getApplicationContext(),M.WAITING_TIME_KEY)!=null )
                                   if (!M.getValue(getApplicationContext(),M.WAITING_TIME_KEY).equals(""))
                               new setWaitTime().execute();
                           }
                       }.start();

                   }
                }
                Log.d("TAGAVERAGE", "onTick:AVERAGE "+average +"    WAITING "+waiting_time+"    NUMBER "+number);
            }
            @Override
            public void onFinish() {
                fn_getlocation();
                if (number==0){
                    M.setValue(getApplicationContext(),String.valueOf(kmphSpeed),M.SPEED1_KEY);
                }
                if (number==1){
                    M.setValue(getApplicationContext(),String.valueOf(kmphSpeed),M.SPEED2_KEY);
                }
                if (number==2){
                    M.setValue(getApplicationContext(),String.valueOf(kmphSpeed),M.SPEED3_KEY);
                }
                if (number==3){
                    M.setValue(getApplicationContext(),String.valueOf(kmphSpeed),M.SPEED4_KEY);
                }
//                    Toast.makeText(WaitingService.this, "" +kmphSpeed, Toast.LENGTH_SHORT).show();
                Log.d("TAGSPEED", "onStartCommand: TIME END");
                if (number!=4){
                    number++;
                }
                againCFive();
            }
        }.start();
    }

    public void againCFive() {

        Log.d("TAGSPEED", "onStartCommand: AGAIN CT");
        if (M.getValue(this,M.SHOULD_SERVICE_STOP) != null){
            if (!M.getValue(this,M.SHOULD_SERVICE_STOP).equals("")){
                if (M.getValue(this,M.SHOULD_SERVICE_STOP).equals("true")){
                    stopSelf();

                }else countDownFive();
            }
        }

    }
    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        String provider;
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            currentLocation = locationManager.getLastKnownLocation(provider);
            if (currentLocation != null){
                double currentSpeed = round(currentLocation.getSpeed(),3, BigDecimal.ROUND_HALF_UP);
                kmphSpeed = round((currentSpeed*3.6),3,BigDecimal.ROUND_HALF_UP);
                Log.d("TAGSPEED", "fn_getlocation: "+kmphSpeed);
            }
        }
    }
    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }
    public class setWaitTime extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"set_wait_time.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                Log.d("TAGSPEED", "onResponse: "+response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ride_id", ride_id);
                    params.put("wait_time",String.valueOf(waiting_time));
                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //to add spacing between cards
            if (this != null) {
            }
        }
        @Override
        protected void onPreExecute() {
        }
    }

    @Override
    public void onDestroy() {
        M.setValue(getApplicationContext(),String.valueOf(waiting_time),M.WAITING_TIME_KEY);
        super.onDestroy();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Waiting Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
//   average = Double.parseDouble(M.getValue(getApplicationContext(),M.SPEED1_KEY)) +
//           Double.parseDouble(M.getValue(getApplicationContext(),M.SPEED2_KEY) )+
//           Double.parseDouble(M.getValue(getApplicationContext(),M.SPEED3_KEY)) +
//           Double.parseDouble(M.getValue(getApplicationContext(),M.SPEED4_KEY));
//           average = average/4;
//           if (Double.compare(average,5.0) == -1){
//           if (M.getValue(getApplicationContext(),M.WAITING_TIME_KEY)!=null || !M.getValue(getApplicationContext(),M.WAITING_TIME_KEY).isEmpty()){
//           M.setValue(getApplicationContext(),M.WAITING_TIME_KEY,String.valueOf(waiting_time + Double.parseDouble(M.getValue(getApplicationContext(),M.WAITING_TIME_KEY))));
//           waiting_time = 0;
//           }else {
//           M.setValue(getApplicationContext(),M.WAITING_TIME_KEY,String.valueOf(waiting_time));
//           waiting_time = 0;
//           }
//           new CountDownTimer(1000, 1000) {
//@Override
//public void onTick(long millisUntilFinished) { }
//@Override
//public void onFinish() {
//        new setWaitTime().execute();
//        }
//        }.start();
