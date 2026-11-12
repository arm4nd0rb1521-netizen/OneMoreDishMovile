package com.example.onemoredish.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onemoredish.R;
import com.example.onemoredish.data.Sesion;
import com.example.onemoredish.ui.Navegacion;
import com.example.onemoredish.util.Pantalla;

public class BienvenidaActivity extends AppCompatActivity {

    // etiqueta, título, detalle (textos del look and feel)
    private static final String[][] ROLES = {
            {"Cliente", "Pide y reserva", "Menú, domicilios y tus favoritos"},
            {"Administrador", "Controla todo", "Ventas, personal e inventario"},
            {"Mesero", "Atiende el salón", "Mesas, cuentas y cobros"},
            {"Cocina", "Prepara y entrega", "Comandas, inventario y domicilios"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),   // iconos claros sobre el degradado
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        super.onCreate(savedInstanceState);

        // Con "Mantener sesión" entra directo; sin ella, la sesión se descarta al reabrir la app
        Sesion sesion = Sesion.de(this);
        if (sesion.activa()) {
            if (sesion.recordar() && Navegacion.irAInicio(this, sesion.rol())) return;
            sesion.cerrar();
        }

        setContentView(R.layout.activity_bienvenida);
        Pantalla.ajustar(findViewById(R.id.raiz), findViewById(R.id.fila_logo), findViewById(R.id.hoja));

        LinearLayout lista = findViewById(R.id.lista_roles);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (String[] r : ROLES) {
            View tarjeta = inflater.inflate(R.layout.item_tarjeta_rol, lista, false);
            ((TextView) tarjeta.findViewById(R.id.rol_etiqueta)).setText(r[0]);
            ((TextView) tarjeta.findViewById(R.id.rol_titulo)).setText(r[1]);
            ((TextView) tarjeta.findViewById(R.id.rol_detalle)).setText(r[2]);
            lista.addView(tarjeta);
        }

        findViewById(R.id.btn_iniciar).setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
        findViewById(R.id.btn_crear).setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
    }
}