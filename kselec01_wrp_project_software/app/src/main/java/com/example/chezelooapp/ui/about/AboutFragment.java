package com.example.chezelooapp.ui.about;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.chezelooapp.R;

import static android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD;

public class AboutFragment extends Fragment {

    @SuppressLint("WrongConstant")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_about, container, false);
        final TextView mText=root.findViewById(R.id.text_about);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mText.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
        return root;
    }
}