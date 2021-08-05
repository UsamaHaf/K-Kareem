package canvasolutions.kreemcabs.drivers.fragment.driver;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.activity.MainActivity;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.fragment.customer.FragmentRideNew;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.service.WaitingService;
import canvasolutions.kreemcabs.drivers.settings.AppConst;
import canvasolutions.kreemcabs.drivers.settings.ConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentDashboard extends Fragment{

    ViewPager pager;
    TabLayout tabs;
    View view;
    public static Context context;
    public static ConnectionDetector connectionDetector;
    public String tag="FragmentHome";
    ArrayList<String> tabNames = new ArrayList<String>();
    int currpos=0;
    private RelativeLayout layout_new,layout_confirmed,layout_onride,layout_completed,layout_sales,layout_today_earning,layout_new_booking,layout_confirmed_booking;
    public static ProgressBar progressBar_failed;
    public static LinearLayout layout_not_found,layout_failed;
    public static LinearLayout layout_liste;
    private static TextView dash_new_nb,dash_confirmed_nb,dash_onride_nb,dash_completed_nb,dash_today_earning_nb, dash_sales_nb,dash_new_nb_booking,dash_confirmed_nb_booking;
    private int mYear_depart, mMonth_depart, mDay_depart;
    Handler mHandler;


//    private static String today;

    public static SwipeRefreshLayout swipe_refresh;

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
        view= inflater.inflate(R.layout.fragment_dashboard, container, false);

        context=getActivity();
        MainActivity.setTitle(getString(R.string.app_name));
//        if(M.getCountry(context).equals("All"))
//            MainActivity.setTitle("Dashboard");
//        else
//            MainActivity.setTitle("Dashboard - "+M.getCountry(context));
        connectionDetector=new ConnectionDetector(context);

        layout_new = (RelativeLayout) view.findViewById(R.id.layout_new);
        layout_confirmed = (RelativeLayout) view.findViewById(R.id.layout_confirmed);
        layout_onride = (RelativeLayout) view.findViewById(R.id.layout_onride);
        layout_new_booking = (RelativeLayout) view.findViewById(R.id.layout_new_booking);
        layout_confirmed_booking = (RelativeLayout) view.findViewById(R.id.layout_confirmed_booking);
        layout_completed = (RelativeLayout) view.findViewById(R.id.layout_completed);
        layout_sales = (RelativeLayout) view.findViewById(R.id.layout_sales);
        layout_today_earning = (RelativeLayout) view.findViewById(R.id.layout_today_earning);
        progressBar_failed = (ProgressBar) view.findViewById(R.id.progressBar_failed);
        layout_liste = (LinearLayout) view.findViewById(R.id.layout_liste);
        layout_not_found = (LinearLayout) view.findViewById(R.id.layout_not_found);
        layout_failed = (LinearLayout) view.findViewById(R.id.layout_failed);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        dash_new_nb = (TextView) view.findViewById(R.id.dash_new_nb);
        dash_new_nb_booking = (TextView) view.findViewById(R.id.dash_new_nb_booking);
        dash_confirmed_nb_booking = (TextView) view.findViewById(R.id.dash_confirmed_nb_booking);
        dash_confirmed_nb = (TextView) view.findViewById(R.id.dash_confirmed_nb);
        dash_onride_nb = (TextView) view.findViewById(R.id.dash_onride_nb);
        dash_completed_nb = (TextView) view.findViewById(R.id.dash_completed_nb);
        dash_today_earning_nb = (TextView) view.findViewById(R.id.dash_today_earning_nb);
        dash_sales_nb = (TextView) view.findViewById(R.id.dash_sales_nb);





        layout_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.selectItem(1);
                MainActivity.selectItemDriver(MainActivity.NEW);
            }
        });
        layout_confirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.selectItem(2);
                MainActivity.selectItemDriver(MainActivity.CONFIRMED);
            }
        });
        layout_onride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.selectItem(3);
                MainActivity.selectItemDriver(MainActivity.ON_RIDE);
            }
        });
        layout_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.selectItem(4);
                MainActivity.selectItemDriver(MainActivity.COMPLETED);
            }
        });
        layout_new_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.selectItem(6);
                MainActivity.selectItemDriver(MainActivity.NEW_BOOKING);
            }
        });
        layout_confirmed_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.selectItem(7);
                MainActivity.selectItemDriver(MainActivity.CONFIRMED_BOOKING);
            }
        });


        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getDashboard().execute();
            }
        });

        layout_failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar_failed.setVisibility(View.VISIBLE);
                new getDashboard().execute();
            }
        });

        Calendar c = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            c = Calendar.getInstance();
            mYear_depart = c.get(Calendar.YEAR);
            mMonth_depart = c.get(Calendar.MONTH);
            mDay_depart = c.get(Calendar.DAY_OF_MONTH);
        }
//        today = mYear_depart+"-"+mMonth_depart+"-"+mDay_depart;

        swipe_refresh.setRefreshing(true);
        new getDashboard().execute();

        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable,10000);
//        context.startService(new Intent(context, WaitingService.class).putExtra("ride_id","2"));
        return view;
    }



    private final Runnable m_Runnable = new Runnable(){
        public void run(){
            new getDashboard().execute();
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




    /** Récupération des nombres par statut**/
    public static class getDashboard extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"get_dashboard.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d("TAGNEW", "onResponse: "+response);
                            try {
                                swipe_refresh.setRefreshing(false);
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    layout_liste.setVisibility(View.VISIBLE);
                                    layout_not_found.setVisibility(View.GONE);
                                    layout_failed.setVisibility(View.GONE);
                                    progressBar_failed.setVisibility(View.GONE);

                                    JSONObject nombre = msg.getJSONObject(String.valueOf(0));




                                    if (M.getNumber_new(context) == null){
                                        M.setNumber_new("0",context);
                                    }
                                    if (M.getNumber_confirmed(context)== null){
                                        M.setNumber_confirmed("0",context);
                                    }
                                    if (M.getNumber_completed(context)== null){
                                        M.setNumber_completed("0",context);
                                    }
                                    if (M.getNumber_onride(context)== null){
                                        M.setNumber_onride("0",context);
                                    }
                                    if (M.getNumber_sales(context)== null){
                                        M.setNumber_sales("0",context);
                                    }
                                    if (M.getRideConfirmedBooking(context)== null){
                                        M.setRideConfirmedBooking(context,"0");
                                    }
                                    if (M.getRideNewBooking(context)== null){
                                        M.setRideNewBooking(context,"0");
                                    }

                                    Log.d("NBNEW", "onResponse: "+nombre.getString("nb_new"));
                                    Log.d("NBNEW", "onResponse: "+M.getNumber_new(context));

                                    Log.d("TAGNEW", "onResponse: "+nombre.getString("nb_new"));

                                    if (Integer.parseInt(nombre.getString("nb_new")) > Integer.parseInt(M.getNumber_new(context))){
                                        M.setNumber_new(nombre.getString("nb_new"),context);
                                        dash_new_nb.setText(M.getNumber_new(context));
//                                        notificationRing();
//                                        dialogNotification(R.string.new_booking,R.string.you_have_a_new_ride_please_check_your_ndashboard);
                                    }else {
                                        M.setNumber_new(nombre.getString("nb_new"),context);
                                        dash_new_nb.setText(M.getNumber_new(context));
                                    }

                                    if (Integer.parseInt(nombre.getString("nb_confirmed")) > Integer.parseInt(M.getNumber_confirmed(context))){
                                        M.setNumber_confirmed(nombre.getString("nb_confirmed"),context);
                                        dash_confirmed_nb.setText(M.getNumber_confirmed(context));
//                                        notificationRing();
//                                        dialogNotification(R.string.confirm_booking,R.string.your_ride_is_confirmed_please_check_confirmed_rides);
                                    }else {
                                        dash_confirmed_nb.setText(nombre.getString("nb_confirmed"));
                                        M.setNumber_confirmed(nombre.getString("nb_confirmed"),context);
                                    }

                                    if (!nombre.getString("nb_onride").equals(M.getNumber_onride(context))){
                                        M.setNumber_onride(nombre.getString("nb_onride"),context);
                                        dash_onride_nb.setText(M.getNumber_onride(context));
                                    }else {
                                        dash_onride_nb.setText(M.getNumber_onride(context));
                                    }
                                    if (dash_onride_nb.getText().toString().equals("1")){
//                                        context.startService(new Intent(context,WaitingService.class));
                                        ContextCompat.startForegroundService(context, new Intent(context,WaitingService.class));
                                    }
                                    if (Integer.parseInt(nombre.getString("nb_completed")) > Integer.parseInt(M.getNumber_completed(context))){
                                        M.setNumber_completed(nombre.getString("nb_completed"),context);
                                        dash_completed_nb.setText(M.getNumber_completed(context));
//                                        notificationRing();
//                                        dialogNotification(R.string.completed,R.string.your_ride_completed_successfully);
                                    }else {
                                        M.setNumber_completed(nombre.getString("nb_completed"),context);
                                        dash_completed_nb.setText(M.getNumber_completed(context));
                                    }

                                    if (!nombre.getString("nb_sales").equals(M.getNumber_sales(context))){
                                        M.setNumber_sales(nombre.getString("nb_sales"),context);
                                        dash_sales_nb.setText(M.getNumber_sales(context));
                                    }else {
                                        dash_sales_nb.setText(M.getNumber_sales(context));
                                    }
                                    if (!nombre.getString("new_book").equals(M.getRideNewBooking(context))){
                                        M.setRideNewBooking(context,nombre.getString("new_book"));
                                        dash_new_nb_booking.setText(M.getRideNewBooking(context));
                                    }else {
                                        dash_new_nb_booking.setText(M.getRideNewBooking(context));
                                    }
                                    if (!nombre.getString("comfirm_book").equals(M.getRideConfirmedBooking(context))){
                                        M.setRideConfirmedBooking(context,nombre.getString("comfirm_book"));
                                        dash_confirmed_nb_booking.setText(M.getRideConfirmedBooking(context));
                                    }else {
                                        dash_confirmed_nb_booking.setText(M.getRideConfirmedBooking(context));
                                    }

//
//                                    dash_new_nb.setText(nombre.getString("nb_new"));
//                                    dash_confirmed_nb.setText(nombre.getString("nb_confirmed"));
//                                    dash_onride_nb.setText(nombre.getString("nb_onride"));
//                                    dash_completed_nb.setText(nombre.getString("nb_completed"));
//                                    dash_sales_nb.setText(nombre.getString("nb_sales"));


                                    dash_today_earning_nb.setText(M.getCurrency(context)+" "+nombre.getString("today_earning"));
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
                    params.put("id_diver", M.getID(context));
//                    params.put("today", today);
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
                dialog.dismiss();
            }
        }).show();
    }
}
