package com.example.onemoredish.ui.principal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onemoredish.R;
import com.example.onemoredish.data.Datos;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Locale;

/** Pestaña "Menú" del administrador: lo mismo que menu_admin.html (buscar, agregar, modificar, eliminar). */
public class MenuFragment extends Fragment {

    private static final int OP_MODIFICAR = 1, OP_ELIMINAR = 2;

    private GridLayout grid;
    private TextView txtContador, txtVacio;
    private TextInputEditText txtBuscar;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup cont, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_menu, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        grid = v.findViewById(R.id.grid_platillos);
        txtContador = v.findViewById(R.id.txt_contador);
        txtVacio = v.findViewById(R.id.txt_vacio);
        txtBuscar = v.findViewById(R.id.txt_buscar);

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void onTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void afterTextChanged(Editable s) { pintar(false); }
        });
        v.findViewById(R.id.btn_agregar_platillo).setOnClickListener(x -> formulario(null));

        pintar(true);
    }

    // ---------------------------------------------------------------- lista
    private void pintar(boolean animar) {
        String filtro = texto(txtBuscar).toLowerCase(Locale.ROOT);
        grid.removeAllViews();

        int n = 0;
        for (Datos.Platillo p : Datos.platillos()) {
            if (!p.nombre.toLowerCase(Locale.ROOT).contains(filtro)) continue;
            View t = tarjeta(p);
            grid.addView(t);
            if (animar) entrar(t, n);
            n++;
        }

        txtContador.setText(n == 1 ? getString(R.string.un_platillo) : getString(R.string.n_platillos, n));
        txtVacio.setVisibility(n == 0 ? View.VISIBLE : View.GONE);
        txtVacio.setText(Datos.platillos().isEmpty() ? R.string.menu_vacio : R.string.menu_sin_resultados);
    }

    /** Las tarjetas aparecen en cascada: suben y se desvanecen hacia adentro. */
    private void entrar(View t, int i) {
        float d = getResources().getDisplayMetrics().density;
        t.setAlpha(0f);
        t.setTranslationY(24 * d);
        t.animate().alpha(1f).translationY(0f)
                .setStartDelay(Math.min(i, 8) * 45L).setDuration(260).start();
    }

    private View tarjeta(Datos.Platillo p) {
        View v = getLayoutInflater().inflate(R.layout.item_platillo, grid, false);
        ((TextView) v.findViewById(R.id.pl_emoji)).setText(p.emoji);
        ((TextView) v.findViewById(R.id.pl_nombre)).setText(p.nombre);
        ((TextView) v.findViewById(R.id.pl_desc)).setText(p.descripcion);
        ((TextView) v.findViewById(R.id.pl_precio)).setText(Datos.peso2(p.precio));
        v.findViewById(R.id.pl_opciones).setOnClickListener(b -> opciones(b, p));
        return v;
    }

    // ---------------------------------------------------------------- botón ⋯
    private void opciones(View ancla, Datos.Platillo p) {
        PopupMenu pm = new PopupMenu(requireContext(), ancla, Gravity.END);
        pm.getMenu().add(0, OP_MODIFICAR, 0, R.string.modificar_platillo);
        pm.getMenu().add(0, OP_ELIMINAR, 1, R.string.eliminar_platillo);
        pm.setOnMenuItemClickListener(op -> {
            if (op.getItemId() == OP_MODIFICAR) formulario(p);
            else confirmarEliminar(p);
            return true;
        });
        pm.show();
    }

    private void confirmarEliminar(Datos.Platillo p) {
        // Solo en modo demo: la hoja de mesa necesita al menos un platillo en la carta.
        // Quita esta validación cuando conectes los servlets.
        if (Datos.platillos().size() <= 1) { aviso(R.string.demo_minimo); return; }

        new MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.confirmar_eliminar, p.nombre))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, (d, w) -> {
                    Datos.platillos().remove(p);
                    pintar(false);
                    aviso(R.string.platillo_eliminado);
                }).show();
    }

    // ---------------------------------------------------------------- crear / modificar
    private void formulario(@Nullable Datos.Platillo editar) {
        BottomSheetDialog dlg = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.sheet_platillo, null);
        dlg.setContentView(v);
        dlg.getBehavior().setSkipCollapsed(true);
        dlg.setOnShowListener(d -> dlg.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED));

        TextInputLayout tilNombre = v.findViewById(R.id.til_nombre_platillo);
        TextInputLayout tilDesc = v.findViewById(R.id.til_desc_platillo);
        TextInputLayout tilPrecio = v.findViewById(R.id.til_precio_platillo);
        TextInputEditText txtNombre = v.findViewById(R.id.txt_nombre_platillo);
        TextInputEditText txtDesc = v.findViewById(R.id.txt_desc_platillo);
        TextInputEditText txtPrecio = v.findViewById(R.id.txt_precio_platillo);
        MaterialButton btn = v.findViewById(R.id.btn_guardar_platillo);

        ((TextView) v.findViewById(R.id.sheet_titulo))
                .setText(editar == null ? R.string.nuevo_platillo : R.string.modificar_titulo);
        btn.setText(editar == null ? R.string.crear : R.string.guardar_cambios);

        if (editar != null) {
            txtNombre.setText(editar.nombre);
            txtDesc.setText(editar.descripcion);
            txtPrecio.setText(String.format(Locale.US, "%.2f", editar.precio));
        }

        btn.setOnClickListener(x -> {
            tilNombre.setError(null);
            tilDesc.setError(null);
            tilPrecio.setError(null);

            String nombre = texto(txtNombre), desc = texto(txtDesc);
            double precio = -1;
            try { precio = Double.parseDouble(texto(txtPrecio)); } catch (NumberFormatException e) { /* queda -1 */ }

            boolean ok = true;
            if (nombre.isEmpty()) { tilNombre.setError(getString(R.string.err_nombre_platillo)); ok = false; }
            if (desc.isEmpty()) { tilDesc.setError(getString(R.string.err_descripcion)); ok = false; }
            if (precio < 0 || precio > 999.99) { tilPrecio.setError(getString(R.string.err_precio)); ok = false; }
            if (!ok) return;

            List<Datos.Platillo> lista = Datos.platillos();
            if (editar == null) {
                int id = 1;
                for (Datos.Platillo p : lista) id = Math.max(id, p.id + 1);
                lista.add(new Datos.Platillo(id, nombre, "", precio, desc, "🍽️"));
                aviso(R.string.platillo_creado);
            } else {
                int i = lista.indexOf(editar);
                if (i >= 0) lista.set(i, new Datos.Platillo(editar.id, nombre, editar.categoria,
                        precio, desc, editar.emoji));
                aviso(R.string.platillo_modificado);
            }
            dlg.dismiss();
            pintar(false);
        });

        dlg.show();
    }

    // ---------------------------------------------------------------- utilidades
    private void aviso(int texto) {
        Snackbar.make(requireView(), texto, Snackbar.LENGTH_SHORT).show();
    }

    private static String texto(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }
}