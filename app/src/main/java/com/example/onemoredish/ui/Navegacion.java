package com.example.onemoredish.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.onemoredish.R;
import com.example.onemoredish.data.Sesion;
import com.example.onemoredish.ui.auth.BienvenidaActivity;
import com.example.onemoredish.ui.principal.PrincipalActivity;

/** Decide a qué pantalla va cada rol. */
public final class Navegacion {
    private Navegacion() {}

    /** @return true si hubo pantalla para ese rol; false si todavía no existe. */
    public static boolean irAInicio(Activity a, String rol) {
        Class<?> destino = null;
        if ("ADMIN".equals(rol)) destino = PrincipalActivity.class;
        // PASOS SIGUIENTES: "MESERO", "COCINERO" y "CLIENTE" también usarán PrincipalActivity

        if (destino == null) {
            Sesion.de(a).cerrar();
            Toast.makeText(a, a.getString(R.string.vista_pendiente, rol), Toast.LENGTH_LONG).show();
            return false;
        }
        Intent i = new Intent(a, destino);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        a.startActivity(i);
        a.finish();
        return true;
    }

    public static void cerrarSesion(Activity a) {
        Sesion.de(a).cerrar();
        Intent i = new Intent(a, BienvenidaActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        a.startActivity(i);
        a.finish();
    }
}