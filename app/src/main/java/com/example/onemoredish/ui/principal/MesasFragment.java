package com.example.onemoredish.ui.principal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onemoredish.R;
import com.example.onemoredish.ui.comun.MapaMesas;

/** Pestaña "Mesas" (la usan admin y mesero). */
public class MesasFragment extends Fragment {
    private MapaMesas mapa;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup cont, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_mesas, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        mapa = new MapaMesas(requireActivity(), v);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapa != null) mapa.pintar();
    }
}