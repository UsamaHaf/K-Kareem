package canvasolutions.kreemcabs.drivers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.databinding.ActivityPhoneVerificationBinding;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.settings.AppConst;

public class PhoneVerification extends AppCompatActivity {
    ActivityPhoneVerificationBinding binding;
    FirebaseAuth mAuth;
    String phone,email;
    LinearLayout  phoneVerificationLayout,emailVerificationLayout;
    TextView resendCode,resendCodeEmail,timer,timerEmail,clearValue,sendCode,sendCodeEmail,responseMessageShow;
    EditText verifyCode;
    Pinview pinview;
    ProgressBar progressBarVerify,progressBarVerifyEmail;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    String verificationId = "";
    boolean isCodeSent = false;
    private static String global_url = AppConst.Server_url;
    String emailVerifyCode = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_phone_verification);

        phoneVerificationLayout = findViewById(R.id.phone_verification_layout);
        emailVerificationLayout = findViewById(R.id.email_verification_layout);
        resendCode = findViewById(R.id.resend_code);
        resendCodeEmail = findViewById(R.id.resend_code_email);
        progressBarVerify = findViewById(R.id.progressBar_verify);
        progressBarVerifyEmail = findViewById(R.id.progressBar_verify_email);
        timer = findViewById(R.id.timer);
        timerEmail = findViewById(R.id.timer_email);
        clearValue = findViewById(R.id.clear_value);
        pinview = findViewById(R.id.pinview);
        sendCode = findViewById(R.id.send_code);
        sendCodeEmail = findViewById(R.id.send_code_email);
        verifyCode = findViewById(R.id.verify_code);
        responseMessageShow = findViewById(R.id.response_message_show);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .95), (int) (height * .38));

        if (getIntent().hasExtra("phone")){
            phoneVerificationLayout.setVisibility(View.VISIBLE);
            emailVerificationLayout.setVisibility(View.GONE);
            phone = getIntent().getStringExtra("phone");
        }
        if (getIntent().hasExtra("email")) {
            emailVerificationLayout.setVisibility(View.VISIBLE);
            phoneVerificationLayout.setVisibility(View.GONE);
            email = getIntent().getStringExtra("email");
        }

        Log.d("TAGPHONE", "onClick: "+phone+" "+email);
        mAuth = FirebaseAuth.getInstance();
        resendCode.setVisibility(View.GONE);
        progressBarVerify.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);


        clearValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pinview.getValue().isEmpty()){
                    pinview.clearValue();
                }
            }
        });
        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                if (isCodeSent){
                    progressBarVerify.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.GONE);
                    verifyCode(pinview.getValue());
                }else {
                    Toast.makeText(PhoneVerification.this, R.string.get_verification_code_first, Toast.LENGTH_SHORT).show();
                }

            }
        });

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                phone = getphone.getText().toString().trim();
                sendVerificationCode(phone);
                progressBarVerify.setVisibility(View.VISIBLE);
                sendCode.setVisibility(View.GONE);
            }
        });
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phone,resendingToken);
                progressBarVerify.setVisibility(View.VISIBLE);
                resendCode.setVisibility(View.GONE);
            }
        });
        resendCodeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCodeEmail.setVisibility(View.GONE);
                progressBarVerifyEmail.setVisibility(View.VISIBLE);
                new sendVerificationEmail().execute();
            }
        });
        sendCodeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCodeEmail.setVisibility(View.GONE);
                progressBarVerifyEmail.setVisibility(View.VISIBLE);
                new sendVerificationEmail().execute();
            }
        });
        verifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TAGreeor", "onTextChanged: "+emailVerifyCode);
                if(verifyCode.getText().toString().length() == emailVerifyCode.length()){
                    if (verifyCode.getText().toString().toLowerCase().equals(emailVerifyCode.toLowerCase())){
                        M.setEmailVerified(PhoneVerification.this,true);
                        Toast.makeText(PhoneVerification.this, "Email Verified", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });
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
                            M.setPhoneVerified(PhoneVerification.this,true);
                            Toast.makeText(PhoneVerification.this, getString(R.string.phone_verified), Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            progressBarVerify.setVisibility(View.GONE);
                            timer.setVisibility(View.VISIBLE);
                            Toast.makeText(PhoneVerification.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        progressBarVerify.setVisibility(View.VISIBLE);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder ( mAuth )
                . setPhoneNumber (number) // Phone number to verify
                . setTimeout ( 60L , TimeUnit. SECONDS ) // Timeout and unit
                . setActivity (PhoneVerification.this) // Activity (for callback 
                . setCallbacks (mCallback) // OnVerificationStateChangedCallbacks
                . build ();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback 
                mCallback,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d("TAGMESSAGE", "onVerificationFailed: "+e.getMessage());
            responseMessageShow.setText(R.string.failed_to_send_code);
            progressBarVerify.setVisibility(View.GONE);
            sendCode.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
            responseMessageShow.setText(getString(R.string.verification_code_sent)+phone);
            verificationId = s;
            resendingToken = forceResendingToken;
            isCodeSent = true;
            countDown(timer);

        }
    };

    private void countDown(TextView timerTextView){
        new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setVisibility(View.VISIBLE);
                timerTextView.setText(getResources().getString(R.string.resend_code)+" in 00:"+millisUntilFinished/1000);
                sendCode.setVisibility(View.GONE);
                sendCodeEmail.setVisibility(View.GONE);
                progressBarVerify.setVisibility(View.GONE);
                progressBarVerifyEmail.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {
                if (timerEmail.getVisibility() == View.VISIBLE){
                    resendCodeEmail.setVisibility(View.VISIBLE);
                }else {
                    resendCode.setVisibility(View.VISIBLE);
                }
                timerTextView.setVisibility(View.GONE);
            }
        }.start();
    }
    private class sendVerificationEmail extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = global_url+"verify_email.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("TAGreeor", "onResponse: "+response);
                        JSONObject json = new JSONObject(response);
                        JSONObject msg = json.getJSONObject("msg");
                        String msg_status = msg.getString("status");
                        String message = msg.getString("message");
                        String data = msg.getString("data");
                        if (msg_status.equals("1")){
                            responseMessageShow.setText(message);
                            countDown(timerEmail);
//                            verifyCode.setText(data);
                            emailVerifyCode = data;
                        }
                    } catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    responseMessageShow.setText(getString(R.string.something_went_wrong_plz_try_again));
                    sendCodeEmail.setVisibility(View.VISIBLE);
                    progressBarVerifyEmail.setVisibility(View.GONE);
                    Log.d("TAGreeor", "onErrorResponse: "+error);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", email);
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
}