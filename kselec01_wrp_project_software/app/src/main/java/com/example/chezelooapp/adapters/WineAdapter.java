package com.example.chezelooapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chezelooapp.R;
import com.example.chezelooapp.models.IsAdministrator;
import com.example.chezelooapp.models.Product;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class WineAdapter extends RecyclerView.Adapter<WineAdapter.WineHolder> {
    private Context mContext;
    private List<Product> mList;
    private NavController navController;
    private boolean isAdmin;
    private IsAdministrator administrator;


    public WineAdapter(Context mContext, List<Product> mList, NavController navController, boolean isAdmin, IsAdministrator administrator) {
        this.mContext = mContext;
        this.mList = mList;
        this.navController = navController;
        this.isAdmin = isAdmin;
        this.administrator = administrator;
    }

    @NonNull
    @Override
    public WineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder;
        holder = LayoutInflater.from(mContext).inflate(R.layout.raw_item_wine, parent, false);
        return new WineHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull WineHolder holder, int position) {
        holder.text.setText(mList.get(position).getTitle());
        holder.mLayout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("navArgs", mList.get(position));
            navController.navigate(R.id.nav_details, bundle);
        });

        if (isAdmin) {
            holder.deleteteBTn.setVisibility(View.VISIBLE);
            holder.deleteteBTn.setOnLongClickListener(v -> {

                Snackbar
                        .make(v, "Would you like to delete this item", Snackbar.LENGTH_LONG)
                        .setAction(
                                "Ok", v1 -> administrator.onLongClickPressed(mList.get(position).getKey(),false))
                        .show();
                return true;
            });
        }
    }



    @Override
    public int getItemCount() {
        return (mList == null) ? 0 : mList.size();
    }

     static class WineHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView text;
        private ConstraintLayout mLayout;
        private ImageView deleteteBTn;

         WineHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.raw_wine_text);
            mLayout = itemView.findViewById(R.id.raw_wine_layout);
            deleteteBTn = itemView.findViewById(R.id.raw_wine_delete);
        }
    }
}
