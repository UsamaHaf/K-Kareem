package canvasolutions.kreemcabs.drivers.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textfield.TextInputLayout;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.activity.MainActivity;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.model.RideModel;
import canvasolutions.kreemcabs.drivers.service.WaitingService;
import canvasolutions.kreemcabs.drivers.settings.AppConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class RideDriverAdapter extends RecyclerView.Adapter<RideDriverAdapter.MyViewHolder> {
    private static final int LOCATION_REQUEST_CODE = 101;
    private Context context;
    private List<RideModel> albumList;
    Activity activity;
    private String currentActivity;
    private String distance = "";
    private String promoType = "";
    private  String isPromoApplied = "false";
    private String promoValue = "";
    private String board_end = "0";
    final private String[][] tab = {{}};
    final private String[][] tab1 = { {} };
    private static final int REQUEST_PHONE_CALL = 1;
    public Location currentLocation = new Location("dummyprovider10");
    LocationManager locationManager;

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private TextView depart,destination,cost_ride,distance_ride,duration_ride,
                name_customer,statut_ride,date_ride, reject,view_map;
//        private LinearLayout layout_distance_requete;
        private RelativeLayout relative_layout;
        private ImageView call_customer,call_customer_2,date_book,payment_method,round_trip,message_customer;
        private TextView confirm,on_ride,start_ride,complet_ride,pay,confirmed_cancel,onride_cancel,ct_onride_cancel,onboard_ride,bill;
        private TextView place,number_people,heure_retour,date_retour,at;

        public MyViewHolder(View view) {
            super(view);
            if(currentActivity.equals("RideNewDriver")){
//                user_name_requete = (TextView) view.findViewById(R.id.user_name_requete);
//                distance_client_requete = (TextView) view.findViewById(R.id.distance_client_requete);
            }
            depart = (TextView) view.findViewById(R.id.depart);
            destination = (TextView) view.findViewById(R.id.destination);
            cost_ride = (TextView) view.findViewById(R.id.cost_ride);
            distance_ride = (TextView) view.findViewById(R.id.distance_ride);
            duration_ride = (TextView) view.findViewById(R.id.duration_ride);
            name_customer = (TextView) view.findViewById(R.id.name_customer);
            statut_ride = (TextView) view.findViewById(R.id.statut_ride);
            date_ride = (TextView) view.findViewById(R.id.date_ride);
            reject = (TextView) view.findViewById(R.id.reject);
            view_map = (TextView) view.findViewById(R.id.view_map);
            confirm = (TextView) view.findViewById(R.id.confirm);
            confirmed_cancel = (TextView)view.findViewById(R.id.confirmed_cancel);
            onride_cancel = (TextView)view.findViewById(R.id.onride_cancel);
            ct_onride_cancel = (TextView)view.findViewById(R.id.ct_onride_cancel);
            onboard_ride = (TextView)view.findViewById(R.id.on_board);
//            layout_distance_requete = (LinearLayout) view.findViewById(R.id.layout_distance_requete);
            relative_layout = (RelativeLayout) view.findViewById(R.id.relative_layout);
            call_customer = (ImageView) view.findViewById(R.id.call_customer);
            message_customer = (ImageView) view.findViewById(R.id.message_customer_2);
            call_customer_2 = (ImageView) view.findViewById(R.id.call_customer_2);
            date_book = (ImageView) view.findViewById(R.id.date_book);
            on_ride = (TextView) view.findViewById(R.id.on_ride);
            start_ride = (TextView) view.findViewById(R.id.start_ride);
            complet_ride = (TextView) view.findViewById(R.id.complet_ride);
            payment_method = (ImageView) view.findViewById(R.id.payment_method);
            pay = (TextView) view.findViewById(R.id.pay);
            place = (TextView) view.findViewById(R.id.place);
            number_people = (TextView) view.findViewById(R.id.number_people);
            round_trip = (ImageView) view.findViewById(R.id.round_trip);
            heure_retour = (TextView) view.findViewById(R.id.heure_retour);
            date_retour = (TextView) view.findViewById(R.id.date_retour);
            bill = (TextView) view.findViewById(R.id.bill);
            at = (TextView) view.findViewById(R.id.at);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            RideModel rideModel = albumList.get(getAdapterPosition());

        }
    }

    public RideDriverAdapter(Context context, List<RideModel> albumList, Activity activity, String currentActivity) {
        this.context = context;
        this.albumList = albumList;
        this.activity = activity;
        this.currentActivity = currentActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
//        if(currentActivity.equals("RideNewDriver"))
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card_ride_new_rider, parent, false);
//        else
//            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card_ride_new, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final RideModel rideModel = albumList.get(position);
        if(rideModel.getStatut_round().equals("yes")){
            holder.round_trip.setVisibility(View.VISIBLE);
            holder.heure_retour.setVisibility(View.VISIBLE);
            holder.heure_retour.setText(rideModel.getHeure_retour());
            holder.date_retour.setVisibility(View.VISIBLE);
            holder.date_retour.setText(rideModel.getDate_retour());
            holder.at.setVisibility(View.VISIBLE);
        }else {
            holder.round_trip.setVisibility(View.INVISIBLE);
            holder.heure_retour.setVisibility(View.INVISIBLE);
            holder.heure_retour.setText("");
            holder.date_retour.setVisibility(View.INVISIBLE);
            holder.date_retour.setText("");
            holder.at.setVisibility(View.INVISIBLE);
        }
        holder.number_people.setText(rideModel.getNumber_poeple());
        if(currentActivity.equals("RideNew")){
            holder.reject.setVisibility(View.VISIBLE);
            holder.confirm.setVisibility(View.VISIBLE);
            holder.call_customer_2.setVisibility(View.GONE);
            holder.call_customer.setVisibility(View.VISIBLE);
            holder.message_customer.setVisibility(View.VISIBLE);
            holder.on_ride.setVisibility(View.GONE);
            holder.start_ride.setVisibility(View.GONE);
            holder.complet_ride.setVisibility(View.GONE);
        }
        else if(currentActivity.equals("RideConfirmed")){
            String rideID = String.valueOf(rideModel.getId());
            Log.d("TAGIDD", "onBindViewHolder:SAVED ID "+ M.getCancelConfirmedRideID(context)+"\n getID"+ rideModel.getId());
            if (M.getCancelConfirmedRideID(context) != null && !M.getTimeStartConfirmedCancel(context).equals("")){
                Log.d("TAGIDD", "onBindViewHolder: NOT NULL");
                if (M.getCancelConfirmedRideID(context).equals(rideID)){
                    Log.d("TAGIDD", "onBindViewHolder: MATCH");
                    long current = System.currentTimeMillis()/1000;
                    Log.d("TAGIDD", "CURRENT"+current+"START"+M.getTimeStartConfirmedCancel(context));
                    long timeR = (Long.parseLong(M.getTimeStartConfirmedCancel(context))+(30*1000)) - (System.currentTimeMillis()/1000);
                    Log.d("TAGIDD", "COUNTDOWN"+timeR);
                    new CountDownTimer(timeR, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.d("TAGENTERED", "onTick: ENTERED");
                            if (Long.parseLong(M.getTimeStartConfirmedCancel(context))+10 > current){
                                holder.confirmed_cancel.setVisibility(View.VISIBLE);
                            }else {
                                holder.confirmed_cancel.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFinish() {
                            holder.confirmed_cancel.setVisibility(View.GONE);
                        }
                    }.start();
                }
            }
            holder.confirmed_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    new rejectRide().execute(String.valueOf(ridePojo.getId()),String.valueOf(position),String.valueOf(ridePojo.getUser_id()),ridePojo.getConducteur_name());
                    showMessageReject(rideModel.getId(),position, rideModel.getUser_id(), rideModel.getConducteur_name());
                }
            });
            holder.on_ride.setVisibility(View.VISIBLE);
            holder.call_customer_2.setVisibility(View.GONE);
            holder.call_customer.setVisibility(View.VISIBLE);
            holder.message_customer.setVisibility(View.VISIBLE);
            holder.reject.setVisibility(View.GONE);
            holder.confirm.setVisibility(View.GONE);
            holder.start_ride.setVisibility(View.GONE);
            holder.complet_ride.setVisibility(View.GONE);
        }
        else if(currentActivity.equals("RideOnRide")){
            String rideID = String.valueOf(rideModel.getId());
            String rideIdPrefs = M.getValue(context,M.CANCEL_RIDE_ID_KEY);
            Log.d("TAGIDD", "onBindViewHolder:SAVED ID "+ rideIdPrefs+"\n getID "+rideID);
            if (rideIdPrefs != null && !rideIdPrefs.equals("")){
                Log.d("TAGIDD", "onBindViewHolder: NOT NULL");
                if (rideIdPrefs.equals(rideID)){
                    Log.d("TAGIDD", "onBindViewHolder: MATCH");
                    long current = System.currentTimeMillis()/1000;
                    Log.d("TAGIDD", "CURRENT "+current+"START "+M.getValue(context,M.CANCEL_RIDE_TS_KEY));
                    long timeR = (Long.parseLong(M.getValue(context,M.CANCEL_RIDE_TS_KEY))+(10*60)) - current;
                    Log.d("TAGIDD", "COUNTDOWN "+timeR);
                    Log.d("TAGREJECT", "COUNTDOWN "+M.getValue(context,M.SHOULD_REJECT_VISIBLE));
                    if (M.getValue(context,M.SHOULD_REJECT_VISIBLE).equals("true")){
                        if (Long.parseLong(M.getValue(context,M.CANCEL_RIDE_TS_KEY))+(10*60) < current){
                            holder.onride_cancel.setVisibility(View.VISIBLE);
                            holder.ct_onride_cancel.setVisibility(View.GONE);
                        }else {
                            new CountDownTimer(timeR * 1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    Log.d("TAGENTERED", "onTick: ENTERED");

                                    String sec = null;
                                    String min = null;
                                    if ((millisUntilFinished / 1000) % 60 < 10)
                                        sec = "0" + ((millisUntilFinished / 1000) % 60);
                                    else sec = "" + ((millisUntilFinished / 1000) % 60);
                                    if ((millisUntilFinished / 1000) / 60 < 10)
                                        min = "0" + (millisUntilFinished / 1000) / 60;
                                    else min = "" + (millisUntilFinished / 1000) / 60;

                                    holder.onride_cancel.setVisibility(View.GONE);
                                    holder.ct_onride_cancel.setText(context.getString(R.string.you_can_reject_ride_in) +"\n"+ min + ":" + sec);

                                }

                                @Override
                                public void onFinish() {
                                    holder.ct_onride_cancel.setVisibility(View.GONE);
                                    holder.onride_cancel.setVisibility(View.VISIBLE);
                                }
                            }.start();
                        }
                    }

                }
            }

            holder.onride_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialogRejectionReason(rideID,position, rideModel.getUser_id(), rideModel.getConducteur_name(), rideModel.getConducteur_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.onboard_ride.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMessageOnBoard(Integer.parseInt(rideID),position, rideModel.getUser_id());
                }
            });
            if (M.getValue(context,M.SHOULD_REJECT_VISIBLE).equals("true")){
                holder.start_ride.setVisibility(View.VISIBLE);
                holder.complet_ride.setVisibility(View.GONE);
            }else {
                holder.start_ride.setVisibility(View.GONE);
                holder.complet_ride.setVisibility(View.VISIBLE);
            }
            holder.call_customer_2.setVisibility(View.GONE);
            holder.call_customer.setVisibility(View.VISIBLE);
            holder.message_customer.setVisibility(View.VISIBLE);
            holder.ct_onride_cancel.setVisibility(View.VISIBLE);
            holder.reject.setVisibility(View.GONE);
            holder.confirm.setVisibility(View.GONE);
            holder.on_ride.setVisibility(View.GONE);
        }else{
            holder.reject.setVisibility(View.GONE);
            holder.confirm.setVisibility(View.GONE);
            holder.call_customer_2.setVisibility(View.VISIBLE);
            holder.call_customer.setVisibility(View.GONE);
            holder.message_customer.setVisibility(View.GONE);
            holder.on_ride.setVisibility(View.GONE);
            holder.start_ride.setVisibility(View.GONE);
            holder.complet_ride.setVisibility(View.GONE);
        }

        if(rideModel.getStatut().equals("completed")){
            holder.pay.setVisibility(View.VISIBLE);
            holder.bill.setVisibility(View.VISIBLE);
            holder.pay.setText(R.string.paid);
            holder.pay.setBackground(context.getResources().getDrawable(R.drawable.custom_bg_statut_valide));
            holder.pay.setEnabled(false);
            holder.pay.setTextColor(Color.WHITE);

        }else{
            holder.pay.setVisibility(View.GONE);
        }
        distance = rideModel.getDistance();
        if(distance.length() > 3) {
            String virgule = distance.substring(distance.length() - 3,distance.length() - 2);
            distance = distance.substring(0, distance.length() - 3);
            distance = distance+"."+virgule+" km";
        }else
            distance = distance+R.string.meter;
//        holder.status_ride.setTextColor(context.getResources().getColor(R.color.colorLogoBlack));
        holder.depart.setText(rideModel.getDepart_name());
        holder.destination.setText(rideModel.getDestination_name());
        holder.date_ride.setText(rideModel.getDate());
//        holder.cost_ride.setText(ridePojo.getCout()+" "+ M.getCurrency(context));
        holder.cost_ride.setText(R.string.min_7);
        holder.distance_ride.setText(distance);
        holder.duration_ride.setText(rideModel.getDuree());
//        holder.statut_ride.setText(ridePojo.getStatut());
        if (rideModel.getStatut().equals("completed"))holder.statut_ride.setText(R.string.completed_small);
        else if (rideModel.getStatut().equals("new"))holder.statut_ride.setText(R.string.item_new);
        else if (rideModel.getStatut().equals("on ride"))holder.statut_ride.setText(R.string.on_ride_small);
        else if (rideModel.getStatut().equals("rejected"))holder.statut_ride.setText(R.string.item_rejected);
        else if (rideModel.getStatut().equals("confirmed"))holder.statut_ride.setText(R.string.confirmed_small);
        holder.name_customer.setText(rideModel.getUser_name());
        holder.bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                M.showLoadingDialog(context);
                new getBill().execute(String.valueOf(rideModel.getId()));
            }
        });
        holder.pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.view_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialogViewMap(rideModel.getTrajet());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageReject(rideModel.getId(),position, rideModel.getUser_id(), rideModel.getConducteur_name());
            }
        });
        holder.call_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNumber(rideModel.getCustomer_phone());
            }
        });
        holder.message_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageNumber(rideModel.getCustomer_phone());
            }
        });
        holder.call_customer_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNumber(rideModel.getCustomer_phone());
            }
        });
        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.currentLocation != null) {
                    showMessageConfirm(rideModel.getId(), position, rideModel.getUser_id(), rideModel.getConducteur_name(), MainActivity.currentLocation.getLatitude(), MainActivity.currentLocation.getLongitude(), rideModel.getLatitude_client(), rideModel.getLongitude_client());
                } else{
                    showMessageEnabledGPS();
                }
            }
        });
        holder.on_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageOnRide(rideModel.getId(),position, rideModel.getUser_id());
            }
        });
        holder.complet_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = getLocation();
                if (MainActivity.currentLocation != null){
                    Intent serviceIntent = new Intent(context, WaitingService.class);
                    context.stopService(serviceIntent);
                    showMessageComplete(rideModel.getId(),position, rideModel.getUser_id(), rideModel.getLatitude_client(), rideModel.getLongitude_client(),String.valueOf(MainActivity.currentLocation.getLatitude()),String.valueOf(MainActivity.currentLocation.getLongitude()));
                }

            }
        });
        holder.start_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentActivity.equals("RideConfirmed")){
                    if(isGoogleMapsInstalled()) {
                        loadNavigationView(rideModel.getLatitude_client(), rideModel.getLongitude_client());
                        ContextCompat.startForegroundService(context, new Intent(context, WaitingService.class).putExtra("ride_id", rideModel.getId()));
                    }else
                        Toast.makeText(context, context.getResources().getString(R.string.you_need_to_install_google_map_app), Toast.LENGTH_SHORT).show();
                }else{
                    holder.complet_ride.setVisibility(View.VISIBLE);
                    holder.onride_cancel.setVisibility(View.GONE);
                    holder.ct_onride_cancel.setVisibility(View.GONE);
                    holder.start_ride.setVisibility(View.GONE);
                    M.setValue(context,"false",M.SHOULD_REJECT_VISIBLE);
                    Log.d("TAGREJECT", "COUNTDOWN "+M.getValue(context,M.SHOULD_REJECT_VISIBLE));
//                    context.startService(new Intent(context, WaitingService.class).putExtra("ride_id", rideModel.getId()));
                    ContextCompat.startForegroundService(context, new Intent(context, WaitingService.class).putExtra("ride_id", rideModel.getId()));
                    M.setValue(context,"false",M.SHOULD_SERVICE_STOP);
                    if(isGoogleMapsInstalled())
                        loadNavigationView(rideModel.getLatitude_destination(), rideModel.getLongitude_destination());
                    else
                        Toast.makeText(context, context.getResources().getString(R.string.you_need_to_install_google_map_app), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // loading model cover using Glide library
        Glide.with(context).load(AppConst.Server_urlMain+"assets/images/payment_method/"+ rideModel.getImg_payment_method())
                .skipMemoryCache(false)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.payment_method);
        holder.place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGoogleMapsInstalled())
                    loadNavigationView(String.valueOf(rideModel.getLatitude_client()),String.valueOf(rideModel.getLongitude_client()));
                else
                    Toast.makeText(context, context.getResources().getString(R.string.you_need_to_install_google_map_app), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void showMessageEnabledGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.must_activate_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //This method would confirm the otp
    private void dialogPlace(String place) throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(context);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_layout_exactly_place, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        TextView input_place = (TextView) confirmDialog.findViewById(R.id.input_place);
        TextView cancel = (TextView) confirmDialog.findViewById(R.id.cancel);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();

        input_place.setText(place);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    public boolean isGoogleMapsInstalled() {
        try{
            PackageInfo info = context.getPackageManager().getPackageInfo("com.google.android.apps.maps",0);
            return true;
        } catch(PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public void showEndRideBtn(TextView onBoard,TextView endRide){
        if (board_end.equals("1")){
            onBoard.setVisibility(View.GONE);
            endRide.setVisibility(View.VISIBLE);
        }
    }

    public void loadNavigationView(String lat,String lng){
        Uri navigation = Uri.parse("google.navigation:q="+lat+","+lng+"");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
        navigationIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(navigationIntent);
    }

    public void showMessageReject(final int idRide, final int position, final int id_user, final String driver_name){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.do_you_want_to_reject_this_ride))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        M.showLoadingDialog(context);
                        new rejectRide().execute(String.valueOf(idRide),String.valueOf(position),String.valueOf(id_user),driver_name);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /** Appeler numéro de téléphone **/
    public void callNumber(String numero){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }
        else
        {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel: "+numero.trim()));
            if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            context.startActivity(callIntent);
        }
    }
    public void messageNumber(String number){
        Intent messageIntent = new Intent(Intent.ACTION_VIEW);
        messageIntent.setType("vnd.android-dir/mms-sms");
        messageIntent.putExtra("address",number);
        context.startActivity(messageIntent);

    }

    //This method would confirm the otp
    private void dialogViewMap(String img) throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(context);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_layout_view_map, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        ImageView image = (ImageView) confirmDialog.findViewById(R.id.image);
        final ProgressBar progressBar = (ProgressBar) confirmDialog.findViewById(R.id.progressBar);

        // loading model cover using Glide library
        Glide.with(context).load(AppConst.Server_url+"images/recu_trajet_course/"+ img)
                .skipMemoryCache(false)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();
    }
    private void dialogBill(String ride_id,String bill_id,String cout, String traveling,String waiting,String subtotal,String from_wallet,String starting) throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        Log.d("TAGBILL", "dialogBill: IN BILL");
        LayoutInflater li = LayoutInflater.from(context);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_layout_payment_amount1, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        Button close =  confirmDialog.findViewById(R.id.close);
        TextView total = (TextView) confirmDialog.findViewById(R.id.total_amount);
        TextView total1 = (TextView) confirmDialog.findViewById(R.id.total_amount1);
        TextView starting_amount = (TextView) confirmDialog.findViewById(R.id.starting_amount);
        TextView traveling_amount = (TextView) confirmDialog.findViewById(R.id.traveling);
        TextView waiting_amount = (TextView) confirmDialog.findViewById(R.id.waiting_amount);
        TextView sub_total = (TextView) confirmDialog.findViewById(R.id.sub_total);
        TextView wallet = (TextView) confirmDialog.findViewById(R.id.from_wallet);
        TextView currency = (TextView) confirmDialog.findViewById(R.id.currency);
        TextView receipt_id = (TextView) confirmDialog.findViewById(R.id.reciept_id);
        EditText recieved_amount = confirmDialog.findViewById(R.id.recieved_amount);
        TextInputLayout textInputLayout = confirmDialog.findViewById(R.id.input_layout_received_amount);
        ProgressBar progressBar_promo =  confirmDialog.findViewById(R.id.progressBar_promo);
        EditText promo_code =  confirmDialog.findViewById(R.id.promo_code);
        TextView apply_promo_code = confirmDialog.findViewById(R.id.apply_promo_code);
        TextView cancel_promo_code = confirmDialog.findViewById(R.id.cancel_promo_code);
        TextInputLayout input_layout_promo_code = confirmDialog.findViewById(R.id.input_layout_promo_code);

        recieved_amount.requestFocus();
        currency.setText(M.getCurrency(context));
        total.setText(cout);
        total1.setText(cout);
        traveling_amount.setText(traveling);
        starting_amount.setText(starting);
        waiting_amount.setText(waiting);
        sub_total.setText(subtotal);
        wallet.setText(from_wallet);
        receipt_id.setText(context.getString(R.string.receipt)+"#"+bill_id);


        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recieved_amount.getText().toString().trim().isEmpty()){
                    textInputLayout.setError(context.getString(R.string.enter_received_amount_error));
                    recieved_amount.requestFocus();
                }else {
                    M.showLoadingDialog(context);
                    String received = recieved_amount.getText().toString().trim();
                    String extra = "0";
                    if (Float.parseFloat(received) > Float.parseFloat(cout)){
                        extra = String.valueOf(Float.parseFloat(received) - Float.parseFloat(cout));
                    }
                    new setPayment().execute(ride_id,received,subtotal,extra,from_wallet);
                    alertDialog.cancel();
                    MainActivity.selectItemDriver(MainActivity.COMPLETED);
                }
            }
        });
        apply_promo_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (promo_code.getText().toString().trim().isEmpty()) {
                    input_layout_promo_code.setError(context.getResources().getString(R.string.enter_your_promo_code));
                }else {
                    progressBar_promo.setVisibility(View.VISIBLE);
                    apply_promo_code.setVisibility(View.GONE);
                    getPromoCode(progressBar_promo,apply_promo_code,cancel_promo_code,input_layout_promo_code,promo_code);
                }
            }
        });
        cancel_promo_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_promo_code.setVisibility(View.GONE);
                apply_promo_code.setText(context.getResources().getString(R.string.apply_small));
                apply_promo_code.setEnabled(true);
                promo_code.setEnabled(true);
                promoType = "";
                promoValue = "";
                isPromoApplied = "false";
            }
        });
        alertDialog.setCancelable(false);
    }
    private void dialogShowBill(String ride_id,String bill_id,String cout, String traveling,String waiting,String subtotal,String from_wallet,String starting) throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        Log.d("TAGBILL", "dialogBill: IN BILL");
        LayoutInflater li = LayoutInflater.from(context);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_layout_payment_amount1, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        Button close =  confirmDialog.findViewById(R.id.close);
        TextView total = (TextView) confirmDialog.findViewById(R.id.total_amount);
        TextView total1 = (TextView) confirmDialog.findViewById(R.id.total_amount1);
        TextView starting_amount = (TextView) confirmDialog.findViewById(R.id.starting_amount);
        TextView traveling_amount = (TextView) confirmDialog.findViewById(R.id.traveling);
        TextView waiting_amount = (TextView) confirmDialog.findViewById(R.id.waiting_amount);
        TextView sub_total = (TextView) confirmDialog.findViewById(R.id.sub_total);
        TextView wallet = (TextView) confirmDialog.findViewById(R.id.from_wallet);
        TextView currency = (TextView) confirmDialog.findViewById(R.id.currency);
        TextView receipt_id = (TextView) confirmDialog.findViewById(R.id.reciept_id);
        EditText recieved_amount = confirmDialog.findViewById(R.id.recieved_amount);
        LinearLayout layout_promo = confirmDialog.findViewById(R.id.layout_promo);
        TextInputLayout textInputLayout = confirmDialog.findViewById(R.id.input_layout_received_amount);

        layout_promo.setVisibility(View.GONE);
        recieved_amount.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.GONE);
        currency.setText(M.getCurrency(context));
        total.setText(cout);
        total1.setText(cout);
        traveling_amount.setText(traveling);
        starting_amount.setText(starting);
        waiting_amount.setText(waiting);
        sub_total.setText(subtotal);
        wallet.setText(from_wallet);
        receipt_id.setText(context.getString(R.string.receipt)+"#"+bill_id);


        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.setCancelable(false);
    }
    //This method would confirm the otp
    private void dialogSucess(String message, String status) throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(context);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_layout_subscribe_success, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        TextView close = (TextView) confirmDialog.findViewById(R.id.close);
        TextView msg = (TextView) confirmDialog.findViewById(R.id.msg);

        msg.setText(message);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                switch (status){
//                    case "new" : MainActivity.selectItem(2); break;
                    case "confirmed" : MainActivity.selectItemDriver(MainActivity.CONFIRMED); break;
                    case "onride" : MainActivity.selectItemDriver(MainActivity.ON_RIDE); break;
                    case "completed" : MainActivity.selectItemDriver(MainActivity.COMPLETED); break;
                    case "onboard" :  break;
                    default : MainActivity.selectItemDriver(MainActivity.DASHBOARD); break;
                }
            }
        });
        alertDialog.setCancelable(false);
    }

    private void dialogRejectionReason(String idRide,int position,int id_user,String driver_name, int conductor_id) throws JSONException {
        String txtNoShow = "no_show";
        String txtOther = "other";
        LayoutInflater li = LayoutInflater.from(context);
        View confirmDialog = li.inflate(R.layout.dialog_layout_reject_reason, null);
        TextView no_show = (TextView) confirmDialog.findViewById(R.id.reason_no_show);
        TextView other = (TextView) confirmDialog.findViewById(R.id.reason_other);
        ImageView cancel = (ImageView)confirmDialog.findViewById(R.id.reason_cancel);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(confirmDialog);
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        no_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new rejectRideWithReason().execute(String.valueOf(idRide),String.valueOf(position),String.valueOf(id_user),driver_name,txtNoShow, String.valueOf(conductor_id));
                M.showLoadingDialog(context);
                alertDialog.dismiss();
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new rejectRideWithReason().execute(String.valueOf(idRide),String.valueOf(position),String.valueOf(id_user),driver_name,txtOther, String.valueOf(conductor_id));
                M.showLoadingDialog(context);
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setCancelable(false);
    }

    /** Annuler un requête **/
    public class rejectRide extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"rejected_requete.php";
            final String id_ride = params[0];
            final String position = params[1];
            final String id_user = params[2];
            final String driver_name = params[3];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    delete(Integer.parseInt(position));
                                    notifyDataSetChanged();
                                    dialogSucess(context.getResources().getString(R.string.rejected_successfull),"canceled");
                                }else{
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_ride", id_ride);
                    params.put("id_user", id_user);
                    params.put("driver_name", driver_name);
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
    public class rejectRideWithReason extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"rejected_requete_with_reason.php";
            final String id_ride = params[0];
            final String position = params[1];
            final String id_user = params[2];
            final String driver_name = params[3];
            final String reason = params[4];
            final String id_driver = params[5];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    delete(Integer.parseInt(position));
                                    notifyDataSetChanged();
                                    dialogSucess(context.getResources().getString(R.string.rejected_successfull),"canceled");
                                }else{
                                    Toast.makeText(context, R.string.something_went_wrong_plz_try_again, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_ride", id_ride);
                    params.put("id_user", id_user);
                    params.put("driver_name", driver_name);
                    params.put("reason", reason);
                    params.put("id_driver", id_driver);
                    params.put("cat_user", M.getUserCategorie(context));
//                    params.put("id_user_app", M.getID(context));
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


    public void showMessageConfirm(final int idRide, final int position, final int id_user, final String driver_name, final double lat_conducteur, final double lng_conducteur, final String lat_client, final String lng_client){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.do_you_want_to_confirm_this_ride))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        M.showLoadingDialog(context);
                        new confirmRide().execute(String.valueOf(idRide),String.valueOf(position),String.valueOf(id_user),driver_name, String.valueOf(lat_conducteur), String.valueOf(lng_conducteur), String.valueOf(lat_client), String.valueOf(lng_client));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void showMessageOnRide(final int idRide, final int position, final int id_user){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.do_you_want_to_on_ride_this_ride))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        M.showLoadingDialog(context);
                        M.setValue(context,"true",M.SHOULD_REJECT_VISIBLE);
                        new onRideRide().execute(String.valueOf(idRide),String.valueOf(position),String.valueOf(id_user));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void showMessageOnBoard(final int idRide, final int position, final int id_user){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.do_you_want_to_on_board_this_ride))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        M.showLoadingDialog(context);
                        new onBoardRide().execute(String.valueOf(idRide),String.valueOf(position),String.valueOf(id_user));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void showMessageComplete(final int idRide, final int position, final int id_user, String lat_depart,String lng_depart,String lat_dest,String lng_dest){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.do_you_want_to_complete_this_ride))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        M.showLoadingDialog(context);
                        M.setValue(context,"true",M.SHOULD_SERVICE_STOP);
//                        context.stopService(new Intent(context,WaitingService.class).setAction("STOP"));
//                        new generateBill().execute(String.valueOf(idRide),lat_depart,lng_depart,lat_dest,lng_dest);
                        new completeRide().execute(String.valueOf(idRide),String.valueOf(position),String.valueOf(id_user),lat_depart,lng_depart,lat_dest,lng_dest);
                        Log.d("TAGS", "onClick: "+M.getValue(context,M.SHOULD_SERVICE_STOP));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /** Confimer un requête **/
    public class confirmRide extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"confirmed_requete.php";
            final String id_ride = params[0];
            final String position = params[1];
            final String id_user = params[2];
            final String driver_name = params[3];

            final String lat_conducteur = params[4];
            final String lng_conducteur = params[5];
            final String lat_client = params[6];
            final String lng_client = params[7];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    delete(Integer.parseInt(position));
                                    notifyDataSetChanged();
                                    dialogSucess(context.getResources().getString(R.string.confirmed_successfull),"confirmed");
                                    M.setCancelConfirmedRideID(context, id_ride);
                                    M.setTimeStartConfirmedCancel(context,String.valueOf(System.currentTimeMillis()/1000));
                                }else{
                                    Toast.makeText(context, R.string.time_out_to_confirm, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_ride", id_ride);
                    params.put("driver_name", driver_name);
                    params.put("id_conducteur", M.getID(context));
                    params.put("id_user", id_user);
                    params.put("lat_conducteur", lat_conducteur);
                    params.put("lng_conducteur", lng_conducteur);
                    params.put("lat_client", lat_client);
                    params.put("lng_client", lng_client);
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

    /** On Ride un requête **/
    public class onRideRide extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"on_ride_requete.php";
            final String id_ride = params[0];
            final String position = params[1];
            final String id_user = params[2];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    delete(Integer.parseInt(position));
                                    notifyDataSetChanged();
                                    dialogSucess(context.getResources().getString(R.string.on_ride_successfull),"onride");
                                    M.setValue(context,id_ride,M.CANCEL_RIDE_ID_KEY);
                                    M.setValue(context, String.valueOf(System.currentTimeMillis()/1000),M.CANCEL_RIDE_TS_KEY);
                                }else{
                                    Toast.makeText(context, R.string.something_went_wrong_plz_try_again, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_ride", id_ride);
                    params.put("id_user", id_user);
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

    public class onBoardRide extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"on_board_requete.php";
            final String id_ride = params[0];
            final String position = params[1];
            final String id_user = params[2];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    delete(Integer.parseInt(position));
                                    notifyDataSetChanged();

                                    dialogSucess(context.getResources().getString(R.string.on_board_successfull),"onboard");
                                }else{
                                    Toast.makeText(context, R.string.something_went_wrong_plz_try_again, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_ride", id_ride);
                    params.put("id_user", id_user);
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
    /** Complete un requête **/
    public class completeRide extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"complete_requete.php";
            final String id_ride = params[0];
            final String position = params[1];
            final String id_user = params[2];
            final String lat_depart = params[3];
            final String lng_depart = params[4];
            final String lat_dest = params[5];
            final String lng_dest = params[6];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
//                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    delete(Integer.parseInt(position));
                                    notifyDataSetChanged();
                                    new generateBill().execute(id_ride,lat_depart,lng_depart,lat_dest,lng_dest);
//                                    Toast.makeText(context, "getBill", Toast.LENGTH_SHORT).show();

//                                    dialogSucess(context.getResources().getString(R.string.completed_successfull),"completed");
                                }else{
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_ride", id_ride);
                    params.put("id_user", id_user);
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
    public class getBill extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"get_bill.php";
            final String id_ride = params[0];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);
                                Log.d("TAGBILL", "onResponse: "+response);
                                JSONObject msg = json.getJSONObject("msg");
                                String bill_id = msg.getString("id");
                                String wait_price = msg.getString("wait_price");
                                String subtotal = msg.getString("subtotal");
                                String from_wallet = msg.getString("from_wallet");
                                String cout = msg.getString("cout");
                                String payment_method = msg.getString("payment_method");
//                                String traveling = msg.getString("minimum");
                                String traveling = msg.getString("traveling");
                                String starting = msg.getString("starting");
                                dialogShowBill(id_ride,bill_id,cout,traveling,wait_price,subtotal,from_wallet,starting);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_ride", id_ride);
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
    public class generateBill extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"generate_bill.php";
            final String id_ride = params[0];
            final String lat_depart = params[1];
            final String lng_depart = params[2];
            final String lat_dest = params[3];
            final String lng_dest = params[4];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("TAGBILL", "onResponse: "+response);
                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);

                                JSONObject msg = json.getJSONObject("msg");
                                Log.d("TAGBILL", "onResponse: "+msg);
                                String wait_price = String.valueOf(Math.round(Float.parseFloat(msg.getString("wait_price"))));

                                Log.d("TAGBILL", "onResponse: "+wait_price);

                                String subtotal = String.valueOf(Math.round(Float.parseFloat(msg.getString("subtotal"))));
                                Log.d("TAGBILL", "onResponse: "+subtotal);
                                String from_wallet = msg.getString("from_wallet");
                                Log.d("TAGBILL", "onResponse: "+from_wallet);

                                String cout = String.valueOf(Math.round(Float.parseFloat(msg.getString("cout"))));
                                Log.d("TAGBILL", "onResponse: "+cout);
                                String payment_method = msg.getString("payment_method");
                                Log.d("TAGBILL", "onResponse: "+payment_method);
                                String minimum = msg.getString("minimum");
                                String bill_id = msg.getString("bill_id");
                                String starting = msg.getString("starting");
                                String traveling = msg.getString("traveling");
                                Log.d("TAGBILL", "onResponse: "+minimum);
                                Log.d("TAGBILL", "onResponse: "+wait_price+"  "+subtotal+"  "+from_wallet+"  "+cout+"  "+payment_method+"  "+minimum);
                                dialogBill(id_ride,bill_id,cout,traveling,wait_price,subtotal,from_wallet,starting);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_ride", id_ride);
                    params.put("lat_depart", lat_depart);
                    params.put("lng_depart", lng_depart);
                    params.put("lat_dest", lat_dest);
                    params.put("lng_dest", lng_dest);
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
    public class setPayment extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"set_payment.php";
            final String id_ride = params[0];
            final String amount = params[1];
            final String total = params[2];
            final String extra = params[3];
            final String from_wallet = params[4];

            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                JSONObject json = new JSONObject(response);
                                Log.d("TAGBILL", "onResponse: "+response);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id_ride", id_ride);
                    params.put("amount", amount);
                    params.put("total", total);
                    params.put("extra", extra);
                    params.put("from_wallet", from_wallet);
                    params.put("user", M.getUserCategorie(context));
                    params.put("applied", isPromoApplied);
                    params.put("promo_type", promoType);
                    params.put("promo_value", promoValue);
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
    public void getPromoCode(ProgressBar progressBar_promo, TextView apply_promo_code, TextView cancel_promo_code,TextInputLayout input_layout_promo_code,EditText promo_code) {
        String postUrl = AppConst.Server_url+"get_promo_code.php";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, postUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject msg = json.getJSONObject("msg");
                    String etat = msg.getString("etat");
                    String message = msg.getString("message");
                    progressBar_promo.setVisibility(View.GONE);
                    apply_promo_code.setVisibility(View.VISIBLE);
                    if(etat.equals("1")){
                        String value = msg.getString("value");
                        String type = msg.getString("type");
                        promoType = type;
                        promoValue = value;
                        isPromoApplied = "true";

                        apply_promo_code.setText(R.string.applied);
                        cancel_promo_code.setVisibility(View.VISIBLE);
                        apply_promo_code.setEnabled(false);
                        input_layout_promo_code.setErrorEnabled(false);
                        promo_code.setEnabled(false);
                    }else {
                        input_layout_promo_code.setError(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", M.getID(context) );
                params.put("promo_code",promo_code.getText().toString().trim() );
                return params;
            }
        };
        requestQueue.add(request);
    }
    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void delete(int position){
        albumList.remove(position);
        notifyItemRemoved(position);
        return;
    }

    public RideModel getRequete(int id){
        RideModel rideModel = null;
        for (int i=0; i< albumList.size(); i++){
            if(albumList.get(i).getId() == id){
                rideModel = albumList.get(i);
                break;
            }
        }
        return rideModel;
    }

    public void restoreItem(RideModel rideModel, int position) {
        albumList.add(position, rideModel);
        notifyItemInserted(position);
    }

    public List<RideModel> getData() {
        return albumList;
    }
    private Location getLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        String provider;
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            currentLocation = locationManager.getLastKnownLocation(provider);
        }
        return currentLocation;
    }
}