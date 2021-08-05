package canvasolutions.kreemcabs.drivers.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import canvasolutions.kreemcabs.drivers.R;
import canvasolutions.kreemcabs.drivers.model.M;
import canvasolutions.kreemcabs.drivers.model.TransactionPojo;
import canvasolutions.kreemcabs.drivers.settings.AppConst;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private Context mContext;
    private List<TransactionPojo> albumList;
    Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private TextView date, amount,time;
        private ImageView image;
        private ProgressBar progressBar;


        public MyViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            amount = (TextView) view.findViewById(R.id.amount);
            time = (TextView) view.findViewById(R.id.time);
            image = (ImageView) view.findViewById(R.id.icon_transation);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar_icon_transtion);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            CoursePojo coursePojo = albumList.get(getAdapterPosition());
        }
    }

    public TransactionAdapter(Context mContext, List<TransactionPojo> albumList, Activity activity) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card_transaction, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TransactionPojo transactionPojo = albumList.get(position);
        holder.amount.setText(M.getCurrency(mContext)+" "+transactionPojo.getAmount());
        holder.date.setText(transactionPojo.getDate());
        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(AppConst.Server_url+"images/app_user/"+transactionPojo.getImage())
                .skipMemoryCache(false)
//                .centerCrop()
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
                }).into(holder.image);
        holder.time.setText(transactionPojo.getTime());

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

    public TransactionPojo getComment(int id){
        TransactionPojo transactionPojo = null;
        for (int i=0; i< albumList.size(); i++){
            if(albumList.get(i).getId() == id){
                transactionPojo = albumList.get(i);
                break;
            }
        }
        return transactionPojo;
    }
}