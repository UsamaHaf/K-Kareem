package canvasolutions.kreemcabs.drivers.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.activity.DriverVehicleActivity;
import canvasolutions.kreemcabs.drivers.model.CategoryVehiclePojo;
import canvasolutions.kreemcabs.drivers.settings.AppConst;

import java.util.List;

public class CategoryVehicleAdapter extends RecyclerView.Adapter<CategoryVehicleAdapter.MyViewHolder> {

    private Context mContext;
    private List<CategoryVehiclePojo> albumList;
    Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private TextView duration_request,name_vehicule;
        private LinearLayout linear;
        private ImageView img_vehicule;
        private ProgressBar progressBar;


        public MyViewHolder(View view) {
            super(view);
            duration_request = (TextView) view.findViewById(R.id.duree_requete);
            name_vehicule = (TextView) view.findViewById(R.id.name_vehicule);
            linear = (LinearLayout) view.findViewById(R.id.linear);
            img_vehicule = (ImageView) view.findViewById(R.id.img_vehicule);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

    public CategoryVehicleAdapter(Context mContext, List<CategoryVehiclePojo> albumList, Activity activity) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card_category_vehicle, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CategoryVehiclePojo categoryVehiclePojo = albumList.get(position);
        holder.duration_request.setText(categoryVehiclePojo.getDuration());
        holder.name_vehicule.setText(categoryVehiclePojo.getName());

        if(categoryVehiclePojo.getStatut().equals("yes")) {
            holder.linear.setBackground(mContext.getResources().getDrawable(R.drawable.custom_driver_select));
            DriverVehicleActivity.id_categorie_vehicle = String.valueOf(categoryVehiclePojo.getId());
        }else
            holder.linear.setBackground(null);

        Glide.with(mContext).load(AppConst.Server_urlMain+"assets/images/type_vehicle/"+
                categoryVehiclePojo.getImage())
                .skipMemoryCache(false)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.img_vehicule);
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryVehiclePojo categoryVehiclePojo = albumList.get(position);
                categoryVehiclePojo.setStatut("yes");
                DriverVehicleActivity.id_categorie_vehicle = String.valueOf(categoryVehiclePojo.getId());
                for(int i=0; i<albumList.size(); i++){
                    if(albumList.get(i).getId() != categoryVehiclePojo.getId())
                        albumList.get(i).setStatut("no");
                }
                notifyDataSetChanged();
            }
        });
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

    public CategoryVehiclePojo getCategorieVehicle(int id){
        CategoryVehiclePojo categorieTaxiPojo = null;
        for (int i=0; i< albumList.size(); i++){
            if(albumList.get(i).getId() == id){
                categorieTaxiPojo = albumList.get(i);
                break;
            }
        }
        return categorieTaxiPojo;
    }
}