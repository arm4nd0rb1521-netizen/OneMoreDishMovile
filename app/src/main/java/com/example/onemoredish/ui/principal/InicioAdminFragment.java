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
import com.example.onemoredish.data.Datos;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;
import java.util.Locale;

/** Pantalla de Inicio ("Resumen del día") con métricas en tiempo real desde SQLite. */
public class InicioAdminFragment extends Fragment {

    private TextView txtVentasMonto, txtPedidosCount, txtTicketProm, txtMesasFraccion, txtMesasPorcentaje, txtCocinaCount;
    private CircularProgressIndicator progMesas; // NUEVO (solo visual, Paso 3): anillo de ocupación

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup cont, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_inicio_admin, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        txtVentasMonto = v.findViewById(R.id.txt_home_ventas_monto);
        txtPedidosCount = v.findViewById(R.id.txt_home_pedidos_count);
        txtTicketProm = v.findViewById(R.id.txt_home_ticket_prom);
        txtMesasFraccion = v.findViewById(R.id.txt_home_mesas_fraccion);
        txtMesasPorcentaje = v.findViewById(R.id.txt_home_mesas_porcentaje);
        txtCocinaCount = v.findViewById(R.id.txt_home_cocina_count);
        progMesas = v.findViewById(R.id.prog_home_mesas); // NUEVO (solo visual, Paso 3)

        cargarDatosResumen();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDatosResumen();
    }

    private void cargarDatosResumen() {
        // 1. Ventas de hoy
        double totalHoy = 0;
        int pedidosHoy = 0;
        List<Datos.VentaDia> ventasDiarias = Datos.ventasDiarias();
        if (!ventasDiarias.isEmpty()) {
            Datos.VentaDia hoy = ventasDiarias.get(0);
            totalHoy = hoy.totalMonto;
            pedidosHoy = hoy.totalVentas;
        }

        txtVentasMonto.setText(Datos.peso2(totalHoy));
        txtPedidosCount.setText(pedidosHoy == 1 ? "1 pedido" : pedidosHoy + " pedidos");

        double ticketProm = pedidosHoy > 0 ? totalHoy / pedidosHoy : 0;
        txtTicketProm.setText(String.format(Locale.US, "Ticket prom. $%.2f", ticketProm));

        // 2. Mesas
        int totalMesas = Datos.mesas().size();
        int ocupadas = Datos.ocupadas();
        txtMesasFraccion.setText(ocupadas + "/" + totalMesas);
        int porcentaje = totalMesas > 0 ? (int) Math.round((double) ocupadas / totalMesas * 100) : 0;
        txtMesasPorcentaje.setText(porcentaje + "% ocupado");
        progMesas.setProgressCompat(porcentaje, false); // NUEVO (solo visual, Paso 3): el anillo muestra el mismo %

        // 3. En cocina (pendientes o en preparación)
        int enCocina = 0;
        for (Datos.Venta v : Datos.detalleVentas()) {
            if ("Pendiente".equalsIgnoreCase(v.estadoCocina) || "En preparación".equalsIgnoreCase(v.estadoCocina)) {
                enCocina++;
            }
        }
        txtCocinaCount.setText(String.valueOf(enCocina));
    }
}
