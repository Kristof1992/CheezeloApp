package com.example.chezelooapp.ui.wine;

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
import com.example.chezelooapp.adapters.WineAdapter;
import com.example.chezelooapp.models.IsAdministrator;
import com.example.chezelooapp.models.Product;
import java.util.LinkedList;
import java.util.List;

public class WineFragment extends Fragment {
    List<Product> mList=new LinkedList<>();
    ILoadWine mInterfaceLoader;
    IsAdministrator mAdminInterface;
    boolean isAdmin=false;
    WineAdapter mAdapter;

    public interface ILoadWine{
        void loadWine(List<Product> mList, WineAdapter mAdapter);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wine, container, false);
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mInterfaceLoader=(ILoadWine)getActivity();
        mAdminInterface=(IsAdministrator)getActivity();
        if(mAdminInterface!=null)
        isAdmin= mAdminInterface.isAdministrator();
    }

    @Override
    public void onResume() {
        super.onResume();
        mInterfaceLoader.loadWine(mList,mAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    private void initUI(View root){
        RecyclerView mRecyclerview=root.findViewById(R.id.rw_wine);
        mAdapter=new WineAdapter(getContext(),mList, Navigation.findNavController(root),isAdmin,mAdminInterface);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerview.setAdapter(mAdapter);
    }

}