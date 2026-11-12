package com.example.onemoredish.data;

import android.content.Context;
import android.content.SharedPreferences;

/** Guarda token, rol y nombre del usuario que inició sesión. */
public final class Sesion {
    private static final String ARCHIVO = "sesion";
    private final SharedPreferences sp;

    private Sesion(Context c) {
        sp = c.getApplicationContext().getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    public static Sesion de(Context c) { return new Sesion(c); }

    public void guardar(String token, int id, String rol, String nombre, boolean recordar) {
        sp.edit().putString("token", token).putInt("id", id).putString("rol", rol)
                .putString("nombre", nombre).putBoolean("recordar", recordar).apply();
    }

    public void cerrar() { sp.edit().clear().apply(); }

    public boolean activa()   { return token() != null; }
    public String token()     { return sp.getString("token", null); }
    public String rol()       { return sp.getString("rol", null); }
    public String nombre()    { return sp.getString("nombre", ""); }
    public int id()           { return sp.getInt("id", 0); }
    public boolean recordar() { return sp.getBoolean("recordar", false); }
}