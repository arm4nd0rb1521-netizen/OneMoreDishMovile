package com.example.onemoredish.ui.principal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
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
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Fragmento de Ventas:
 * 1. Nueva Venta / Pedido: interfaz estilo Menú con botones +/-, total en vivo y envío a cocina.
 * 2. Ventas por Fecha / Calendario: consulta de ventas del día seleccionado en el calendario.
 */
public class VentasFragment extends Fragment {

    // Modo 1: Ventas / Pedidos
    private GridLayout gridVenta;
    private TextView txtResumenArticulos, txtTotalAcumulado, txtVacioVenta;
    private TextInputEditText txtBuscarVenta;
    private MaterialButton btnVerPedido;

    // Cantidades seleccionadas por platillo.id
    private final Map<Integer, Integer> cantidades = new HashMap<>();

    // Modo 2: Calendario / Historial
    private View layoutNuevaVenta, layoutCalendarioVentas;
    private TextView txtFechaSeleccionada, txtMetricMonto, txtMetricPedidos, txtSinVentasFecha;
    private LinearLayout listaHistorialVentas;

    private final SimpleDateFormat sdfIso = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat sdfBonito = new SimpleDateFormat("d 'de' MMMM, yyyy", Locale.forLanguageTag("es"));
    private final Calendar calSeleccionado = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup cont, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_ventas, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        // Selector de vista
        ChipGroup grupoModo = v.findViewById(R.id.grupo_modo_ventas);
        layoutNuevaVenta = v.findViewById(R.id.layout_nueva_venta);
        layoutCalendarioVentas = v.findViewById(R.id.layout_calendario_ventas);

        // Referencias Modo 1
        gridVenta = v.findViewById(R.id.grid_platillos_venta);
        txtResumenArticulos = v.findViewById(R.id.txt_resumen_articulos);
        txtTotalAcumulado = v.findViewById(R.id.txt_total_acumulado);
        txtVacioVenta = v.findViewById(R.id.txt_vacio_venta);
        txtBuscarVenta = v.findViewById(R.id.txt_buscar_venta);
        btnVerPedido = v.findViewById(R.id.btn_ver_pedido);

        // Referencias Modo 2
        txtFechaSeleccionada = v.findViewById(R.id.txt_fecha_seleccionada);
        txtMetricMonto = v.findViewById(R.id.txt_metric_monto);
        txtMetricPedidos = v.findViewById(R.id.txt_metric_pedidos);
        txtSinVentasFecha = v.findViewById(R.id.txt_sin_ventas_fecha);
        listaHistorialVentas = v.findViewById(R.id.lista_historial_ventas);

        // Listeners Modo Selector
        grupoModo.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_modo_venta) {
                layoutNuevaVenta.setVisibility(View.VISIBLE);
                layoutCalendarioVentas.setVisibility(View.GONE);
                pintarVentaGrid();
            } else if (checkedId == R.id.chip_modo_calendario) {
                layoutNuevaVenta.setVisibility(View.GONE);
                layoutCalendarioVentas.setVisibility(View.VISIBLE);
                pintarCalendario();
            }
        });

        // Buscador de platillos
        txtBuscarVenta.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void onTextChanged(CharSequence s, int a, int c, int d) {}
            @Override public void afterTextChanged(Editable s) { pintarVentaGrid(); }
        });

        btnVerPedido.setOnClickListener(x -> abrirConfirmacionCocina());

        // Navegación Calendario
        v.findViewById(R.id.btn_fecha_anterior).setOnClickListener(x -> {
            calSeleccionado.add(Calendar.DAY_OF_MONTH, -1);
            pintarCalendario();
        });
        v.findViewById(R.id.btn_fecha_siguiente).setOnClickListener(x -> {
            calSeleccionado.add(Calendar.DAY_OF_MONTH, 1);
            pintarCalendario();
        });
        v.findViewById(R.id.btn_abrir_calendario).setOnClickListener(x -> abrirPickerCalendario());

        // Inicializar por defecto
        pintarVentaGrid();
        actualizarBarraTotal();
    }

    // ================================================================= MODO 1: VENTA Y PEDIDO

    private void pintarVentaGrid() {
        String filtro = txtBuscarVenta.getText() == null ? "" : txtBuscarVenta.getText().toString().trim().toLowerCase(Locale.ROOT);
        gridVenta.removeAllViews();

        int n = 0;
        for (Datos.Platillo p : Datos.platillos()) {
            if (!p.nombre.toLowerCase(Locale.ROOT).contains(filtro)) continue;
            View t = tarjetaVenta(p);
            gridVenta.addView(t);
            n++;
        }
        txtVacioVenta.setVisibility(n == 0 ? View.VISIBLE : View.GONE);
    }

    private View tarjetaVenta(Datos.Platillo p) {
        View v = getLayoutInflater().inflate(R.layout.item_platillo_venta, gridVenta, false);
        View card = v.findViewById(R.id.card_platillo_venta);
        ((TextView) v.findViewById(R.id.pl_emoji)).setText(p.emoji);
        ((TextView) v.findViewById(R.id.pl_nombre)).setText(p.nombre);
        ((TextView) v.findViewById(R.id.pl_desc)).setText(p.descripcion);
        ((TextView) v.findViewById(R.id.pl_precio)).setText(Datos.peso2(p.precio));

        TextView txtCant = v.findViewById(R.id.txt_cantidad);
        ImageButton btnMenos = v.findViewById(R.id.btn_menos);
        ImageButton btnMas = v.findViewById(R.id.btn_mas);

        int cantActual = cantidades.containsKey(p.id) ? cantidades.get(p.id) : 0;
        txtCant.setText(String.valueOf(cantActual));
        card.setBackgroundResource(cantActual > 0 ? R.drawable.bg_tarjeta_platillo_seleccionado : R.drawable.bg_tarjeta_platillo);

        btnMenos.setOnClickListener(b -> {
            int c = cantidades.containsKey(p.id) ? cantidades.get(p.id) : 0;
            if (c > 0) {
                c--;
                if (c == 0) cantidades.remove(p.id); else cantidades.put(p.id, c);
                txtCant.setText(String.valueOf(c));
                card.setBackgroundResource(c > 0 ? R.drawable.bg_tarjeta_platillo_seleccionado : R.drawable.bg_tarjeta_platillo);
                actualizarBarraTotal();
            }
        });

        btnMas.setOnClickListener(b -> {
            int c = cantidades.containsKey(p.id) ? cantidades.get(p.id) : 0;
            c++;
            cantidades.put(p.id, c);
            txtCant.setText(String.valueOf(c));
            card.setBackgroundResource(R.drawable.bg_tarjeta_platillo_seleccionado);
            actualizarBarraTotal();
        });

        return v;
    }

    private void actualizarBarraTotal() {
        int totalItems = 0;
        double totalMonto = 0;

        for (Datos.Platillo p : Datos.platillos()) {
            if (cantidades.containsKey(p.id)) {
                int cant = cantidades.get(p.id);
                totalItems += cant;
                totalMonto += (cant * p.precio);
            }
        }

        txtResumenArticulos.setText(totalItems == 1 ? getString(R.string.un_articulo) : getString(R.string.n_articulos, totalItems));
        txtTotalAcumulado.setText(Datos.peso2(totalMonto));
        btnVerPedido.setEnabled(totalItems > 0);
    }

    // ---------------------------------------------------------------- DIÁLOGO CONFIRMAR Y ENVIAR A COCINA

    private void abrirConfirmacionCocina() {
        BottomSheetDialog dlg = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.sheet_confirmar_venta, null);
        dlg.setContentView(v);
        dlg.getBehavior().setSkipCollapsed(true);
        dlg.setOnShowListener(d -> dlg.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED));

        LinearLayout lista = v.findViewById(R.id.lista_confirmacion);
        MaterialAutoCompleteTextView comboMesa = v.findViewById(R.id.combo_mesa_destino);
        TextInputEditText txtComentarios = v.findViewById(R.id.txt_comentarios_cocina);
        TextView txtTotal = v.findViewById(R.id.txt_total_confirmacion);
        MaterialButton btnEnviar = v.findViewById(R.id.btn_confirmar_enviar_cocina);

        // Opciones de mesa / destino
        List<String> opciones = new ArrayList<>();
        opciones.add(getString(R.string.para_llevar));
        for (Datos.Mesa m : Datos.mesas()) {
            opciones.add(getString(R.string.mesa_n, m.numero));
        }
        comboMesa.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, opciones));
        comboMesa.setText(opciones.get(0), false);

        Runnable refrescarSheet = new Runnable() {
            @Override
            public void run() {
                lista.removeAllViews();
                double suma = 0;
                for (Datos.Platillo p : Datos.platillos()) {
                    if (cantidades.containsKey(p.id) && cantidades.get(p.id) > 0) {
                        int cant = cantidades.get(p.id);
                        double sub = cant * p.precio;
                        suma += sub;

                        View row = getLayoutInflater().inflate(R.layout.item_linea_confirmacion, lista, false);
                        ((TextView) row.findViewById(R.id.txt_nombre_linea)).setText(p.nombre);
                        ((TextView) row.findViewById(R.id.txt_detalle_linea))
                                .setText(cant + " x " + Datos.peso2(p.precio));
                        ((TextView) row.findViewById(R.id.txt_subtotal_linea)).setText(Datos.peso2(sub));

                        row.findViewById(R.id.btn_quitar_linea).setOnClickListener(b -> {
                            cantidades.remove(p.id);
                            actualizarBarraTotal();
                            if (cantidades.isEmpty()) {
                                dlg.dismiss();
                            } else {
                                run();
                            }
                        });
                        lista.addView(row);
                    }
                }
                txtTotal.setText(Datos.peso2(suma));
            }
        };

        refrescarSheet.run();

        btnEnviar.setOnClickListener(b -> {
            List<Datos.LineaVenta> lineas = new ArrayList<>();
            for (Datos.Platillo p : Datos.platillos()) {
                if (cantidades.containsKey(p.id) && cantidades.get(p.id) > 0) {
                    lineas.add(new Datos.LineaVenta(p.nombre, cantidades.get(p.id), p.precio));
                }
            }
            if (lineas.isEmpty()) {
                Snackbar.make(requireView(), R.string.err_sin_platillos_seleccionados, Snackbar.LENGTH_SHORT).show();
                dlg.dismiss();
                return;
            }

            String destino = comboMesa.getText().toString().trim();
            String comentarios = txtComentarios.getText() == null ? "" : txtComentarios.getText().toString().trim();

            Datos.registrarVentaCompleta(destino, lineas, comentarios);

            String msj = destino.equals(getString(R.string.para_llevar)) ?
                    getString(R.string.venta_para_llevar_exitosa) :
                    getString(R.string.venta_exitosa, destino);

            Snackbar.make(requireView(), msj, Snackbar.LENGTH_LONG).show();

            cantidades.clear();
            pintarVentaGrid();
            actualizarBarraTotal();
            dlg.dismiss();
        });

        dlg.show();
    }

    // ================================================================= MODO 2: CALENDARIO Y HISTORIAL DE VENTAS

    private void pintarCalendario() {
        Date d = calSeleccionado.getTime();
        String fechaIso = sdfIso.format(d);
        txtFechaSeleccionada.setText(sdfBonito.format(d));

        List<Datos.Venta> ventasDia = Datos.ventasPorFecha(fechaIso);
        double totalMonto = Datos.totalMontoPorFecha(fechaIso);

        txtMetricMonto.setText(Datos.peso2(totalMonto));
        txtMetricPedidos.setText(String.valueOf(ventasDia.size()));

        listaHistorialVentas.removeAllViews();
        for (Datos.Venta v : ventasDia) {
            View ticket = tarjetaHistorial(v);
            listaHistorialVentas.addView(ticket);
        }

        txtSinVentasFecha.setVisibility(ventasDia.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private View tarjetaHistorial(Datos.Venta v) {
        View view = getLayoutInflater().inflate(R.layout.item_venta_historial, listaHistorialVentas, false);
        ((TextView) view.findViewById(R.id.txt_ticket_id)).setText("#" + v.id);
        ((TextView) view.findViewById(R.id.txt_venta_hora)).setText(v.hora);
        ((TextView) view.findViewById(R.id.txt_venta_destino)).setText(v.destino);
        ((TextView) view.findViewById(R.id.txt_venta_resumen)).setText(v.resumenArticulos());
        ((TextView) view.findViewById(R.id.txt_venta_total)).setText(Datos.peso2(v.total));

        TextView txtCom = view.findViewById(R.id.txt_venta_comentarios);
        if (v.comentarios != null && !v.comentarios.isEmpty()) {
            txtCom.setVisibility(View.VISIBLE);
            txtCom.setText(getString(R.string.comentarios_agregados, v.comentarios));
        } else {
            txtCom.setVisibility(View.GONE);
        }
        return view;
    }

    private void abrirPickerCalendario() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.filtrar_fecha)
                .setSelection(calSeleccionado.getTimeInMillis())
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            calSeleccionado.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            pintarCalendario();
        });

        picker.show(getParentFragmentManager(), "DATE_PICKER");
    }
}
