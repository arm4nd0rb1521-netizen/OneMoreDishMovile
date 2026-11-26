package com.example.onemoredish.ui.principal;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.example.onemoredish.R;
import com.example.onemoredish.data.Datos;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Pestaña "Personal" para gestionar el CRUD completo de empleados (Cocinero, Mesero, Administrador),
 * mostrando su estado, nombre, puesto, horario y usuario de la app.
 */
public class PersonalFragment extends Fragment {

    private LinearLayout lista;
    private TextView txtContador, txtVacio;
    private TextInputEditText txtBuscar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup cont, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_personal, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        lista = v.findViewById(R.id.lista_personal);
        txtContador = v.findViewById(R.id.txt_contador_personal);
        txtVacio = v.findViewById(R.id.txt_vacio_personal);
        txtBuscar = v.findViewById(R.id.txt_buscar_personal);

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void onTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void afterTextChanged(Editable s) { pintar(); }
        });

        v.findViewById(R.id.btn_anadir_personal).setOnClickListener(x -> formularioEmpleado(null));

        pintar();
    }

    private void pintar() {
        String filtro = txtBuscar.getText() == null ? "" : txtBuscar.getText().toString().trim().toLowerCase(Locale.ROOT);
        lista.removeAllViews();

        List<Datos.Empleado> empleados = Datos.personal();
        List<Datos.Empleado> filtrados = new ArrayList<>();
        for (Datos.Empleado e : empleados) {
            if (e.nombre.toLowerCase(Locale.ROOT).contains(filtro) ||
                    e.puesto.toLowerCase(Locale.ROOT).contains(filtro) ||
                    e.usuario.toLowerCase(Locale.ROOT).contains(filtro)) {
                filtrados.add(e);
            }
        }

        // Agrupar por puesto (ej. Cocinero, Mesero, etc.)
        Map<String, List<Datos.Empleado>> grupos = new LinkedHashMap<>();
        for (Datos.Empleado e : filtrados) {
            String puestoKey = (e.puesto != null && !e.puesto.isEmpty()) ? e.puesto.toUpperCase(Locale.ROOT) + "S" : "OTRO";
            List<Datos.Empleado> grupoLista = grupos.get(puestoKey);
            if (grupoLista == null) {
                grupoLista = new ArrayList<>();
                grupos.put(puestoKey, grupoLista);
            }
            grupoLista.add(e);
        }

        int totalMostrados = 0;
        for (Map.Entry<String, List<Datos.Empleado>> entry : grupos.entrySet()) {
            TextView seccion = new TextView(requireContext());
            seccion.setText(entry.getKey());
            seccion.setTextSize(13f);
            seccion.setTextColor(getResources().getColor(R.color.boton, null));
            seccion.setTypeface(null, android.graphics.Typeface.BOLD);
            seccion.setPadding(4, 28, 4, 8);
            lista.addView(seccion);

            for (Datos.Empleado e : entry.getValue()) {
                View item = getLayoutInflater().inflate(R.layout.item_personal, lista, false);
                ((TextView) item.findViewById(R.id.txt_personal_iniciales)).setText(e.iniciales());
                ((TextView) item.findViewById(R.id.txt_personal_nombre)).setText(e.nombre);

                String detalle = "Id " + e.id + " • " + e.horario + " • @" + e.usuario;
                ((TextView) item.findViewById(R.id.txt_personal_puesto_horario)).setText(detalle);

                TextView estado = item.findViewById(R.id.txt_personal_estado);
                estado.setText(e.estado);

                // Colorear badge según estado ("trabajando" verde, "descansando" amarillo/naranja)
                boolean trabajando = "trabajando".equalsIgnoreCase(e.estado);
                int colorFondo = trabajando ? Color.parseColor("#E1F0E5") : Color.parseColor("#FEF3D6");
                int colorTexto = trabajando ? Color.parseColor("#2D6A45") : Color.parseColor("#946200");
                ViewCompat.setBackgroundTintList(estado, ColorStateList.valueOf(colorFondo));
                estado.setTextColor(colorTexto);

                item.setOnClickListener(x -> formularioEmpleado(e));
                lista.addView(item);
                totalMostrados++;
            }
        }

        txtContador.setText(totalMostrados == 1 ? getString(R.string.un_empleado) : getString(R.string.n_empleados, totalMostrados));
        txtVacio.setVisibility(totalMostrados == 0 ? View.VISIBLE : View.GONE);
    }

    // ---------------------------------------------------------------- CRUD: Formulario Agregar / Modificar
    private void formularioEmpleado(@Nullable Datos.Empleado editar) {
        BottomSheetDialog dlg = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.sheet_personal, null);
        dlg.setContentView(v);
        Objects.requireNonNull(dlg.getBehavior()).setSkipCollapsed(true);
        dlg.setOnShowListener(d -> dlg.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED));

        TextView titulo = v.findViewById(R.id.sheet_titulo_personal);
        TextInputEditText txtNombre = v.findViewById(R.id.txt_nombre_personal);
        TextInputEditText txtUsuario = v.findViewById(R.id.txt_usuario_personal);
        MaterialAutoCompleteTextView comboPuesto = v.findViewById(R.id.combo_puesto_personal);
        MaterialAutoCompleteTextView comboHorario = v.findViewById(R.id.combo_horario_personal);
        MaterialAutoCompleteTextView comboEstado = v.findViewById(R.id.combo_estado_personal);
        MaterialButton btnGuardar = v.findViewById(R.id.btn_guardar_personal);
        MaterialButton btnEliminar = v.findViewById(R.id.btn_eliminar_personal_sheet);

        titulo.setText(editar == null ? R.string.anadir_personal : R.string.modificar_personal);
        btnGuardar.setText(editar == null ? R.string.anadir : R.string.guardar_cambios);

        // Configurar adaptadores para dropdowns
        String[] puestos = {"Cocinero", "Mesero", "Administrador"};
        String[] horarios = {"Mañana", "Tarde", "Completo"};
        String[] estados = {"trabajando", "descansando"};

        comboPuesto.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, puestos));
        comboHorario.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, horarios));
        comboEstado.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, estados));

        if (editar != null) {
            txtNombre.setText(editar.nombre);
            txtUsuario.setText(editar.usuario);
            comboPuesto.setText(editar.puesto, false);
            comboHorario.setText(editar.horario, false);
            comboEstado.setText(editar.estado, false);
            btnEliminar.setVisibility(View.VISIBLE);
        } else {
            comboPuesto.setText(puestos[0], false);
            comboHorario.setText(horarios[0], false);
            comboEstado.setText(estados[0], false);
            btnEliminar.setVisibility(View.GONE);
        }

        btnGuardar.setOnClickListener(x -> {
            String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().toString().trim();
            String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().toString().trim();
            String puesto = comboPuesto.getText().toString().trim();
            String horario = comboHorario.getText().toString().trim();
            String estado = comboEstado.getText().toString().trim();

            if (nombre.isEmpty()) {
                txtNombre.setError(getString(R.string.err_nombre_personal));
                return;
            }
            if (usuario.isEmpty()) {
                txtUsuario.setError(getString(R.string.err_usuario_personal));
                return;
            }

            List<Datos.Empleado> listaEmpleados = Datos.personal();
            if (editar == null) {
                int nuevoId = 1;
                for (Datos.Empleado e : listaEmpleados) nuevoId = Math.max(nuevoId, e.id + 1);
                listaEmpleados.add(new Datos.Empleado(nuevoId, nombre, usuario, puesto, horario, estado));
                Snackbar.make(requireView(), R.string.personal_creado, Snackbar.LENGTH_SHORT).show();
            } else {
                editar.nombre = nombre;
                editar.usuario = usuario;
                editar.puesto = puesto;
                editar.horario = horario;
                editar.estado = estado;
                Snackbar.make(requireView(), R.string.personal_modificado, Snackbar.LENGTH_SHORT).show();
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

    private void confirmarEliminar(Datos.Empleado empleado) {
        if (empleado == null) return;
        new MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.confirmar_eliminar_personal, empleado.nombre))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, (d, w) -> {
                    Datos.personal().remove(empleado);
                    pintar();
                    Snackbar.make(requireView(), R.string.personal_eliminado, Snackbar.LENGTH_SHORT).show();
                }).show();
    }
}
