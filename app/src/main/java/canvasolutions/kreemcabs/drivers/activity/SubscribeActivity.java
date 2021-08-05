package canvasolutions.kreemcabs.drivers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SubscribeActivity extends AppCompatActivity {
    private TextView send, i_have_account;
    public TextView verify_phone, verify_email, change_phone, change_email;
    private String val_firstname_subs, val_lastname_subs, val_cnic_subs, val_email_subs,
            val_phone_subs, val_password_subs, val_password_conf_subs, account_type;
    private static EditText phone_subs, password_subs, password_conf, firstname_subs,
            lastname_subs, email_insc, cnic_subs;
    private CountryCodePicker country_code_subs;
    private Context context;
    private static ProgressBar progressBar_subs;
    private TextInputLayout input_layout_firstname_subs, input_layout_lastname_subs,
            input_layout_phone_subs, input_layout_password_inc, input_layout_dni_subs,
            input_layout_password_conf, input_layout_email_insc;
    private static String global_url = AppConst.Server_url;
    private TextView title;
    private String id_driver = "";
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        context = SubscribeActivity.this;


        queue = Volley.newRequestQueue(this);


        Bundle objetbundle = this.getIntent().getExtras();
        account_type = objetbundle.getString("account_type");
        val_phone_subs = objetbundle.getString("phone");

        send = (TextView) findViewById(R.id.send);
        verify_phone = (TextView) findViewById(R.id.verify_phone);
        change_email = (TextView) findViewById(R.id.change_email);
        change_phone = (TextView) findViewById(R.id.change_phone);
        verify_email = (TextView) findViewById(R.id.verify_email);
        i_have_account = (TextView) findViewById(R.id.i_have_account);
        phone_subs = (EditText) findViewById(R.id.phone_subs);
        country_code_subs = (CountryCodePicker) findViewById(R.id.country_code_subs);
        password_subs = (EditText) findViewById(R.id.password_subs);
        password_conf = (EditText) findViewById(R.id.password_conf);
        firstname_subs = (EditText) findViewById(R.id.firstname_subs);
        lastname_subs = (EditText) findViewById(R.id.lastname_subs);
        cnic_subs = (EditText) findViewById(R.id.dni_insc);
        email_insc = (EditText) findViewById(R.id.email_insc);
        progressBar_subs = (ProgressBar) findViewById(R.id.progressBar_subs);
        input_layout_firstname_subs =
                (TextInputLayout) findViewById(R.id.input_layout_firstname_subs);
        input_layout_dni_subs = (TextInputLayout) findViewById(R.id.input_layout_dni_inc);
        input_layout_lastname_subs =
                (TextInputLayout) findViewById(R.id.input_layout_lastname_subs);
        input_layout_phone_subs = (TextInputLayout) findViewById(R.id.input_layout_phone_subs);
        input_layout_password_inc = (TextInputLayout) findViewById(R.id.input_layout_password_inc);
        input_layout_password_conf =
                (TextInputLayout) findViewById(R.id.input_layout_password_conf);
        input_layout_email_insc = (TextInputLayout) findViewById(R.id.input_layout_email_insc);
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(AppConst.font_quicksand_medium(context));

        M.setPhoneVerified(this, false);
        M.setEmailVerified(this, false);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val_firstname_subs = firstname_subs.getText().toString();
                val_email_subs = email_insc.getText().toString();
                val_cnic_subs = cnic_subs.getText().toString();

                val_password_subs = password_subs.getText().toString();
                val_password_conf_subs = password_conf.getText().toString();
                submitFormSubscribe();
            }
        });
        i_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        verify_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                country_code_subs.registerCarrierNumberEditText(phone_subs);
                val_phone_subs = country_code_subs.getFullNumberWithPlus().replace("", "").trim();

                if (!phone_subs.getText().toString().trim().isEmpty()) {
                    if (verify_phone.getText().toString().equals(getString(R.string.verify_phone))) {
                        Log.d("TAGPHONE", "onClick: " + val_phone_subs);
                        startActivity(new Intent(SubscribeActivity.this, PhoneVerification.class).putExtra("phone", val_phone_subs));
                    } else {
                        verify_phone.setClickable(false);
                    }
                } else {
                    input_layout_phone_subs.setError(getResources().getString(R.string.enter_your_phone_number));
                    requestFocus(phone_subs);
                }
            }
        });
        verify_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email_insc.getText().toString().trim().isEmpty() || !email_insc.getText().toString().trim().contains("@") || !email_insc.getText().toString().trim().contains(".") || email_insc.getText().toString().trim().length() < 5) {
                    input_layout_email_insc.setError(getResources().getString(R.string.please_enter_valid_email_address));
                    requestFocus(email_insc);
                } else {
                    val_email_subs = email_insc.getText().toString();
                    if (verify_email.getText().toString().equals(getString(R.string.verify_email))) {
                        Log.d("TAGPHONE", "onClick: " + val_email_subs);
                        startActivity(new Intent(SubscribeActivity.this, PhoneVerification.class).putExtra("email", val_email_subs));
                    } else {
                        email_insc.setClickable(false);
                    }
                }
            }
        });
        change_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                M.setPhoneVerified(SubscribeActivity.this, false);
                verify_phone.setText(R.string.verify_phone);
                country_code_subs.setEnabled(true);
                country_code_subs.setCcpClickable(true);
                phone_subs.setEnabled(true);
                verify_phone.setEnabled(true);
                phone_subs.requestFocus();
            }
        });
        change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                M.setEmailVerified(SubscribeActivity.this, false);
                verify_email.setText(R.string.verify_email);
                verify_email.setEnabled(true);
                email_insc.setEnabled(true);
            }
        });
    }

    private void launchHomeScreen() {
        Intent intent;
        if (account_type.equals("customer")) {
            intent = new Intent(SubscribeActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SubscribeActivity.this, DriverVehicleActivity.class);
            intent.putExtra("id_driver", id_driver);
        }
        intent.putExtra("fragment_name", "");
        startActivity(intent);
        finish();
    }

    /**
     * Validating form
     *//*
    private void submitFormSubscribeCustomer() {
        if (!validatefirstname()) {
            return;
        }
        if (!validatelastname()) {
            return;
        }
        if (!validatePhone()) {
            return;
        }
        if (!validateDni()) {
            return;
        }
        if (!validatePhoneValid()) {
            return;
        }
        if (!validateEmailValid()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!validatePasswordValid()) {
            return;
        }
        if (!validatepasswordConf()) {
            return;
        }
        if (val_password_conf_subs.equals(val_password_subs)) {
            progressBar_subs.setVisibility(View.VISIBLE);
            new createUser().execute();
        } else {
            Toast.makeText(context, getResources().getString(R.string.password_requires),
                    Toast.LENGTH_SHORT).show();
        }
    }*/

    private void submitFormSubscribe() {
        Log.d("TAGCLIEKD", "onClick: CLICKED");
        if (!validatefirstname()) {
            return;
        }
        if (!email_insc.getText().toString().isEmpty()) {
            if (!validateEmailValid()) {
                return;
            }
        }
        progressBar_subs.setVisibility(View.VISIBLE);
        new createUser().execute();

    }

    private boolean validatefirstname() {
        if (firstname_subs.getText().toString().trim().isEmpty()) {
            input_layout_firstname_subs.setError(getResources().getString(R.string.name_surname));
            requestFocus(firstname_subs);
            return false;
        } else {
            input_layout_firstname_subs.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatelastname() {
        if (lastname_subs.getText().toString().trim().isEmpty()) {
            input_layout_lastname_subs.setError(getResources().getString(R.string.enter_your_last_name));
            requestFocus(lastname_subs);
            return false;
        } else {
            input_layout_lastname_subs.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDni() {
        String dni = cnic_subs.getText().toString().trim();
        if (cnic_subs.getText().toString().trim().isEmpty()) {
            input_layout_dni_subs.setError(getResources().getString(R.string.enter_your_dni));
            requestFocus(cnic_subs);
            return false;
        } else if (dni.contains(".") || dni.contains(",") || dni.contains("~") || dni.contains(
                "#") || dni.contains("^") || dni.contains("|") || dni.contains("$") || dni.contains("%") || dni.contains("&") || dni.contains("!") || dni.contains("*") || dni.contains("?")) {
            input_layout_dni_subs.setError(getResources().getString(R.string.can_not_use_special_character));
            requestFocus(cnic_subs);
            return false;
        } else {
            input_layout_dni_subs.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        if (phone_subs.getText().toString().trim().isEmpty()) {
            input_layout_phone_subs.setError(getResources().getString(R.string.enter_your_phone_number));
            requestFocus(phone_subs);
            return false;
        } else if (verify_phone.getText().equals(getString(R.string.verify_phone))) {
            input_layout_phone_subs.setError(getResources().getString(R.string.verify_your_phone));
            requestFocus(phone_subs);
            return false;
        } else {
            input_layout_phone_subs.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validatePhoneValid() {
        if (phone_subs.getText().toString().trim().length() < 8) {
            input_layout_phone_subs.setError(getResources().getString(R.string.enter_a_good_phone_number));
            requestFocus(phone_subs);
            return false;
        } else {
            input_layout_phone_subs.setErrorEnabled(false);
        }

        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        if (!TextUtils.isEmpty(target)) {
            Patterns.EMAIL_ADDRESS.matcher(target).matches();
            return true;
        }
        return false;
    }

    private boolean validateEmailValid() {

        if (email_insc.getText().toString().trim().isEmpty()) {
            input_layout_email_insc.setError(getResources().getString(R.string.enter_your_email_address));
            requestFocus(email_insc);
            return false;
        }
        if (!email_insc.getText().toString().trim().contains("@")) {
            input_layout_email_insc.setError(getResources().getString(R.string.please_enter_valid_email_address));
            requestFocus(email_insc);
            return false;
        }
        if (!email_insc.getText().toString().trim().contains(".")) {
            input_layout_email_insc.setError(getResources().getString(R.string.please_enter_valid_email_address));
            requestFocus(email_insc);
            return false;
        }
        if (email_insc.getText().toString().trim().length() < 5) {
            input_layout_email_insc.setError(getResources().getString(R.string.please_enter_valid_email_address));
            requestFocus(email_insc);
            return false;
        } else if (verify_email.getText().equals(getString(R.string.verify_email))) {
            input_layout_email_insc.setError(getResources().getString(R.string.verify_your_email));
            requestFocus(email_insc);
            return false;
        } else {
            input_layout_email_insc.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (password_subs.getText().toString().trim().isEmpty()) {
            input_layout_password_inc.setError(getResources().getString(R.string.enter_your_password));
            requestFocus(password_subs);
            return false;
        } else {
            input_layout_password_inc.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordValid() {
        if (password_subs.getText().toString().trim().length() < 8) {
            input_layout_password_inc.setError(getResources().getString(R.string.passwor_requires_at_least_characters));
            requestFocus(password_subs);
            return false;
        } else {
            input_layout_password_inc.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatepasswordConf() {
        if (password_conf.getText().toString().trim().isEmpty()) {
            input_layout_password_conf.setError(getResources().getString(R.string.confirm_password));
            requestFocus(password_conf);
            return false;
        } else {
            input_layout_password_conf.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * User registration
     **/
    private class createUser extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = global_url + "user_register.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        progressBar_subs.setVisibility(View.INVISIBLE);
                        JSONObject json = new JSONObject(response);
                        JSONObject msg = json.getJSONObject("msg");
                        String etat = msg.getString("etat");
                        if (etat.equals("1")) {
                            JSONObject user = json.getJSONObject("user");

                            phone_subs.setText("");
                            password_subs.setText("");
                            password_conf.setText("");
                            firstname_subs.setText("");
                            lastname_subs.setText("");
                            email_insc.setText("");
                            cnic_subs.setText("");
//                                    Toast.makeText(context, "Successfully completed", Toast

                            if (account_type.equals("customer")) {
                                saveProfile(new User(user.getString("id"), user.getString("nom"),
                                        user.getString("prenom"), user.getString("phone"),
                                        user.getString("email"), user.getString("statut"),
                                        user.getString("login_type"), user.getString("tonotify"),
                                        user.getString("device_id"), user.getString("fcm_id"),
                                        user.getString("creer"), user.getString("modifier"),
                                        user.getString("photo_path"), user.getString("user_cat"),
                                        "", user.getString("currency"), "", "", "", "", "", "",
                                        "", user.getString("country")));
                            } else {
                                id_driver = user.getString("id");
                                saveProfile(new User(user.getString("id"), user.getString("nom"),
                                        user.getString("prenom"), user.getString("phone"),
                                        user.getString("email"), user.getString("statut"),
                                        user.getString("login_type"), user.getString("tonotify"),
                                        user.getString("device_id"), user.getString("fcm_id"),
                                        user.getString("creer"), user.getString("modifier"),
                                        user.getString("photo_path"), user.getString("user_cat"),
                                        "", user.getString("currency"), user.getString(
                                                "statut_licence"), user.getString("statut_nic"),
                                        "", "", "", "", user.getString("statut_vehicule"),
                                        user.getString("country")));
                            }

                            launchHomeScreen();
                        } else if (etat.equals("2")) {
//                                    Toast.makeText(context, R.string
//                                    .phone_or_email_already_exist, Toast.LENGTH_SHORT).show();
                            requestFocus(phone_subs);
                            input_layout_phone_subs.setError(getString(R.string.phone_or_email_already_exist));
                            input_layout_email_insc.setError(getString(R.string.phone_or_email_already_exist));
                        } else
                            Toast.makeText(context, R.string.registration_failed,
                                    Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar_subs.setVisibility(View.INVISIBLE);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("firstname", val_firstname_subs);
                    params.put("phone", val_phone_subs + ",#, " + val_cnic_subs);
                    params.put("password", val_password_subs + " ");
                    params.put("email", val_email_subs);
                    params.put("account_type", account_type);
                    params.put("login_type", "phone");
                    params.put("tonotify", "yes");

                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000,
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

    private void saveProfile(User user) {
        M.setNom(user.getNom(), context);
        M.setPrenom(user.getPrenom(), context);
        M.setPhone(user.getPhone(), context);
        M.setEmail(user.getEmail(), context);
        M.setID(user.getId(), context);
        M.setlogintype(user.getLogin_type(), context);
        M.setUsername(user.getNom(), context);
        M.setUserCategorie(user.getUser_cat(), context);
//        M.setCoutByKm(user.getCost(),context);
        M.setCurrentFragment("", context);
        M.setCurrency(user.getCurrency(), context);
        if (!user.getUser_cat().equals("user_app"))
            M.setStatutConducteur(user.getStatut_online(), context);

        M.setVehicleBrand(user.getVehicle_brand(), context);
        M.setVehicleColor(user.getVehicle_color(), context);
        M.setVehicleModel(user.getVehicle_model(), context);
        M.setVehicleNumberPlate(user.getVehicle_numberplate(), context);
        M.setCountry(user.getCountry(), context);
        if (user.getTonotify().equals("yes")) M.setPushNotification(true, context);
        else M.setPushNotification(false, context);

        updateFCM(M.getID(context));
    }

    public void updateFCM(final String user_id) {
        final String[] fcmid = {""};
        final String[] deviceid = {""};
        if (AppConst.fcm_id == null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult().getToken();
                    fcmid[0] = token;
                    deviceid[0] = Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    if (fcmid[0] != null && fcmid[0].trim().length() > 0 && deviceid[0] != null && deviceid[0].trim().length() > 0) {
                        new setUserFCM().execute(user_id, fcmid[0], deviceid[0]);
                    }
                }
            });
        } else {
            fcmid[0] = AppConst.fcm_id;
        }
    }

    /**
     * Update the user's token
     **/
    private class setUserFCM extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url + "update_fcm.php";
            final String user_id = params[0];
            final String fcmid = params[1];
            final String deviceid = params[2];
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
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
                    params.put("user_id", user_id);
                    params.put("fcm_id", fcmid);
                    params.put("device_id", deviceid);
                    return params;
                }
            };
            queue.add(jsonObjReq);
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000,
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
    protected void onResume() {
        if (M.getPhoneVerified(SubscribeActivity.this).equals("true")) {
            verify_phone.setText(R.string.phone_verified);
            country_code_subs.setEnabled(false);
            country_code_subs.setCcpClickable(false);
            phone_subs.setEnabled(false);
            verify_phone.setEnabled(false);
            email_insc.requestFocus();
            input_layout_phone_subs.setErrorEnabled(false);
        }
        if (M.getEmailVerified(SubscribeActivity.this).equals("true")) {
            verify_email.setText(R.string.email_verified);
            verify_email.setEnabled(false);
            email_insc.setEnabled(false);
            input_layout_email_insc.setErrorEnabled(false);
        }

        super.onResume();
    }
}
