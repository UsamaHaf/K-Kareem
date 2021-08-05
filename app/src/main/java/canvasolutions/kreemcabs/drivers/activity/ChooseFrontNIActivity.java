package canvasolutions.kreemcabs.drivers.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.controller.AppController;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.settings.AppConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ChooseFrontNIActivity extends AppCompatActivity {
    private TextView select_photo_front;
    private static int RESULT_LOAD_IMAGE = 1;
    private final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Uri filePath;
    public static Bitmap bitmap = null;
    public static Bitmap bitmap_anc = null;
    public static ImageView ni_photo_front;
    private static String img_data="",img_name="";
    private TextView title_front,message_front;
    private Context context;
    private FloatingActionButton button_next_front;
    private String id_driver="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_front_ni);

        Bundle objetbundle = this.getIntent().getExtras();
        id_driver = objetbundle.getString("id_driver");
//        id_driver = "121";
        context = ChooseFrontNIActivity.this;
        select_photo_front = (TextView)findViewById(R.id.select_photo_front);
        ni_photo_front = (ImageView) findViewById(R.id.ni_photo_front);
        title_front = (TextView) findViewById(R.id.title_front);
        button_next_front = (FloatingActionButton) findViewById(R.id.button_next_front);
        title_front.setTypeface(AppConst.font_quicksand_medium(context));
        message_front = (TextView) findViewById(R.id.message_front);
        message_front.setTypeface(AppConst.font_quicksand_medium(context));

        select_photo_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionGrantedFirst() == true){
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }else{
                    isPermissionGranted();
                }
            }
        });

        button_next_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!img_data.equals("")){
//                    progressBar_login.setVisibility(View.VISIBLE);
                    M.showLoadingDialog(context);
                    new ChooseFrontNIActivity.setUserNic().execute();
                }else{
                    Toast.makeText(context, getResources().getString(R.string.choose_a_photo), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /** Registration of a photo of a driver's national identity card**/
    private class setUserNic extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            publishProgress();
            String url = AppConst.Server_url+"set_user_nic.php";
            StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                JSONObject msg = json.getJSONObject("msg");
                                String etat = msg.getString("etat");
                                M.hideLoadingDialog();
                                if(etat.equals("1")){
                                    if(!msg.getString("image").equals("")) {
                                        M.setNic(msg.getString("image"), context);
                                        Intent intent = new Intent(ChooseFrontNIActivity.this, ChooseNIActivity.class);
                                        intent.putExtra("id_driver",id_driver);
                                        startActivity(intent);
                                        finish();
                                    }else{

                                    }
                                }else{
                                    Toast.makeText(context, getResources().getString(R.string.failed_try_again), Toast.LENGTH_SHORT).show();
                                    ni_photo_front.setImageBitmap(bitmap_anc);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, getResources().getString(R.string.failed_try_again), Toast.LENGTH_SHORT).show();
                    M.hideLoadingDialog();
                    ni_photo_front.setImageBitmap(bitmap_anc);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("image",img_data);
                    params.put("image_name",img_name);
                    params.put("id_driver",id_driver);
                    params.put("user_cat","conducteur");
                    params.put("cnic_img_nmbr","1");
                    return params;
                }

            };
            AppController.getInstance().addToRequestQueue(jsonObjReq);

            return null;
        }

        /*@Override
        protected void onProgressUpdate(final Integer... values) {
            super.onProgressUpdate(values);
            Log.i("progression", String.valueOf(values[0]));
            // Update the progress bar
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(values[0]);
                    // Show the progress on TextView
//                    if((int)progressStatus <= 100)
//                        counter.setText((int)progressStatus+"%");
                }
            });
        }*/

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

    public boolean isPermissionGrantedFirst() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Toast.makeText(Accueil.this, "Permission is granted", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                //Toast.makeText(Accueil.this, "Permission is revoked", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Toast.makeText(Accueil.this, "Permission is granted", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(context, ""+getFileName(filePath), Toast.LENGTH_SHORT).show();

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            filePath = data.getData();
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if(bitmap_anc != null)
                bitmap_anc = ((BitmapDrawable) ni_photo_front.getDrawable()).getBitmap();

            bitmap = BitmapFactory.decodeFile(picturePath);
            bitmap = getResizeImage(bitmap,720,true);
            ni_photo_front.setImageBitmap(bitmap);

            if(bitmap != null){
                img_data = getStringImage(bitmap);
                img_name = getFileName(filePath);
            }else {
                img_data = "";
                img_name = "";
            }
        }else {

        }
    }

    public static Bitmap getResizeImage(Bitmap realImage, float maxImageSize, Boolean filter){
        float ratio = Math.min((float)maxImageSize / realImage.getWidth(), (float)maxImageSize / realImage.getHeight());
        int width = Math.round((float)ratio * realImage.getWidth());
        int height = Math.round((float)ratio * realImage.getHeight());
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}