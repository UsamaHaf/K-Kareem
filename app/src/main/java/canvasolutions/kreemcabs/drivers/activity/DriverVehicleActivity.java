package canvasolutions.kreemcabs.drivers.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.adapter.CategoryVehicleAdapter;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.model.CategoryVehiclePojo;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.settings.AppConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverVehicleActivity extends AppCompatActivity {
    private String val_brand_subs, val_model_subs, val_color_subs, val_numberplate_subs, val_number_passengers_subs;
    private static EditText brand_subs,model_subs, color_subs,numberplate_subs,number_passengers_subs;
    private TextInputLayout input_layout_brand_subs, input_layout_model_subs, input_layout_color_subs, input_layout_number_passengers_subs
            , input_layout_numberplate_subs;
    private Context context;
    RadioButton type_vehicle,type_bike;
    CheckBox inside_city,outside_city,bike_driver,delivery_boy;
    RadioGroup choose_inside_outside_city,choose_driver_delivery_bike;
    TextView title;
    String vehicle_bike_status = "";//inside city or outside city
    String category_status = ""; //vehicle or bike
//    private static ProgressBar progressBar_subs;
    private FloatingActionButton button_next;
    private static String global_url = AppConst.Server_url;
    public static RecyclerView recycler_view_category_vehicle;
    public static List<CategoryVehiclePojo> albumList_category_vehicle;
    public static CategoryVehicleAdapter adapter_category_vehicle;
    public static String id_categorie_vehicle = "",id_driver="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_vehicle);
        context = DriverVehicleActivity.this;

        Bundle objetbundle = this.getIntent().getExtras();
        id_driver = objetbundle.getString("id_driver");
//        id_driver = "7";
        brand_subs = (EditText) findViewById(R.id.brand_subs);
        model_subs = (EditText) findViewById(R.id.model_subs);
        color_subs = (EditText)findViewById(R.id.color_subs);
        numberplate_subs = (EditText)findViewById(R.id.numberplate_subs);
        number_passengers_subs = (EditText)findViewById(R.id.number_passengers_subs);
//        progressBar_subs = (ProgressBar) findViewById(R.id.progressBar_subs);
        input_layout_brand_subs = (TextInputLayout)findViewById(R.id.input_layout_brand_subs);
        input_layout_model_subs = (TextInputLayout)findViewById(R.id.input_layout_model_subs);
        input_layout_color_subs = (TextInputLayout)findViewById(R.id.input_layout_color_subs);
        input_layout_numberplate_subs = (TextInputLayout)findViewById(R.id.input_layout_numberplate_subs);
        input_layout_number_passengers_subs = (TextInputLayout)findViewById(R.id.input_layout_number_passengers_subs);
        button_next = (FloatingActionButton) findViewById(R.id.button_next);
        type_bike = (RadioButton) findViewById(R.id.type_bike);
        type_vehicle = (RadioButton) findViewById(R.id.type_vehicle);
        inside_city =  findViewById(R.id.inside_city);
        outside_city = findViewById(R.id.out_city);
        bike_driver =  findViewById(R.id.bike_driver);
        delivery_boy =  findViewById(R.id.delivery_boy);
        choose_inside_outside_city = (RadioGroup) findViewById(R.id.choose_inside_outside);
        choose_driver_delivery_bike = (RadioGroup) findViewById(R.id.choose_driver_delivery);
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(AppConst.font_quicksand_medium(context));

        albumList_category_vehicle = new ArrayList<>();
        adapter_category_vehicle = new CategoryVehicleAdapter(context, albumList_category_vehicle,this);
        recycler_view_category_vehicle = (RecyclerView) findViewById(R.id.recycler_view_category_vehicle);
        recycler_view_category_vehicle.setItemAnimator(new DefaultItemAnimator());
        recycler_view_category_vehicle.setAdapter(adapter_category_vehicle);

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!id_categorie_vehicle.equals("")){
                    val_brand_subs = brand_subs.getText().toString();
                    val_model_subs = model_subs.getText().toString();
                    val_color_subs = color_subs.getText().toString();
                    val_numberplate_subs = numberplate_subs.getText().toString();
                    val_number_passengers_subs = number_passengers_subs.getText().toString();
                    submitFormSubscribe();
                }else{
                    Toast.makeText(context, getResources().getString(R.string.please_select_your_vehicle_type), Toast.LENGTH_SHORT).show();
                }
            }
        });
        type_vehicle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    delivery_boy.setChecked(false);
                    bike_driver.setChecked(false);
                    vehicle_bike_status = "";
                    category_status = "vehicle";
                    M.showLoadingDialog(context);
                    choose_inside_outside_city.setVisibility(View.VISIBLE);
                    choose_driver_delivery_bike.setVisibility(View.GONE);
                    M.setRideType(context, "car", M.RIDETYPE_KEY);
                    new getCategoryVehicle().execute();
                }
            }
        });
        type_bike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    M.showLoadingDialog(context);
                    inside_city.setChecked(false);
                    outside_city.setChecked(false);
                    vehicle_bike_status = "";
                    category_status = "bike";
                    choose_inside_outside_city.setVisibility(View.GONE);
                    choose_driver_delivery_bike.setVisibility(View.VISIBLE);
                    M.setRideType(context,"bike",M.RIDETYPE_KEY);
                    new getCategoryVehicle().execute();
                }
            }
        });
        inside_city.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (outside_city.isChecked()) vehicle_bike_status = "both";
                    else vehicle_bike_status = "in_city";
                }
                else {
                    if (outside_city.isChecked()) vehicle_bike_status = "city_to_city";
                    else vehicle_bike_status = "";
                }
            }
        });
        outside_city.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (inside_city.isChecked())vehicle_bike_status = "both";
                    else vehicle_bike_status = "city_to_city";
                }
                else {
                    if (inside_city.isChecked()) vehicle_bike_status = "in_city";
                    else vehicle_bike_status = "";
                }
            }
        });
        bike_driver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (delivery_boy.isChecked()) vehicle_bike_status = "both";
                    else vehicle_bike_status = "bike_driver";
                }
                else {
                    if (delivery_boy.isChecked()) vehicle_bike_status = "delivery_boy";
                    else vehicle_bike_status = "";
                }
            }
        });
        delivery_boy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (bike_driver.isChecked()) vehicle_bike_status = "both";
                    else vehicle_bike_status = "delivery_boy";
                }
                else {
                    if (bike_driver.isChecked()) vehicle_bike_status = "bike_driver";
                    else vehicle_bike_status = "";
                }
            }
        });
    }

    /** Récupération des catégories de véhicule**/
    public class getCategoryVehicle extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = AppConst.Server_url+"get_categorie_vehicle.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                albumList_category_vehicle.clear();
                                adapter_category_vehicle.notifyDataSetChanged();
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    for(int i=0; i<(msg.length()-1); i++) {
                                        JSONObject taxi = msg.getJSONObject(String.valueOf(i));
                                        if(i==0){
                                            albumList_category_vehicle.add(new CategoryVehiclePojo(taxi.getInt("id"),taxi.getString("libelle"),taxi.getString("image"),
                                                    "",taxi.getString("prix"),"yes",taxi.getString("statut_commission"),taxi.getString("commission"),taxi.getString("type"),taxi.getString("statut_commission_perc"),taxi.getString("commission_perc"),taxi.getString("type_perc")));
                                        }else{
                                            albumList_category_vehicle.add(new CategoryVehiclePojo(taxi.getInt("id"),taxi.getString("libelle"),taxi.getString("image"),
                                                    "",taxi.getString("prix"),"no",taxi.getString("statut_commission"),taxi.getString("commission"),taxi.getString("type"),taxi.getString("statut_commission_perc"),taxi.getString("commission_perc"),taxi.getString("type_perc")));
                                        }
                                        adapter_category_vehicle.notifyDataSetChanged();
                                    }
                                    Log.d("TAGRES", "onResponse: "+response);
                                    Log.d("TAGRES", "onResponse: "+albumList_category_vehicle);
                                }else{

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
                    params.put("type",M.getRideType(context,M.RIDETYPE_KEY));
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

    /**
     * Validating form
     */
    private void submitFormSubscribe() {
        if (category_status == null || category_status.equals("") ){
            Toast.makeText(context, getString(R.string.please_select_type), Toast.LENGTH_SHORT).show();
            return;
        }
        if(type_vehicle.isChecked()){
            if (vehicle_bike_status == null || vehicle_bike_status.equals("")){
                Toast.makeText(context, getString(R.string.please_select_service_type), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(type_bike.isChecked()){
            if (vehicle_bike_status == null || vehicle_bike_status.equals("")){
                Toast.makeText(context, getString(R.string.please_select_service_type), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (!validateBrand()) {
            return;
        }
        if (!validateModel()) {
            return;
        }
        if (!validateColor()) {
            return;
        }
        if (!validateNumberPlate()) {
            return;
        }
        if (!validateNumberPassanger()) {
            return;
        }
//        progressBar_subs.setVisibility(View.VISIBLE);
        M.showLoadingDialog(context);
        new createDriverVehicle().execute();
    }

    private boolean validateBrand() {
        if (brand_subs.getText().toString().trim().isEmpty()) {
            input_layout_brand_subs.setError(getResources().getString(R.string.enter_your_vehicle_brand));
            requestFocus(brand_subs);
            return false;
        } else {
            input_layout_brand_subs.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateModel() {
        if (model_subs.getText().toString().trim().isEmpty()) {
            input_layout_model_subs.setError(getResources().getString(R.string.enter_your_vehicle_model));
            requestFocus(model_subs);
            return false;
        } else {
            input_layout_model_subs.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateColor() {
        if (color_subs.getText().toString().trim().isEmpty()) {
            input_layout_color_subs.setError(getResources().getString(R.string.enter_your_vehicle_color));
            requestFocus(color_subs);
            return false;
        } else {
            input_layout_color_subs.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNumberPlate() {
        if (numberplate_subs.getText().toString().trim().isEmpty()) {
            input_layout_numberplate_subs.setError(getResources().getString(R.string.enter_your_vehicle_numberplate));
            requestFocus(numberplate_subs);
            return false;
        } else {
            input_layout_numberplate_subs.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNumberPassanger() {
        if (number_passengers_subs.getText().toString().trim().isEmpty()) {
            input_layout_number_passengers_subs.setError(getResources().getString(R.string.enter_your_number_of_passenger));
            requestFocus(number_passengers_subs);
            return false;
        } else {
            input_layout_number_passengers_subs.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /** Enregistrement d'un utilisateur **/
    private class createDriverVehicle extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = global_url+"driver_vehicle_register.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                M.hideLoadingDialog();
                                Log.d("TAGABC", "onResponse: "+response);
//                                progressBar_subs.setVisibility(View.INVISIBLE);
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                if(etat.equals("1")){
                                    JSONObject vehicle = json.getJSONObject("vehicle");
                                    brand_subs.setText("");
                                    model_subs.setText("");
                                    color_subs.setText("");
                                    numberplate_subs.setText("");
//                                    Toast.makeText(context, "Successfully completed", Toast.LENGTH_SHORT).show();

                                    M.setVehicleBrand( vehicle.getString("brand"),context);
                                    M.setVehicleModel( vehicle.getString("model"),context);
                                    M.setVehicleColor( vehicle.getString("color"),context);
                                    M.setVehicleNumberPlate( vehicle.getString("numberplate"),context);

                                    Intent intent = new Intent(DriverVehicleActivity.this, ChoosePhotoActivity.class);
                                    intent.putExtra("id_driver",id_driver);
                                    startActivity(intent);
                                    finish();

                                }else if(etat.equals("2")){

                                }else
                                    Toast.makeText(context,R.string.registration_failed, Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {

                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
//                    progressBar_subs.setVisibility(View.INVISIBLE);
                    M.hideLoadingDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("brand", val_brand_subs);
                    params.put("model", val_model_subs);
                    params.put("color", val_color_subs);
                    params.put("numberplate", val_numberplate_subs);
                    params.put("passenger", val_number_passengers_subs);
                    params.put("id_categorie_vehicle", id_categorie_vehicle);
                    params.put("id_driver", id_driver);
                    params.put("bike_or_vehicle", category_status);
                    params.put("vehicle_bike_status", vehicle_bike_status);
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
