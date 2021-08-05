package canvasolutions.kreemcabs.drivers.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.databinding.ActivityForgotPassworBinding;
import canvasolutions.kreemcabs.drivers.settings.AppConst;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswor extends AppCompatActivity {

    ActivityForgotPassworBinding binding;
    Context context;
    private static String global_url = AppConst.Server_url;
    TextView title,forgot;
    ProgressBar progressBar_forgot;
    EditText email_forgot;
    TextInputLayout input_layout_email_forgot;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_passwor);

        title = findViewById(R.id.title_forgot);
        progressBar_forgot = findViewById(R.id.progressBar_forgot);
        forgot = findViewById(R.id.forgot);
        email_forgot = findViewById(R.id.email_forgot);
        input_layout_email_forgot =  findViewById(R.id.input_layout_email_forgot); 

        context = ForgotPasswor.this;
        title.setTypeface(AppConst.font_quicksand_medium(context));
        progressBar_forgot.setVisibility(View.GONE);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }
    private void submit() {
//        if (!validatePhone()) {
//            return;
//        }
        if (!validateEmail()) {
            return;
        }
        progressBar_forgot.setVisibility(View.VISIBLE);
        new forgotPassword().execute();

    }

    private boolean validateEmail() {

        if (email_forgot.getText().toString().trim().isEmpty()) {
            input_layout_email_forgot.setError(getResources().getString(R.string.enter_your_email_address));
            email_forgot.requestFocus();
            return false;
        } if (!email_forgot.getText().toString().trim().contains("@")) {
            input_layout_email_forgot.setError(getResources().getString(R.string.please_enter_valid_email_address));
            email_forgot.requestFocus();
            return false;
        }
        if (!email_forgot.getText().toString().trim().contains(".")) {
            input_layout_email_forgot.setError(getResources().getString(R.string.please_enter_valid_email_address));
            email_forgot.requestFocus();
            return false;
        }
        if (email_forgot.getText().toString().trim().length() < 5) {
            input_layout_email_forgot.setError(getResources().getString(R.string.please_enter_valid_email_address));
            email_forgot.requestFocus();
            return false;
        }
        else {
            input_layout_email_forgot.setErrorEnabled(false);
        }

        return true;
    }
    /** Connexion d'un utilisateur **/
    private class forgotPassword extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = global_url+"forget_password.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBar_forgot.setVisibility(View.INVISIBLE);
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String msg_status = msg.getString("msg_status");

                                if (msg_status.equals("0")){
                                    Toast.makeText(context, msg.getString("message"), Toast.LENGTH_SHORT).show();
                                }else if (msg_status.equals("1")){
                                    dialogSucess(msg.getString("message"));//email sent
//                                    Toast.makeText(context, msg.getString("message"), Toast.LENGTH_SHORT).show();
                                }else if(msg_status.equals("2")){
                                    Toast.makeText(context, msg.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {

                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar_forgot.setVisibility(View.INVISIBLE);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_type", "2");
                    params.put("email", email_forgot.getText().toString().trim());
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
    private void dialogSucess(String message) throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(context);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_layout_subscribe_success, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        TextView close = (TextView) confirmDialog.findViewById(R.id.close);
        TextView msg = (TextView) confirmDialog.findViewById(R.id.msg);

        msg.setText(message);
        close.setText(getString(R.string.ok));

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
                startActivity(new Intent(context, LoginActivity.class));
                alertDialog.dismiss();
                finish();
            }
        });
        alertDialog.setCancelable(false);
    }
}