package com.example.onemoredish.ui.comun;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.example.onemoredish.R;
import com.example.onemoredish.data.Datos;
import com.google.android.material.progressindicator.LinearProgressIndicator;

/** Pinta la tarjeta de ocupación y el mapa de mesas. Lo usan Inicio (admin) y Mesas (admin y mesero). */
public final class MapaMesas {
    private final Activity act;
    private final View raiz;

    public MapaMesas(Activity act, View raiz) {
        this.act = act;
        this.raiz = raiz;
    }

    public void pintar() {
        int total = Datos.mesas().size();
        int oc = Datos.ocupadas();
        int pct = total == 0 ? 0 : Math.round(100f * oc / total);

        ((TextView) raiz.findViewById(R.id.txt_ocupadas)).setText(String.valueOf(oc));
        ((TextView) raiz.findViewById(R.id.txt_total)).setText(" / " + total);
        ((TextView) raiz.findViewById(R.id.txt_porcentaje)).setText(act.getString(R.string.porcentaje_ocupado, pct));
        ((LinearProgressIndicator) raiz.findViewById(R.id.barra_ocupacion)).setProgressCompat(pct, true);

        GridLayout grid = raiz.findViewById(R.id.grid_mesas);
        grid.removeAllViews();

        float d = act.getResources().getDisplayMetrics().density;
        int margen = Math.round(5 * d);
        int disponible = act.getResources().getDisplayMetrics().widthPixels - Math.round(40 * d);
        int lado = disponible / 3 - 2 * margen;
        LayoutInflater inf = LayoutInflater.from(act);

        for (Datos.Mesa m : Datos.mesas()) {
            View t = inf.inflate(R.layout.item_mesa, grid, false);
            ((TextView) t.findViewById(R.id.txt_numero)).setText(String.valueOf(m.numero));
            ((TextView) t.findViewById(R.id.txt_estado_mesa)).setText(m.estado);
            t.setBackgroundResource(m.redonda ? R.drawable.bg_mesa_redonda : R.drawable.bg_mesa_cuadrada);
            ViewCompat.setBackgroundTintList(t, ColorStateList.valueOf(fuerte(act, m.estado)));
            t.setOnClickListener(v -> HojaMesa.mostrar(act, m, this::pintar));

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = lado;
            lp.height = lado;
            lp.setMargins(margen, margen, margen, margen);
            t.setLayoutParams(lp);
            grid.addView(t);
        }
    }

    // ---------- colores por estado ----------
    public static int fuerte(Context c, String e) {
        return ContextCompat.getColor(c, Datos.OCUPADA.equals(e) ? R.color.mesa_ocupada
                : Datos.SUCIA.equals(e) ? R.color.mesa_sucia
                  : Datos.RESERVADA.equals(e) ? R.color.mesa_reservada : R.color.mesa_libre);
    }

    public static int claro(Context c, String e) {
        return ContextCompat.getColor(c, Datos.OCUPADA.equals(e) ? R.color.mesa_ocupada_claro
                : Datos.SUCIA.equals(e) ? R.color.mesa_sucia_claro
                  : Datos.RESERVADA.equals(e) ? R.color.mesa_reservada_claro : R.color.mesa_libre_claro);
    }

    public static int texto(Context c, String e) {
        return ContextCompat.getColor(c, Datos.OCUPADA.equals(e) ? R.color.mesa_ocupada_texto
                : Datos.SUCIA.equals(e) ? R.color.mesa_sucia_texto
                  : Datos.RESERVADA.equals(e) ? R.color.mesa_reservada_texto : R.color.mesa_libre_texto);
    }
}