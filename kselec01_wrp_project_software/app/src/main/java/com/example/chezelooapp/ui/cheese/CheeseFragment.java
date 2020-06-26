package com.example.chezelooapp.ui.cheese;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chezelooapp.R;
import com.example.chezelooapp.adapters.CheeseAdapter;
import com.example.chezelooapp.models.IsAdministrator;
import com.example.chezelooapp.models.Product;

import java.util.LinkedList;
import java.util.List;

public class CheeseFragment extends Fragment {
    private CheeseAdapter mAdapter;

    private ILoadCheese mInterfaceLoader;
    private IsAdministrator mAdminInterface;
    private boolean isAdmin = false;

    private List<Product> mCheeseList = new LinkedList<>();


    public interface ILoadCheese {
        void loadCheese(List<Product> mList, CheeseAdapter mAdapter);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       return  inflater.inflate(R.layout.fragment_cheese, container, false);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mInterfaceLoader = (ILoadCheese) getActivity();
        mAdminInterface = (IsAdministrator) getActivity();
        if (mAdminInterface != null)
            isAdmin = mAdminInterface.isAdministrator();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }


    @Override
    public void onResume() {
        loadCheese();
        super.onResume();
    }

    private void loadCheese() {
        mInterfaceLoader.loadCheese(mCheeseList, mAdapter);
    }

    private void initUI(View root) {
        RecyclerView mRecyclerview = root.findViewById(R.id.rw_cheese);
        mAdapter = new CheeseAdapter(getContext(), mCheeseList, Navigation.findNavController(root), isAdmin, mAdminInterface);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerview.setAdapter(mAdapter);
    }
}