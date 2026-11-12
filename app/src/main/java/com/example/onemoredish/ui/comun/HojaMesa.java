package com.example.onemoredish.ui.comun;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.example.onemoredish.R;
import com.example.onemoredish.data.Datos;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.List;

/** Hoja inferior de una mesa: cambiar estado y ver/agregar platillos al pedido. */
public final class HojaMesa {
    private HojaMesa() {}

    public static void mostrar(Context ctx, Datos.Mesa mesa, Runnable alCambiar) {
        BottomSheetDialog dlg = new BottomSheetDialog(ctx);
        LayoutInflater inf = LayoutInflater.from(ctx);
        View v = inf.inflate(R.layout.sheet_mesa, null);
        dlg.setContentView(v);
        dlg.getBehavior().setSkipCollapsed(true);
        dlg.setOnShowListener(d -> dlg.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED));

        TextView badge = v.findViewById(R.id.sheet_estado);
        ChipGroup grupo = v.findViewById(R.id.grupo_estado);
        View bloque = v.findViewById(R.id.bloque_pedido);
        TextView sinPedido = v.findViewById(R.id.txt_sin_pedido);
        LinearLayout lista = v.findViewById(R.id.lista_pedido);
        TextView totalValor = v.findViewById(R.id.sheet_total);
        MaterialAutoCompleteTextView combo = v.findViewById(R.id.combo_platillo);
        MaterialButton btnAgregar = v.findViewById(R.id.btn_agregar_pedido);

        ((TextView) v.findViewById(R.id.sheet_titulo)).setText(ctx.getString(R.string.mesa_n, mesa.numero));

        // ----- combo de platillos -----
        final List<Datos.Platillo> carta = Datos.platillos();
        String[] nombres = new String[carta.size()];
        for (int i = 0; i < nombres.length; i++) nombres[i] = carta.get(i).nombre;
        combo.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_dropdown_item_1line, nombres));
        combo.setText(nombres[0], false);
        final int[] elegido = {0};
        combo.setOnItemClickListener((p, vista, pos, id) -> elegido[0] = pos);

        // ----- refrescar todo según el estado actual -----
        Runnable refrescar = () -> {
            badge.setText(mesa.estado);
            ViewCompat.setBackgroundTintList(badge, ColorStateList.valueOf(MapaMesas.claro(ctx, mesa.estado)));
            badge.setTextColor(MapaMesas.texto(ctx, mesa.estado));

            boolean ocupada = Datos.OCUPADA.equals(mesa.estado);
            bloque.setVisibility(ocupada ? View.VISIBLE : View.GONE);
            sinPedido.setVisibility(ocupada ? View.GONE : View.VISIBLE);
            if (!ocupada) return;

            lista.removeAllViews();
            for (Datos.Linea l : mesa.pedido) {
                View fila = inf.inflate(R.layout.item_linea, lista, false);
                ((TextView) fila.findViewById(R.id.lin_nombre)).setText(l.nombre);
                ((TextView) fila.findViewById(R.id.lin_precio)).setText(Datos.peso(l.precio));
                lista.addView(fila);
            }
            totalValor.setText(Datos.peso(mesa.total()));
        };

        // ----- estado actual y cambios -----
        grupo.check(idChip(mesa.estado));
        refrescar.run();
        grupo.setOnCheckedStateChangeListener((g, ids) -> {
            if (ids.isEmpty()) return;
            String nuevo = estadoDe(ids.get(0));
            if (nuevo.equals(mesa.estado)) return;
            mesa.estado = nuevo;
            if (Datos.LIBRE.equals(nuevo)) mesa.pedido.clear();
            refrescar.run();
            alCambiar.run();
        });

        // ----- agregar platillo -----
        btnAgregar.setOnClickListener(b -> {
            Datos.Platillo p = carta.get(elegido[0]);
            mesa.pedido.add(new Datos.Linea(p.nombre, p.precio));
            refrescar.run();
            alCambiar.run();
            Snackbar.make(v, R.string.platillo_agregado, Snackbar.LENGTH_SHORT).show();
        });

        dlg.show();
    }

    private static int idChip(String estado) {
        if (Datos.OCUPADA.equals(estado)) return R.id.chip_ocupada;
        if (Datos.SUCIA.equals(estado)) return R.id.chip_sucia;
        if (Datos.RESERVADA.equals(estado)) return R.id.chip_reservada;
        return R.id.chip_libre;
    }

    private static String estadoDe(int id) {
        if (id == R.id.chip_ocupada) return Datos.OCUPADA;
        if (id == R.id.chip_sucia) return Datos.SUCIA;
        if (id == R.id.chip_reservada) return Datos.RESERVADA;
        return Datos.LIBRE;
    }
}