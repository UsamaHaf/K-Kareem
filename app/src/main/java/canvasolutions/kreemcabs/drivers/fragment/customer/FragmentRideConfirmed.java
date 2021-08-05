package canvasolutions.kreemcabs.drivers.fragment.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.tabs.TabLayout;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.activity.MainActivity;
import canvasolutions.kreemcabs.drivers.adapter.RideAdapter;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.model.RideModel;
import canvasolutions.kreemcabs.drivers.settings.AppConst;
import canvasolutions.kreemcabs.drivers.settings.ConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iwgang.countdownview.CountdownView;

public class FragmentRideConfirmed extends Fragment implements CountdownView.OnCountdownEndListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener
        {

    private static CountdownView.OnCountdownEndListener OnCountdownEndListener;
    ViewPager pager;
    TabLayout tabs;
    View view;
    public static Context context;
    public static ConnectionDetector connectionDetector;
    String TAG="FragmentHome";
    ArrayList<String> tabNames = new ArrayList<String>();
    int currpos=0;

    public static RecyclerView recycler_view_ride;
    public static List<RideModel> albumList_ride;
    public static RideAdapter adapter_ride;

    /** MAP **/
    private GoogleMap mMap;
    public static Location currentLocation, clientLocation, destinationLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 101;

    /** GOOGLE API CLIENT **/
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds

    View mapView;
    private LocationManager locationManager;
    private final int REQUEST_FINE_LOCATION = 1234;
    private String provider;
    private int PLACE_PICKER_REQUEST = 1;
    static LinearLayout time_completed;
    public static  Button btn_ok_complete;
    public static SwipeRefreshLayout swipe_refresh;
    public static int timeDuration;
    public static ProgressBar progressBar_failed;
    public static LinearLayout layout_not_found,layout_failed;
    public static RelativeLayout layout_liste;
    private static LinearLayout linear_layout;
    private static RideModel rideModel = null;
    Handler mHandler;
    static String distance_client;
    static CountdownView mCvCountdownView;
    static LinearLayout timer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null)
            currpos = getArguments().getInt("tab_pos",0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_ride_confirmed, container, false);

        context=getActivity();
        MainActivity.setTitle(getString(R.string.confirmed_small));
//        if(M.getCountry(context).equals("All"))
//            MainActivity.setTitle(getString(R.string.confirmed_small));
//        else
//            MainActivity.setTitle(getString(R.string.confirmed_small)+" - "+M.getCountry(context));
        connectionDetector=new ConnectionDetector(context);

        if (MainActivity.newInt==1){
            MainActivity.newInt = 0;
            dialogNotification(R.string.wait_please,R.string.stay_here);
        }

        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable,10000);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
//            return;
        }

        // Get the location manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (provider != null) {
            currentLocation = locationManager.getLastKnownLocation(provider);
            clientLocation = locationManager.getLastKnownLocation(provider);
            destinationLocation = locationManager.getLastKnownLocation(provider);
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(context)
//                .enableAutoManage(getActivity(),0,this)
                .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(getActivity(), this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        albumList_ride = new ArrayList<>();
        adapter_ride = new RideAdapter(context, albumList_ride, getActivity(), "RideConfirmed");
        mCvCountdownView = (CountdownView)view.findViewById(R.id.countdown);
        timer = (LinearLayout)view.findViewById(R.id.timerLayout) ;
        btn_ok_complete = (Button)view.findViewById(R.id.btn_ok_complete);
        time_completed = (LinearLayout)view.findViewById(R.id.time_completed) ;
        linear_layout = (LinearLayout) view.findViewById(R.id.linear_layout);
        progressBar_failed = (ProgressBar) view.findViewById(R.id.progressBar_failed);
        layout_liste = (RelativeLayout) view.findViewById(R.id.layout_liste);
        layout_not_found = (LinearLayout) view.findViewById(R.id.layout_not_found);
        layout_failed = (LinearLayout) view.findViewById(R.id.layout_failed);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        recycler_view_ride = (RecyclerView) view.findViewById(R.id.recycler_view_requetes);
        @SuppressLint("WrongConstant") LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler_view_ride.setLayoutManager(verticalLayoutManager);
        recycler_view_ride.setItemAnimator(new DefaultItemAnimator());
        recycler_view_ride.setAdapter(adapter_ride);


        btn_ok_complete.setOnClickListener(v -> {
            time_completed.setVisibility(View.GONE);
            timer.setVisibility(View.GONE);
        });
        if(!M.getUserCategorie(context).equals("conducteur")){
            Log.d("DRIVER", "onCreateView: "+M.getDriverCT(context));
            if (M.getDriverCT(context).equals("1")) {
                Long ts = System.currentTimeMillis() / 1000;
                Log.d("DEVICE", "onResumeCurrentTS: "+ts);
                Log.d("DEVICE", "onGetFromM: "+Long.parseLong(M.getTimeStart(context)));
                if ((Long.parseLong(M.getTimeStart(context)) + (20 * 60)) < ts) {
                    timer.setVisibility(View.GONE);
                    time_completed.setVisibility(View.VISIBLE);
                    M.setDriverCT(context,"0");


//                mCvCountdownView.start(0);
                } else {
                    time_completed.setVisibility(View.GONE);
                    timer.setVisibility(View.VISIBLE);
                    Long remainingTime = (Long.parseLong(M.getTimeStart(context)) + (20 * 60)) - ts;
                    Log.d("DEVICE", "onGetFromM: "+remainingTime);
                    mCvCountdownView.start(remainingTime * 1000);
                }
            }




//            mCvCountdownView.start(Long.parseLong(M.getDriverCT(context)));

        }



//        recycler_view_transaction.addOnItemTouchListener(new RecyclerTouchListener(context, recycler_view_transaction, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                ridePojo ridePojo = albumList_transaction.get(position);
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//                ridePojo ridePojo = albumList_transaction.get(position);
//            }
//        }));



        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getRideConfirmed().execute();
            }
        });

        layout_failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar_failed.setVisibility(View.VISIBLE);
                new getRideConfirmed().execute();
            }
        });

        swipe_refresh.setRefreshing(true);
        new getRideConfirmed().execute();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
//        Toast.makeText(context, "Ok", Toast.LENGTH_SHORT).show();
        if (location != null) {
//            getDistance(location);
        }
    }

    /** Start COOGLE API Client **/
    @Override
    public void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }



    /*private static String getDistance(Location loc1, Location loc2){
        final String[][] tab = {{}};

        float distanceInMeters1 = loc1.distanceTo(loc2);
        tab[0] = String.valueOf(distanceInMeters1).split("\\.");
        String distance = tab[0][0];
        return distance;
    }*/

    /** Récupération des requêtes d'un conducteur**/
    public static class getRideConfirmed extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"get_requete_user_app_confirmed.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                swipe_refresh.setRefreshing(false);
                                albumList_ride.clear();
                                adapter_ride.notifyDataSetChanged();
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    layout_liste.setVisibility(View.VISIBLE);
                                    layout_not_found.setVisibility(View.GONE);
                                    layout_failed.setVisibility(View.GONE);
                                    progressBar_failed.setVisibility(View.GONE);


                                    for(int i=0; i<(msg.length()-1); i++) {
                                        JSONObject taxi = msg.getJSONObject(String.valueOf(i));
                                        albumList_ride.add(new RideModel(taxi.getInt("id"),taxi.getInt("id_user_app"),taxi.getInt("id_conducteur"), taxi.getString("nom")+" "+taxi.getString("prenom"), taxi.getString("nomConducteur")+" "+taxi.getString("prenomConducteur"), taxi.getString("distance"),taxi.getString("creer"),taxi.getString("statut"),taxi.getString("latitude_depart")
                                                ,taxi.getString("longitude_depart"),taxi.getString("latitude_arrivee"),taxi.getString("longitude_arrivee")
                                                ,taxi.getString("niveau"),taxi.getString("moyenne"),taxi.getString("nb_avis"),taxi.getString("montant"),taxi.getString("duree"),taxi.getString("depart_name"),taxi.getString("destination_name")
                                                ,taxi.getString("trajet"),taxi.getString("driverPhone"),taxi.getString("phone"),taxi.getString("statut_paiement")
                                                ,taxi.getString("payment") ,taxi.getString("payment_image"),taxi.getString("place"),taxi.getString("number_poeple"),taxi.getString("heure_retour"),taxi.getString("statut_round"),taxi.getString("date_retour"),taxi.getString("photo_path"),taxi.getString("comment")));
                                        adapter_ride.notifyDataSetChanged();
                                        distance_client =  taxi.getString("duree");
                                    }
                                }else{
                                    layout_liste.setVisibility(View.GONE);
                                    layout_not_found.setVisibility(View.VISIBLE);
                                    layout_failed.setVisibility(View.GONE);
                                    progressBar_failed.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    layout_liste.setVisibility(View.GONE);
                    layout_not_found.setVisibility(View.GONE);
                    layout_failed.setVisibility(View.VISIBLE);
                    progressBar_failed.setVisibility(View.GONE);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_user_app", M.getID(context));
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

    public static void showNotFound(){
        layout_liste.setVisibility(View.GONE);
        layout_not_found.setVisibility(View.VISIBLE);
        layout_failed.setVisibility(View.GONE);
        progressBar_failed.setVisibility(View.GONE);
    }
    public static void notificationRing(){
//        Toast.makeText(context, "!", Toast.LENGTH_SHORT).show();
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void dialogNotification(int title, int message){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogCountDown(20);
                timer.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        }).show();
    }
    public static void dialogCountDown(int min){
        M.setTimeStart(context,String.valueOf(System.currentTimeMillis()/1000));
        M.setDriverCT(context,"1");
        timer.setVisibility(View.VISIBLE);
        mCvCountdownView.start(1000*60*min);

//        new CountDownTimer(1000 * 60 * min, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//            @Override
//            public void onFinish() {
//
//            }
//        }.start();
//        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setCancelable(false);
//        builder.setTitle(R.string.wait_please);
//        new CountDownTimer(1000*60*min, 1000) {
//            @SuppressLint("DefaultLocale")
//            @Override
//            public void onTick(long millisUntilFinished) {
//                time =  ""+String.format("%d min, %d sec",
//                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
//                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
//                builder.setMessage("Driver is coming towards you in \n"+time);
//                builder.show();
//            }
//            @Override
//            public void onFinish() {
//                notificationRing();
//              builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                  @Override
//                  public void onClick(DialogInterface dialog, int which) {
//                      dialog.dismiss();
//                  }
//              });
//            }
//        }.start();
    }
    @Override
    public void onEnd(CountdownView cv) {
        Object tag = cv.getTag();
        if (null != tag) {
            Log.i("wg", "tag = " + tag.toString());
            timer.setVisibility(View.GONE);
            time_completed.setVisibility(View.VISIBLE);
            M.setDriverCT(context,"0");
        }
    }

    private final Runnable m_Runnable = new Runnable(){
        public void run(){
            new getRideConfirmed().execute();
            String items = String.valueOf(adapter_ride.getItemCount());
            if (!M.getRideConfirmed(context).equals(items)) {
                notificationRing();
                dialogNotification(R.string.item_confirmed,R.string.your_ride_is_confirmed_please_check_confirmed_rides);
                M.setRideConfirmed(context,items);
            }

//            Toast.makeText(getContext(),"in runnable",Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(m_Runnable, 10000);
        }
    };//runnable
    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(m_Runnable);
        onDestroy();
    }

}
