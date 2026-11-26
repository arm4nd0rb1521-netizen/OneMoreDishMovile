package com.example.onemoredish.ui.principal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Módulo de Proveedores: CRUD completo de personas y empresas que surten al restaurante,
 * con una vista de lista mostrando foto/avatar, nombre, producto y contacto.
 */
public class ProveedoresFragment extends Fragment {

    private LinearLayout lista;
    private TextView txtContador, txtVacio;
    private TextInputEditText txtBuscar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup cont, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_proveedores, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        lista = v.findViewById(R.id.lista_proveedores);
        txtContador = v.findViewById(R.id.txt_contador_proveedores);
        txtVacio = v.findViewById(R.id.txt_vacio_proveedores);
        txtBuscar = v.findViewById(R.id.txt_buscar_proveedor);

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void onTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void afterTextChanged(Editable s) { pintar(); }
        });

        v.findViewById(R.id.btn_anadir_proveedor).setOnClickListener(x -> formularioProveedor(null));

        pintar();
    }

    private void pintar() {
        String filtro = txtBuscar.getText() == null ? "" : txtBuscar.getText().toString().trim().toLowerCase(Locale.ROOT);
        lista.removeAllViews();

        List<Datos.Proveedor> proveedores = Datos.proveedores();
        int count = 0;

        for (Datos.Proveedor p : proveedores) {
            if (!p.nombre.toLowerCase(Locale.ROOT).contains(filtro) &&
                    !p.producto.toLowerCase(Locale.ROOT).contains(filtro) &&
                    !p.contacto.toLowerCase(Locale.ROOT).contains(filtro)) {
                continue;
            }

            View item = getLayoutInflater().inflate(R.layout.item_proveedor, lista, false);
            ((TextView) item.findViewById(R.id.txt_prov_avatar)).setText(p.avatar != null && !p.avatar.isEmpty() ? p.avatar : "📦");
            ((TextView) item.findViewById(R.id.txt_prov_nombre)).setText(p.nombre);
            ((TextView) item.findViewById(R.id.txt_prov_producto)).setText("Surte: " + p.producto);
            ((TextView) item.findViewById(R.id.txt_prov_contacto)).setText("Contacto: " + p.contacto);

            item.setOnClickListener(x -> formularioProveedor(p));
            lista.addView(item);
            count++;
        }

        txtContador.setText(count == 1 ? "1 proveedor" : count + " proveedores");
        txtVacio.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
    }

    // ---------------------------------------------------------------- CRUD: Formulario Crear / Modificar
    private void formularioProveedor(@Nullable Datos.Proveedor editar) {
        BottomSheetDialog dlg = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.sheet_proveedor, null);
        dlg.setContentView(v);
        Objects.requireNonNull(dlg.getBehavior()).setSkipCollapsed(true);
        dlg.setOnShowListener(d -> dlg.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED));

        TextView titulo = v.findViewById(R.id.sheet_titulo_proveedor);
        TextInputEditText txtNombre = v.findViewById(R.id.txt_nombre_proveedor);
        TextInputEditText txtProducto = v.findViewById(R.id.txt_producto_proveedor);
        TextInputEditText txtContacto = v.findViewById(R.id.txt_contacto_proveedor);
        TextInputEditText txtAvatar = v.findViewById(R.id.txt_avatar_proveedor);
        MaterialButton btnGuardar = v.findViewById(R.id.btn_guardar_proveedor);
        MaterialButton btnEliminar = v.findViewById(R.id.btn_eliminar_proveedor_sheet);

        titulo.setText(editar == null ? "Nuevo proveedor" : "Modificar proveedor");
        btnGuardar.setText(editar == null ? "Crear" : "Guardar cambios");

        if (editar != null) {
            txtNombre.setText(editar.nombre);
            txtProducto.setText(editar.producto);
            txtContacto.setText(editar.contacto);
            txtAvatar.setText(editar.avatar);
            btnEliminar.setVisibility(View.VISIBLE);
        } else {
            txtAvatar.setText("📦");
            btnEliminar.setVisibility(View.GONE);
        }

        btnGuardar.setOnClickListener(x -> {
            String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().toString().trim();
            String producto = txtProducto.getText() == null ? "" : txtProducto.getText().toString().trim();
            String contacto = txtContacto.getText() == null ? "" : txtContacto.getText().toString().trim();
            String avatar = txtAvatar.getText() == null ? "" : txtAvatar.getText().toString().trim();

            if (nombre.isEmpty()) {
                txtNombre.setError("Escribe el nombre del proveedor");
                return;
            }
            if (producto.isEmpty()) {
                txtProducto.setError("Escribe el producto que surte");
                return;
            }

            List<Datos.Proveedor> listaProv = Datos.proveedores();
            if (editar == null) {
                int nuevoId = 1;
                for (Datos.Proveedor p : listaProv) nuevoId = Math.max(nuevoId, p.id + 1);
                listaProv.add(new Datos.Proveedor(nuevoId, nombre, contacto, producto, avatar.isEmpty() ? "📦" : avatar));
                Snackbar.make(requireView(), "Proveedor agregado correctamente", Snackbar.LENGTH_SHORT).show();
            } else {
                editar.nombre = nombre;
                editar.producto = producto;
                editar.contacto = contacto;
                editar.avatar = avatar.isEmpty() ? "📦" : avatar;
                Snackbar.make(requireView(), "Proveedor modificado correctamente", Snackbar.LENGTH_SHORT).show();
            }

            dlg.dismiss();
            pintar();
        });

        btnEliminar.setOnClickListener(x -> {
            dlg.dismiss();
            confirmarEliminar(editar);
        });

        dlg.show();
    }

    private void confirmarEliminar(Datos.Proveedor proveedor) {
        if (proveedor == null) return;
        new MaterialAlertDialogBuilder(requireContext())
                .setMessage("¿Eliminar a \"" + proveedor.nombre + "\" de los proveedores?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (d, w) -> {
                    Datos.proveedores().remove(proveedor);
                    pintar();
                    Snackbar.make(requireView(), "Proveedor eliminado", Snackbar.LENGTH_SHORT).show();
                }).show();
    }
}
