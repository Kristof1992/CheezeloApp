package com.example.chezelooapp.ui.details;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chezelooapp.R;
import com.example.chezelooapp.models.Product;
import com.squareup.picasso.Picasso;

import static android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    private static final String TAG = "DetailsFragment";
    ImageView mImage;
    TextView mTitle;
    TextView mType;
    TextView mCountry;
    TextView mPair;
    TextView mDescription;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    // Takes the data from the adapter's bundle and populates the relevant views
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImage=view.findViewById(R.id.details_image);
        mTitle=view.findViewById(R.id.details_title);
        mType=view.findViewById(R.id.details_type);
        mCountry=view.findViewById(R.id.details_country);
        mPair=view.findViewById(R.id.details_pair);
        mDescription=view.findViewById(R.id.details_description);

        Bundle bundle = getArguments();

       if(bundle!=null) {
           Product product = bundle.getParcelable("navArgs");
           Log.d(TAG, "onViewCreated: " + String.valueOf(product));
           init(product);
       }
    }

    @SuppressLint("WrongConstant")
    private void init(Product product){
               if (product!=null){
                   // Setting text-alignment to justified
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                           mDescription.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                       }
                   }
                   Picasso.get().load(product.getImage()).placeholder(R.drawable.ic_image_holder_new).into(mImage);
                   mTitle.setText("Title: "+product.getTitle());
                   mType.setText("Type: "+product.getType());
                   mCountry.setText("Country: "+product.getCountry());
                   mPair.setText("Pairs: "+product.getPairs());
                   mDescription.setText("Description: \n\n"+product.getDescription());
               }

    }




