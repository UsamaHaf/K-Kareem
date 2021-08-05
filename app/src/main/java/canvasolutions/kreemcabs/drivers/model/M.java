package canvasolutions.kreemcabs.drivers.model;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import canvasolutions.kreemcabs.drivers.R;


public class M {
    public static ProgressDialog pDialog;
    private static SharedPreferences mSharedPreferences;
    private static String pref_name="settings_odv_driver";
    public final static String RIDETYPE_KEY = "ride_type";
    public final static String SPEED1_KEY = "speed1_key";
    public final static String SPEED2_KEY = "speed2_key";
    public final static String SPEED3_KEY = "speed3_key";
    public final static String SPEED4_KEY = "speed4_key";
    public final static String WAITING_TIME_KEY = "waiting_time_key";
    public final static String CANCEL_RIDE_ID_KEY = "cancel_ride_id_key";// cancel ride id if customer not arrived on a pickup point
    public final static String CANCEL_RIDE_TS_KEY = "cancel_ride_ts_key";//cancel ride time // //  //  //  //  //  //
    public final static String DRIVER_STATUS_KEY = "driver_status_key";
    public final static String SHOULD_REJECT_VISIBLE = "should_reject_visible";
    public final static String SHOULD_SERVICE_STOP = "should_service_stop";
    public final static String IS_SIGN_OUT = "is_sign_out";

    public static void showLoadingDialog(Context mContext) {

        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getString(R.string.please_wait));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        if (!pDialog.isShowing())
            pDialog.show();
    }
    public static void hideLoadingDialog() {
        if(pDialog != null){
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    public static void showToast(Context mContext, String message) {
        M.showToast(mContext, message);
    }

    public static void T(Context mContext, String Message) {
        M.showToast(mContext, Message);
    }

    public static void L(String Message) {
        Log.e("Vidoo", Message);
    }

    public static boolean setUsername(String username, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("username", username);
        return editor.commit();
    }

    public static String getUsername(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("username", null);
    }

    public static boolean setNom(String nom, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("nom", nom);
        return editor.commit();
    }

    public static String getNom(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("nom", null);
    }

    public static boolean setPrenom(String prenom, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("prenom", prenom);
        return editor.commit();
    }

    public static String getPrenom(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("prenom", null);
    }

    public static boolean setRouteDistance(String prenom, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("routedistance", prenom);
        return editor.commit();
    }

    public static String getRouteDistance(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("routedistance", null);
    }

    public static boolean setRouteDuration(String prenom, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("routeduration", prenom);
        return editor.commit();
    }

    public static String getRouteDuration(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("routeduration", null);
    }

    public static String getCurrentFragment(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("current_fragment", null);
    }

    public static boolean setCurrentFragment(String prenom, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("current_fragment", prenom);
        return editor.commit();
    }

    public static boolean setStatutConducteur(String online, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("statut_conducteur", online);
        return editor.commit();
    }

    public static String getStatutConducteur(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("statut_conducteur", null);
    }

    public static boolean setCountry(String country, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("country", country);
        return editor.commit();
    }

    public static String getCountry(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("country", null);
    }


//    public static boolean setEmail(String email, Context mContext) {
//        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putString("email", email);
//        return editor.commit();
//    }

    public static String getEmail(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("email", null);
    }

    public static boolean setEmail(String email, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("email", email);
        return editor.commit();
    }

    public static String getUserCategorie(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("user_cat", null);
    }

    public static boolean setUserCategorie(String user_cat, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("user_cat", user_cat);
        return editor.commit();
    }

    public static String getCoutByKm(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("cout_km", null);
    }

    public static boolean setCoutByKm(String cout_km, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("cout_km", cout_km);
        return editor.commit();
    }

    public static boolean setPhone(String phone, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("phone", phone);
        return editor.commit();
    }

    public static String getPhone(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("phone", null);
    }

    public static boolean setCurrency(String money, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("money", money);
        return editor.commit();
    }

    public static String getCurrency(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("money", null);
    }

    public static boolean setVehicleBrand(String brand, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("brand", brand);
        return editor.commit();
    }

    public static String getBrand(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("brand", null);
    }

    public static boolean setType(String type, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("type", type);
        return editor.commit();
    }

    public static String getType(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("type", null);
    }

    public static boolean setVehicleColor(String color, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("color", color);
        return editor.commit();
    }

    public static String getColor(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("color", null);
    }

    public static boolean setVehicleModel(String licence, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("model", licence);
        return editor.commit();
    }

    public static String getVehicleModel(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("model", null);
    }

    public static boolean setVehicleNumberPlate(String registration, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("numberplate", registration);
        return editor.commit();
    }

    public static String getVehicleNumberPlate(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("numberplate", null);
    }

    public static String getInsurance(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("insurance", null);
    }

    public static String getTaxiType(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("taxi_type", null);
    }

    public static boolean setTaxiType(String taxi_type, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("taxi_type", taxi_type);
        return editor.commit();
    }

    public static String getTaxiTypeImage(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("taxi_type_image", null);
    }

    public static boolean setTaxiTypeImage(String taxi_type_image, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("taxi_type_image", taxi_type_image);
        return editor.commit();
    }

    public static boolean setPhoto(String photo, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("photo", photo);
        return editor.commit();
    }

    public static String getLicence(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("licence", "");
    }

    public static boolean setLicence(String licence, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("licence", licence);
        return editor.commit();
    }

    public static String getNic(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("nic", "");
    }

    public static boolean setNic(String nic, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("nic", nic);
        return editor.commit();
    }

    public static String getPhoto(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("photo", "");
    }

    public static boolean setlogintype(String logintype, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("logintype", logintype);
        return editor.commit();
    }

    public static String getlogintype(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("logintype", null);
    }

    public static boolean setPushNotification(Boolean pn, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putBoolean("pushnotify", pn);
        return editor.commit();
    }

    public static Boolean isPushNotify(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getBoolean("pushnotify", true);
    }

    public static void logOut(Context mContext) {
        setID(null,mContext);
        setNom(null, mContext);
        setPrenom(null,mContext);
        setPhone(null,mContext);
        setEmail(null,mContext);
//        setUserCategorie(null,mContext);
        setCoutByKm(null,mContext);
        setPhoto(null,mContext);
//        setPhoto(null,mContext);
        setlogintype(null,mContext);
        setUsername(null,mContext);
        setPushNotification(true,mContext);
        M.setUserCategorie(null,mContext);
        M.setCurrentFragment(null,mContext);
        M.setCurrency(null,mContext);
        M.setStatutConducteur(null,mContext);
        M.setVehicleBrand(null,mContext);
        M.setVehicleColor(null,mContext);
        M.setVehicleModel(null,mContext);
        M.setVehicleNumberPlate(null,mContext);
        mSharedPreferences.getAll().clear();
    }

    public static boolean setID(String ID, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString("userid", ID);
        return editor.commit();
    }

    public static String getID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("userid", "");
    }



    public static boolean setNumber_new(String nb_new, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("nb_new", nb_new);
        return editor.commit();
    }
    public static String getNumber_new(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("nb_new", null);
    }
    public static boolean setNumber_confirmed(String nb_confirmed, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("nb_confirmed", nb_confirmed);
        return editor.commit();
    }

    public static String getNumber_confirmed(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("nb_confirmed", null);
    }
    public static boolean setNumber_onride(String nb_onride, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("nb_onride", nb_onride);
        return editor.commit();
    }

    public static String getNumber_onride(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("nb_onride", null);
    }
    public static boolean setNumber_completed(String nb_completed, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("nb_completed", nb_completed);
        return editor.commit();
    }

    public static String getNumber_completed(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("nb_completed", null);
    }
    public static boolean setNumber_sales(String nb_sales, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("nb_sales", nb_sales);
        return editor.commit();
    }

    public static String getNumber_sales(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("nb_sales", null);
    }
    public static boolean setRideConfirmed(Context mContext,String rides) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("ridesConfirmed", rides);
        return editor.commit();
    }

    public static String getRideConfirmed(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("ridesConfirmed", "");
    }
    public static boolean setRideConfirmedBooking(Context mContext,String rides) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("setRideConfirmedBooking", rides);
        return editor.commit();
    }

    public static String getRideConfirmedBooking(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("setRideConfirmedBooking", "");
    }
    public static boolean setRideNewBooking(Context mContext,String rides) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("setRideNewBooking", rides);
        return editor.commit();
    }

    public static String getRideNewBooking(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("setRideNewBooking", "");
    }
    public static boolean setDriverCT(Context mContext,String time) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("setDriverCT", time);
        return editor.commit();
    }

    public static String getDriverCT(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("setDriverCT", "");
    }
    public static boolean setTimeStart(Context mContext,String timeStart) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("setTimeStart", timeStart);
        return editor.commit();
    }

    public static String getTimeStart(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("setTimeStart", "");
    }
    public static boolean setCancelConfirmedRideID(Context mContext,String rideId) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("setCancelConfirmedRideID", rideId);
        return editor.commit();
    }

    public static String getCancelConfirmedRideID(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("setCancelConfirmedRideID", "");
    }
    public static boolean setTimeStartConfirmedCancel(Context mContext,String timeStart) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("setTimeStartConfirmedCancel", timeStart);
        return editor.commit();
    }

    public static String getTimeStartConfirmedCancel(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("setTimeStartConfirmedCancel", "");
    }
    public static boolean setPhoneVerified(Context mContext,boolean isVerified) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("setPhoneVerified", String.valueOf(isVerified));
        return editor.commit();
    }

    public static String getPhoneVerified(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("setPhoneVerified", "");
    }
    public static boolean setEmailVerified(Context mContext,boolean isVerified) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("setEmailVerified", String.valueOf(isVerified));
        return editor.commit();
    }

    public static String getEmailVerified(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString("setEmailVerified", "");
    }
    public static boolean setRideType(Context mContext,String type,String key) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, type);
        return editor.commit();
    }

    public static String getRideType(Context mContext,String key) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString(key, "");
    }
    public static boolean setValue(Context mContext, String value, String key) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getValue(Context mContext, String key) {
        mSharedPreferences = mContext.getSharedPreferences(pref_name, 0);
        return mSharedPreferences.getString(key, "");
    }
}
