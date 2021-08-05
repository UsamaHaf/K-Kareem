package canvasolutions.kreemcabs.drivers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.model.User;
import canvasolutions.kreemcabs.drivers.settings.AppConst;
import canvasolutions.kreemcabs.drivers.settings.PrefManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private int LOCATION_PERMISSION_CODE = 1;
    private TextView login,create_customer_account,forget_password,create_driver_account,title,forgot_pass;
    private PrefManager prefManager;
    public static AlertDialog alertDialog;
    private String val_phone, val_password;
    public static EditText input_phone,password;
    private CountryCodePicker country_code;
    private TextInputLayout input_layout_password,input_layout_phone;
    private static ProgressBar progressBar_login;
    private static String global_url = AppConst.Server_url;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;
        login = (TextView)findViewById(R.id.login);
//        create_customer_account = (TextView)findViewById(R.id.create_customer_account);
        create_driver_account = (TextView)findViewById(R.id.create_driver_account);
        forget_password = (TextView)findViewById(R.id.forget_password);
        input_phone = (EditText)findViewById(R.id.input_phone);
        country_code = (CountryCodePicker)findViewById(R.id.country_code);
        password = (EditText)findViewById(R.id.password);
        input_layout_phone = (TextInputLayout)findViewById(R.id.input_layout_phone);
        input_layout_password = (TextInputLayout)findViewById(R.id.input_layout_password);
        progressBar_login = (ProgressBar) findViewById(R.id.progressBar_login);
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(AppConst.font_quicksand_medium(context));

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch7()) {
            launchHomeScreen();
        }

        create_driver_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SubscribeActivity.class);
                intent.putExtra("account_type","driver");
                startActivity(intent);
            }
        });
//        create_customer_account.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, SubscribeActivity.class);
//                intent.putExtra("account_type","customer");
//                startActivity(intent);
//            }
//        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                if (ContextCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val_phone = country_code.getSelectedCountryCodeWithPlus() + input_phone.getText().toString().trim();
                    val_password = password.getText().toString();
                    submitFormLogin();
                } else {
                    requestStoragePermission();
                }


            }
        });
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswor.class));
            }
        });
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch7(false);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("fragment_name", "");
        startActivity(intent);
        finish();
    }

    /**
     * Validating form
     */
    private void submitFormLogin() {
        if (!validatePhoneLogin()) {
            return;
        }
        if (!validatePhoneValidLogin()) {
            return;
        }
        if (!validatePasswordLogin()) {
            return;
        }
        if (!validatePasswordValidLogin()) {
            return;
        }
        progressBar_login.setVisibility(View.VISIBLE);
        new loginUser().execute();
    }

    private boolean validatePhoneLogin() {
        if (input_phone.getText().toString().trim().isEmpty()) {
            input_layout_phone.setError(getResources().getString(R.string.enter_your_phone_number));
            requestFocus(input_phone);
            return false;
        } else {
            input_layout_phone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhoneValidLogin() {
        if (input_phone.getText().toString().trim().length() < 8) {
            input_layout_phone.setError(getResources().getString(R.string.enter_a_goog_phone_number));
            requestFocus(input_phone);
            return false;
        } else {
            input_layout_phone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordLogin() {
        if (password.getText().toString().trim().isEmpty()) {
            input_layout_password.setError(getResources().getString(R.string.enter_your_password));
            requestFocus(password);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordValidLogin() {
        if (password.getText().toString().trim().length() < 8) {
            input_layout_password.setError(getResources().getString(R.string.passwor_requires_at_least_characters));
            requestFocus(password);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch7()) {
            finish();
        }
    }

    /** Connexion d'un utilisateur **/
    private class loginUser extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = global_url+"conductuer_login.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBar_login.setVisibility(View.INVISIBLE);
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    JSONObject user = json.getJSONObject("user");
                                    Log.d("TAGCAT", "onResponse: "+user.getString("user_cat"));
                                    if(user.getString("user_cat").equals("conducteur")){
                                        if(user.getString("statut_vehicule").equals("no")){
                                            Intent intent = new Intent(LoginActivity.this, DriverVehicleActivity.class);
                                            intent.putExtra("id_driver",user.getString("id"));
                                            startActivity(intent);
                                        }else if(user.getString("photo").equals("")){
                                            Intent intent = new Intent(LoginActivity.this, ChoosePhotoActivity.class);
                                            intent.putExtra("id_driver",user.getString("id"));
                                            startActivity(intent);
                                        }else if(user.getString("statut_nic").equals("no")){
                                            Intent intent = new Intent(LoginActivity.this, ChooseNIActivity.class);
                                            intent.putExtra("id_driver",user.getString("id"));
                                            startActivity(intent);
                                        }else if(user.getString("statut_licence").equals("no")){
                                            Intent intent = new Intent(LoginActivity.this, ChooseLicenceActivity.class);
                                            intent.putExtra("id_driver",user.getString("id"));
                                            startActivity(intent);
                                        }else{
                                            saveProfile(new User(user.getString("id"),user.getString("nom"),user.getString("prenom"),user.getString("phone")
                                                    ,user.getString("email"),user.getString("statut"),user.getString("login_type"),user.getString("tonotify"),user.getString("device_id"),
                                                    user.getString("fcm_id"),user.getString("creer"),user.getString("modifier"),user.getString("photo_path"),user.getString("user_cat"),user.getString("online"),user.getString("currency")
                                                    ,user.getString("statut_licence"),user.getString("statut_nic"),user.getString("brand"),user.getString("model"),user.getString("color"),user.getString("numberplate"),user.getString("statut_vehicule"),user.getString("country")));

                                            input_phone.setText("");
                                            password.setText("");
                                            launchHomeScreen();
                                        }
                                    }else{
                                        saveProfile(new User(user.getString("id"),user.getString("nom"),user.getString("prenom"),user.getString("phone")
                                                ,user.getString("email"),user.getString("statut"),user.getString("login_type"),user.getString("tonotify"),user.getString("device_id"),
                                                user.getString("fcm_id"),user.getString("creer"),user.getString("modifier"),user.getString("photo_path"),
                                                user.getString("user_cat"),"",user.getString("currency")
                                                ,"","","","","","","",user.getString("country")));

                                        input_phone.setText("");
                                        password.setText("");
                                        launchHomeScreen();
                                    }
                                }else if(etat.equals("0")){
                                    Toast.makeText(context, context.getResources().getString(R.string.this_account_does_not_exist), Toast.LENGTH_SHORT).show();
                                    requestFocus(input_phone);
                                    input_layout_phone.setError(context.getResources().getString(R.string.enter_another_phone_number));
                                }else if(etat.equals("2")){
                                    Toast.makeText(context, context.getResources().getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show();
                                    requestFocus(password);
                                    input_layout_password.setError(context.getResources().getString(R.string.enter_another_password));
                                }else {
                                    requestFocus(input_phone);
                                    Toast.makeText(context, R.string.this_account_is_inactive, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {

                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar_login.setVisibility(View.INVISIBLE);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("phone", val_phone);
                    params.put("mdp", val_password);
                    params.put("login_type","2"); // type 2 means login with password
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

    private void saveProfile(User user){
        M.setNom(user.getNom(),context);
        M.setPrenom(user.getPrenom(),context);
        M.setPhone(user.getPhone(),context);
        M.setEmail(user.getEmail(),context);
        M.setID(user.getId(),context);
        M.setlogintype(user.getLogin_type(),context);
        M.setUsername(user.getNom(),context);
        M.setUserCategorie(user.getUser_cat(),context);
//        M.setCoutByKm(user.getCost(),context);
        M.setStatutConducteur(user.getStatut_online(),context);
        M.setCurrentFragment("",context);
        M.setCurrency(user.getCurrency(),context);
        M.setPhoto(user.getPhoto(),context);

        M.setVehicleBrand(user.getVehicle_brand(),context);
        M.setVehicleColor(user.getVehicle_color(),context);
        M.setVehicleModel(user.getVehicle_model(),context);
        M.setVehicleNumberPlate(user.getVehicle_numberplate(),context);
        M.setCountry(user.getCountry(),context);
        if(user.getTonotify().equals("yes"))
            M.setPushNotification(true, context);
        else
            M.setPushNotification(false, context);

        updateFCM(M.getID(context));
    }

    public void updateFCM(final String user_id) {
        final String[] fcmid = {""};
        final String[] deviceid = { "" };
        if(AppConst.fcm_id==null){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
//                                Toast.makeText(Demarrage.this, "getInstanceId failed", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            fcmid[0] = token;
                            // Log and toast
                            deviceid[0] = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                            if(fcmid[0] !=null && fcmid[0].trim().length()>0 && deviceid[0] !=null && deviceid[0].trim().length()>0) {
                                new setUserFCM().execute(user_id, fcmid[0], deviceid[0]);
                            }
                        }
                    });
        }else{
            fcmid[0] = AppConst.fcm_id;
        }
    }

    /** Mettre à jour le token de l'utilisateur**/
    private class setUserFCM extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"update_fcm.php";
            final String user_id = params[0];
            final String fcmid = params[1];
            final String deviceid = params[2];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id",user_id);
                    params.put("fcm_id",fcmid);
                    params.put("device_id",deviceid);
                    params.put("user_cat", M.getUserCategorie(context));
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            dialogPermission();
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.permission_needed)
//                    .setMessage(R.string.permission_dialoge_message)
//                    .setPositiveButton(getString(R.string.ok_small), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                           .
//                        }
//                    })
//                    .setNegativeButton(getString(R.string.cancel_small), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                val_phone = country_code.getSelectedCountryCodeWithPlus() + input_phone.getText().toString().trim();
                val_password = password.getText().toString();
                submitFormLogin();
            } else {
                Toast.makeText(this, getString(R.string.permission_message), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void dialogPermission() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(context);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_layout_why_permission, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        TextView Ok = (TextView) confirmDialog.findViewById(R.id.ypermission_Ok);
        TextView cancel = (TextView) confirmDialog.findViewById(R.id.ypermission_cancel);
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
