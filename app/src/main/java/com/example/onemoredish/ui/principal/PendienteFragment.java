package com.example.onemoredish.ui.principal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onemoredish.R;

/** Marcador para las pestañas que se construyen en los siguientes pasos. */
public class PendienteFragment extends Fragment {
    private static final String ARG_TITULO = "titulo";

    public static PendienteFragment nuevo(String titulo) {
        PendienteFragment f = new PendienteFragment();
        Bundle a = new Bundle();
        a.putString(ARG_TITULO, titulo);
        f.setArguments(a);
        return f;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup cont, @Nullable Bundle b) {
        View v = inf.inflate(R.layout.fragment_pendiente, cont, false);
        String t = getArguments() == null ? "" : getArguments().getString(ARG_TITULO, "");
        ((TextView) v.findViewById(R.id.txt_pendiente_titulo)).setText(t);
        return v;
    }
}