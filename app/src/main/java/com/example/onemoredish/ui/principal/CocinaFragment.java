package com.example.onemoredish.ui.principal;

import android.os.Bundle;
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
import com.google.android.material.chip.ChipGroup;

import java.util.List;

/**
 * Módulo de Cocina: Muestra todos los pedidos recientes y permite al cocinero
 * cambiar el estado de cada orden (Pendiente, En preparación, Listo).
 */
public class CocinaFragment extends Fragment {

    private LinearLayout listaPedidos;
    private TextView txtVacio;
    private String filtroEstado = "TODOS";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup cont, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_cocina, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        listaPedidos = v.findViewById(R.id.lista_pedidos_cocina);
        txtVacio = v.findViewById(R.id.txt_vacio_cocina);

        ChipGroup grupoFiltro = v.findViewById(R.id.grupo_filtro_cocina);
        grupoFiltro.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_filtro_todos) filtroEstado = "TODOS";
            else if (id == R.id.chip_filtro_pendientes) filtroEstado = "Pendiente";
            else if (id == R.id.chip_filtro_preparacion) filtroEstado = "En preparación";
            else if (id == R.id.chip_filtro_listos) filtroEstado = "Listo";
            pintarPedidos();
        });

        pintarPedidos();
    }

    private void pintarPedidos() {
        listaPedidos.removeAllViews();
        List<Datos.Venta> ventas = Datos.detalleVentas();

        int mostrados = 0;
        for (Datos.Venta v : ventas) {
            if (!"TODOS".equals(filtroEstado) && !filtroEstado.equalsIgnoreCase(v.estadoCocina)) {
                continue;
            }

            View card = getLayoutInflater().inflate(R.layout.item_pedido_cocina, listaPedidos, false);
            ((TextView) card.findViewById(R.id.txt_cocina_ticket)).setText("Ticket #" + v.id);
            ((TextView) card.findViewById(R.id.txt_cocina_hora)).setText(v.hora);
            ((TextView) card.findViewById(R.id.txt_cocina_destino)).setText(v.destino);

            // Líneas de platillos
            LinearLayout listaItems = card.findViewById(R.id.lista_items_pedido);
            for (Datos.LineaVenta l : v.lineas) {
                TextView itemTxt = new TextView(requireContext());
                itemTxt.setText("• " + l.cantidad + "x " + l.nombre);
                itemTxt.setTextColor(getResources().getColor(R.color.texto, null));
                itemTxt.setTextSize(14f);
                listaItems.addView(itemTxt);
            }

            // Comentarios
            TextView txtCom = card.findViewById(R.id.txt_cocina_comentarios);
            if (v.comentarios != null && !v.comentarios.isEmpty()) {
                txtCom.setVisibility(View.VISIBLE);
                txtCom.setText(getString(R.string.comentarios_agregados, v.comentarios));
            } else {
                txtCom.setVisibility(View.GONE);
            }

            // Selector de estado
            ChipGroup grupoEstado = card.findViewById(R.id.grupo_estado_cocina);
            if ("Pendiente".equalsIgnoreCase(v.estadoCocina)) grupoEstado.check(R.id.chip_pendiente);
            else if ("En preparación".equalsIgnoreCase(v.estadoCocina)) grupoEstado.check(R.id.chip_preparacion);
            else if ("Listo".equalsIgnoreCase(v.estadoCocina)) grupoEstado.check(R.id.chip_listo);

            grupoEstado.setOnCheckedStateChangeListener((g, ids) -> {
                if (ids.isEmpty()) return;
                int chipId = ids.get(0);
                if (chipId == R.id.chip_pendiente) v.estadoCocina = "Pendiente";
                else if (chipId == R.id.chip_preparacion) v.estadoCocina = "En preparación";
                else if (chipId == R.id.chip_listo) v.estadoCocina = "Listo";
            });

            listaPedidos.addView(card);
            mostrados++;
        }

        txtVacio.setVisibility(mostrados == 0 ? View.VISIBLE : View.GONE);
    }
}
