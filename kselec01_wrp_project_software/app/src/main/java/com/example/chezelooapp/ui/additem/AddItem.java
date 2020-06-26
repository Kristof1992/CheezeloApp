package com.example.chezelooapp.ui.additem;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chezelooapp.R;

import static android.content.ContentValues.TAG;

public class AddItem extends Fragment {

    OnAddItem mOnAddItem;

    public interface OnAddItem {
        void addItem(String title, String description, String type, String country, String pairs, boolean isCheese);
        void addImageMethod(View view);
    }

    private Context mContext;
    private EditText mName;
    private EditText mDescription;
    private EditText mType;
    private EditText mCountry;
    private EditText mPair;
    private ImageView mImage;
    private RadioButton mCheeseBtn;
    private RadioButton mWineBtn;
    private Button addBtn;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View mViewHolder=view;

        mName = view.findViewById(R.id.addProduct_name);
        mDescription = view.findViewById(R.id.addProduct_description);
        mType = view.findViewById(R.id.addProduct_type);
        mCountry = view.findViewById(R.id.addProduct_country_origin);
        mPair = view.findViewById(R.id.addProduct_pairs);
        mImage = view.findViewById(R.id.addItem_img);
        mCheeseBtn = view.findViewById(R.id.radioCheese);
        addBtn = view.findViewById(R.id.addProduct_btn_ok);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage(mViewHolder);
            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mOnAddItem = (OnAddItem) getActivity();
    }

    private void addImage(View view) {
        mOnAddItem.addImageMethod(view);
    }

    public void addItem() {
        String name = mName.getText().toString();
        String description = mDescription.getText().toString();
        String type = mType.getText().toString();
        String country = mCountry.getText().toString();
        String pair = mPair.getText().toString();
        boolean isCheese = mCheeseBtn.isChecked();
        //TODO take img url

        if (!name.isEmpty() && !description.isEmpty() && !type.isEmpty() && !country.isEmpty() && !pair.isEmpty()&&mImage.getDrawable()!=null) {
            mOnAddItem.addItem(name, description, type, country, pair, isCheese);
        } else {
            Toast.makeText(getContext(), "Please fill all the fields and add an image from your device", Toast.LENGTH_LONG).show();
        }
    }
}