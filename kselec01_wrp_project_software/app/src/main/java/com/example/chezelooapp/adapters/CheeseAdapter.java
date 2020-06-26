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
import com.example.chezelooapp.models.IsAdministrator;
import com.example.chezelooapp.models.Product;
import com.example.chezelooapp.R;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class CheeseAdapter extends RecyclerView.Adapter<CheeseAdapter.CheeseHolder> {

    private Context mContext;
    private List<Product> mList;
    private NavController navController;
    private boolean isAdmin;
    private IsAdministrator administrator;

    public CheeseAdapter(Context mContext, List<Product> mList, NavController navController, boolean isAdmin, IsAdministrator administrator) {
        this.mContext = mContext;
        this.mList = mList;
        this.navController = navController;
        this.isAdmin = isAdmin;
        this.administrator = administrator;
    }

    @NonNull
    @Override
    public CheeseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder;
        holder = LayoutInflater.from(mContext).inflate(R.layout.raw_item_cheese, parent, false);
        return new CheeseHolder(holder);
    }

    // Filling the view holders with data
    @Override
    public void onBindViewHolder(@NonNull CheeseHolder holder, int position) {
        holder.text.setText(mList.get(position).getTitle());
        holder.mLayout.setOnClickListener(v -> {
            // Bundles are used to pass values from one activity to another
            Bundle bundle = new Bundle();
            // Bundle a mapping from String keys to various Parcelable values
            // .putParcelable (String key, Parcelable value)
            bundle.putParcelable("navArgs", mList.get(position));
            // Fragment nav_details in mobile_navigation.xml
            navController.navigate(R.id.nav_details, bundle);
        });

        // If user is admin bin icon will be visible and delete function on long tap will be added
        if (isAdmin) {
            holder.deleteBTn.setVisibility(View.VISIBLE);
            holder.deleteBTn.setOnLongClickListener(v -> {
                Snackbar
                        .make(v, "Woud you like to delete this item", Snackbar.LENGTH_LONG)
                        //
                        .setAction(
                                "Ok", v1 -> administrator.onLongClickPressed(mList.get(position).getKey(),true))
                        .show();
                return true;
            });
        }

    }

    // Returns the number of items in the list
    @Override
    public int getItemCount() {
        return (mList == null) ? 0 : mList.size();
    }



    // The nested class is static to avoid instantiating the parent class
     static class CheeseHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout mLayout;
        private AppCompatTextView text;
        private ImageView deleteBTn;

         CheeseHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.raw_cheese_layout);
            text = itemView.findViewById(R.id.raw_cheese_text);
            deleteBTn = itemView.findViewById(R.id.raw_cheese_delete);
        }

    }

}