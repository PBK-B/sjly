package com.zpj.sjly.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zpj.sjly.R;
import com.zpj.sjly.bean.AppItem;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private List<AppItem> appItemList;
    private Context context;

    private RequestManager requestManager;

    private OnItemClickListener onItemClickListener;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView appIcon;
        TextView appTitle;
        TextView appInfo;
        TextView appDesc;

        public Bitmap icon;
        AppItem item;

        public ViewHolder(View view){
            super(view);
            itemView = view;
            appIcon = view.findViewById(R.id.item_icon);
            appTitle = view.findViewById(R.id.item_title);
            appInfo = view.findViewById(R.id.item_info);
            appDesc = view.findViewById(R.id.item_desc);

        }
    }

    public AppAdapter(List<AppItem> appItemList){
        this.appItemList = appItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        context = parent.getContext();
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item,parent,false);

        final ViewHolder holder = new ViewHolder(view);

        requestManager = Glide.with(parent.getContext());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AppAdapter.ViewHolder holder, final int position) {
        final AppItem appItem = appItemList.get(position);
        holder.item = appItem;
        holder.appTitle.setText(appItem.getAppTitle());
        holder.appInfo.setText(appItem.getAppSize() + " | " + appItem.getAppInfo());
        holder.appDesc.setText(appItem.getAppComment());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder, holder.getAdapterPosition(), holder.item);
                }
            }
        });

        String icon = appItemList.get(position).getAppIcon();
        requestManager.asBitmap().load(icon).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                holder.icon = resource;
                holder.appIcon.setImageBitmap(resource);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appItemList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(ViewHolder holder, int position, AppItem item);
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


}
