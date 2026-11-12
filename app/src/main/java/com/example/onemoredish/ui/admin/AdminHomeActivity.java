package com.example.onemoredish.ui.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onemoredish.R;
import com.example.onemoredish.data.Sesion;
import com.example.onemoredish.ui.Navegacion;
import com.example.onemoredish.util.Pantalla;

/** MARCADOR del paso 1: el paso 2 lo convierte en "Resumen de hoy" con mapa de mesas y barra inferior. */
public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this,
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),   // iconos oscuros sobre rosa claro
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Pantalla.ajustar(findViewById(R.id.raiz), findViewById(R.id.barra), findViewById(R.id.contenido));

        Sesion s = Sesion.de(this);
        ((TextView) findViewById(R.id.txt_pendiente))
                .setText(getString(R.string.admin_pendiente, s.nombre().isEmpty() ? "ADMIN" : s.nombre()));

        findViewById(R.id.btn_salir).setOnClickListener(v -> Navegacion.cerrarSesion(this));
    }
}