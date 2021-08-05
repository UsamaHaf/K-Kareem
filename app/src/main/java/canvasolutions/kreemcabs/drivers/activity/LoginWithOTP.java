package canvasolutions.kreemcabs.drivers.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.databinding.ActivityLoginWithOtpBinding;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.model.User;
import canvasolutions.kreemcabs.drivers.settings.AppConst;
import canvasolutions.kreemcabs.drivers.settings.PrefManager;

public class LoginWithOTP extends AppCompatActivity {
    ActivityLoginWithOtpBinding binding;

    private int LOCATION_PERMISSION_CODE = 1;
    String phone;
    FirebaseAuth mAuth;
    private PrefManager prefManager;
    public static AlertDialog alertDialog;
    String verificationId = "";
    boolean isCodeSent = false;
    Context context;
    CountDownTimer countDownTimer;
    private static String global_url = AppConst.Server_url;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    ProgressBar progressBarDialog;
    TextView responseMessage,resendDialog,timerDialog;
    int sendResend = 0,cancelDialog = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_with_otp);
        context = LoginWithOTP.this;
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch7()) {
            launchHomeScreen();
        }
        mAuth = FirebaseAuth.getInstance();
        binding.title.setTypeface(AppConst.font_quicksand_medium(context));
        binding.sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    binding.countryCode.registerCarrierNumberEditText(binding.inputPhone);
                    phone = binding.countryCode.getFullNumberWithPlus().replace("", "").trim();
                    if (!binding.inputPhone.getText().toString().trim().isEmpty()) {
                        sendVerificationCode(phone);
                        binding.progressBarVerify.setVisibility(View.VISIBLE);
                        binding.sendOtp.setVisibility(View.GONE);
                        sendResend = 1;
                    }else {
                        binding.inputLayoutPhone.setError(getResources().getString(R.string.enter_your_phone_number));
                        requestFocus(binding.inputPhone);
                    }
                } else {
                    requestStoragePermission();
                }


            }
        });
        binding.resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.countryCode.registerCarrierNumberEditText(binding.inputPhone);
                phone = binding.countryCode.getFullNumberWithPlus().replace("", "").trim();
                if (!binding.inputPhone.getText().toString().trim().isEmpty()) {
                    resendVerificationCode(phone,resendingToken);
                    binding.progressBarVerify.setVisibility(View.VISIBLE);
                    binding.resendCode.setVisibility(View.GONE);
                    binding.inputPhone.setEnabled(false);
                    binding.countryCode.setCcpClickable(false);
                    sendResend = 2;
                }else {
                    binding.inputLayoutPhone.setError(getResources().getString(R.string.enter_your_phone_number));
                    requestFocus(binding.inputPhone);
                }
            }
        });
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private void sendVerificationCode(String number) {
        Log.d("TAGMESSAGE", "sendVerificationCode: "+number);
        binding.progressBarVerify.setVisibility(View.VISIBLE);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder (mAuth)
                . setPhoneNumber (number) // Phone number to verify
                . setTimeout ( 2L, TimeUnit.MINUTES ) // Timeout and unit
                . setActivity (LoginWithOTP.this) // Activity (for callback binding)
                . setCallbacks (mCallback) // OnVerificationStateChangedCallbacks
                . build ();
        Log.d("TAGMESSAGE", "sendVerificationCode: 2 "+options);
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, R.string.phone_verified, Toast.LENGTH_SHORT).show();
                            new loginUser().execute();
                        } else {
                            Toast.makeText(context,"You entered incorrect OTP code!", Toast.LENGTH_SHORT).show();
                            M.hideLoadingDialog();
//                            if (task.getException().getMessage().contains("not exist")){
//                                startActivity(new Intent(context,SubscribeActivity.class));
//                            }
//                            Log.d("TAGMSG", "onComplete: "+task.getException().getMessage());
//                            binding.progressBarVerify.setVisibility(View.GONE);
//                            binding.timer.setVisibility(View.VISIBLE);
//                            Toast.makeText(LoginWithOTP.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                40,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallback,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d("TAGMESSAGE", "onVerificationCompleted: "+phoneAuthCredential);
            M.showLoadingDialog(context);
            signInWithCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d("TAGMESSAGE", "onVerificationFailed: "+e.getMessage());
            if(e.getMessage().contains("blocked all requests")){
                Toast.makeText(context, "We have blocked all requests from this device due to unusual activity. Try again later.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, R.string.something_went_wrong_plz_try_again_after_some_time, Toast.LENGTH_SHORT).show();
            }
            binding.progressBarVerify.setVisibility(View.GONE);
            binding.sendOtp.setVisibility(View.VISIBLE);
            binding.inputPhone.setEnabled(true);
            binding.countryCode.setCcpClickable(true);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
            Log.d("TAGMESSAGE", "onCodeSent: "+s);
            verificationId = s;
            resendingToken = forceResendingToken;
            isCodeSent = true;
            if (sendResend == 1){
                dialogEnterOTP();
            }else if (sendResend == 2){
                progressBarDialog.setVisibility(View.GONE);
            }
            responseMessage.setText(getString(R.string.verification_code_sent)+phone);
            countDown(timerDialog,resendDialog);


        }
    };

    private void countDown(TextView timerTextView,TextView resend){
        Log.d("TAGMESSAGE", "countDown: "+timerTextView);
        countDownTimer = new CountDownTimer(40*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setVisibility(View.VISIBLE);
                timerTextView.setText(getResources().getString(R.string.resend_code)+" in "+millisUntilFinished/1000);
                binding.sendOtp.setVisibility(View.VISIBLE);
                binding.progressBarVerify.setVisibility(View.GONE);
                cancelDialog = 1;

            }

            @Override
            public void onFinish() {
                timerTextView.setVisibility(View.GONE);
                resend.setVisibility(View.VISIBLE);
                cancelDialog = 2;
            }
        }.start();
    }

    private void dialogEnterOTP() {
        LayoutInflater li = LayoutInflater.from(context);
        View otpDialog = li.inflate(R.layout.dialog_layout_enter_otp, null);
        TextView submit = otpDialog.findViewById(R.id.submit_otp);
        TextView cancel = otpDialog.findViewById(R.id.cancel_otp_login);
        timerDialog = otpDialog.findViewById(R.id.timer);
        resendDialog= otpDialog.findViewById(R.id.resend_code);
        responseMessage = otpDialog.findViewById(R.id.response_message_show);
        progressBarDialog = otpDialog.findViewById(R.id.progressBar_verify);
        EditText otp = otpDialog.findViewById(R.id.otp_entered);
        TextInputLayout layout_otp = otpDialog.findViewById(R.id.input_layout_otp);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(otpDialog);
        alert.setCancelable(false);
        //Creating an alert dialog
        alertDialog = alert.create();
        alertDialog.setCancelable(false);

        //Displaying the alert dialog
        alertDialog.show();
//        countDown(timer,resend);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!otp.getText().toString().trim().isEmpty()){
                    M.showLoadingDialog(context);
                    verifyCode(otp.getText().toString().trim());

//                    alertDialog.dismiss();
                }else {
                    layout_otp.setError(getResources().getString(R.string.enter_otp));
                    requestFocus(otp);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                restartActivity();
                if (cancelDialog != 1){
                    binding.inputPhone.setEnabled(true);
                    binding.inputPhone.setText("");
                    binding.sendOtp.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();
                }else Toast.makeText(context, R.string.please_wait_until_completed, Toast.LENGTH_SHORT).show();

            }
        });
        resendDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseMessage.setText("");
                progressBarDialog.setVisibility(View.VISIBLE);
                resendDialog.setVisibility(View.GONE);
                resendVerificationCode(phone,resendingToken);
//                countDown(timer,resend);
                sendResend = 2;
            }
        });
    }
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
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                M.hideLoadingDialog();
                                alertDialog.dismiss();
                                if(etat.equals("1")){
                                    JSONObject user = json.getJSONObject("user");
                                    Log.d("TAGCAT", "onResponse: "+user);
                                    if(user.getString("user_cat").equals("conducteur")){
                                        if(user.getString("statut_vehicule").equals("no")){
                                            Intent intent = new Intent(context, DriverVehicleActivity.class);
                                            intent.putExtra("id_driver",user.getString("id"));
                                            startActivity(intent);
                                        }else if(user.getString("photo").equals("")){
                                            Intent intent = new Intent(context, ChoosePhotoActivity.class);
                                            intent.putExtra("id_driver",user.getString("id"));
                                            startActivity(intent);
                                        }else if(user.getString("statut_nic").equals("no")){
                                            Intent intent = new Intent(context, ChooseNIActivity.class);
                                            intent.putExtra("id_driver",user.getString("id"));
                                            startActivity(intent);
                                        }else if(user.getString("statut_licence").equals("no")){
                                            Intent intent = new Intent(context, ChooseLicenceActivity.class);
                                            intent.putExtra("id_driver",user.getString("id"));
                                            startActivity(intent);
                                        }else{
                                            saveProfile(new User(user.getString("id"),user.getString("nom"),user.getString("prenom"),user.getString("phone")
                                                    ,user.getString("email"),user.getString("statut"),user.getString("login_type"),user.getString("tonotify"),user.getString("device_id"),
                                                    user.getString("fcm_id"),user.getString("creer"),user.getString("modifier"),user.getString("photo_path"),user.getString("user_cat"),user.getString("online"),user.getString("currency")
                                                    ,user.getString("statut_licence"),user.getString("statut_nic"),user.getString("brand"),user.getString("model"),user.getString("color"),user.getString("numberplate"),user.getString("statut_vehicule"),user.getString("country")));

                                            launchHomeScreen();
                                        }
                                    }else{
                                        saveProfile(new User(user.getString("id"),user.getString("nom"),user.getString("prenom"),user.getString("phone")
                                                ,user.getString("email"),user.getString("statut"),user.getString("login_type"),user.getString("tonotify"),user.getString("device_id"),
                                                user.getString("fcm_id"),user.getString("creer"),user.getString("modifier"),user.getString("photo_path"),
                                                user.getString("user_cat"),"",user.getString("currency")
                                                ,"","","","","","","",user.getString("country")));

                                        launchHomeScreen();
                                    }
                                }else if(etat.equals("0")){
                                    Intent intent = new Intent(context, SubscribeActivity.class);
                                    intent.putExtra("account_type","driver");
                                    intent.putExtra("phone",phone);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    M.hideLoadingDialog();
                                    alertDialog.dismiss();
                                    Toast.makeText(context, R.string.this_account_is_inactive, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                M.hideLoadingDialog();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    M.hideLoadingDialog();
                    Toast.makeText(context, R.string.something_went_wrong_plz_try_again, Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("phone", phone);
                    params.put("login_type","1"); // type 1 means login without password
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
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
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
                binding.countryCode.registerCarrierNumberEditText(binding.inputPhone);
                phone = binding.countryCode.getFullNumberWithPlus().replace("", "").trim();
                if (!binding.inputPhone.getText().toString().trim().isEmpty()) {
                    sendVerificationCode(phone);
                    binding.progressBarVerify.setVisibility(View.VISIBLE);
                    binding.sendOtp.setVisibility(View.GONE);
                    binding.inputPhone.setEnabled(false);
                    binding.countryCode.setCcpClickable(false);
                    sendResend = 1;
                } else {
                    binding.inputLayoutPhone.setError(getResources().getString(R.string.enter_your_phone_number));
                    requestFocus(binding.inputPhone);
                }
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
                ActivityCompat.requestPermissions(LoginWithOTP.this,
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
    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch7(false);
        Intent intent = new Intent(LoginWithOTP.this, MainActivity.class);
        intent.putExtra("fragment_name", "");
        startActivity(intent);
        finish();
    }
}