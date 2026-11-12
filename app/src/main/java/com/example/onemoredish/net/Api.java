package com.example.onemoredish.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.onemoredish.data.Sesion;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cliente HTTP mínimo para los servlets de One More Dish.
 * - Los POST se envían como formulario (así los leen tus servlets con getParameter).
 * - Agrega el token de la sesión en el header Authorization.
 * - Los callbacks se ejecutan en el hilo principal (se puede tocar la UI).
 * - Una respuesta HTTP no 2xx, o un JSON con "ok": false, llega a error(mensaje).
 */
public final class Api {
    private Api() {}

    public interface Respuesta {
        void exito(String cuerpo);
        void error(String mensaje);
    }

    private static final ExecutorService HILOS = Executors.newFixedThreadPool(4);
    private static final Handler PRINCIPAL = new Handler(Looper.getMainLooper());

    public static void get(Context ctx, String ruta, Respuesta r) {
        pedir(ctx, "GET", ruta, null, r);
    }

    public static void post(Context ctx, String ruta, Map<String, String> params, Respuesta r) {
        pedir(ctx, "POST", ruta, params, r);
    }

    private static void pedir(Context ctx, String metodo, String ruta,
                              Map<String, String> params, Respuesta r) {
        final String token = Sesion.de(ctx).token();

        HILOS.execute(() -> {
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) new URL(Config.BASE_URL + ruta).openConnection();
                con.setConnectTimeout(15000);
                con.setReadTimeout(20000);
                con.setRequestMethod(metodo);
                con.setRequestProperty("Accept", "application/json");
                if (token != null) con.setRequestProperty("Authorization", "Bearer " + token);

                if ("POST".equals(metodo)) {
                    byte[] cuerpo = codificar(params).getBytes(StandardCharsets.UTF_8);
                    con.setDoOutput(true);
                    con.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded; charset=UTF-8");
                    con.setFixedLengthStreamingMode(cuerpo.length);
                    try (OutputStream os = con.getOutputStream()) { os.write(cuerpo); }
                }

                int codigo = con.getResponseCode();
                InputStream in = codigo < 400 ? con.getInputStream() : con.getErrorStream();
                String texto = leer(in);

                if (codigo >= 200 && codigo < 300) {
                    String fallo = mensajeSiFallo(texto);
                    if (fallo == null) entregarExito(r, texto); else entregarError(r, fallo);
                } else {
                    String m = mensajeDe(texto);
                    entregarError(r, m != null ? m : "Error del servidor (" + codigo + ")");
                }
            } catch (IOException e) {
                entregarError(r, "No se pudo conectar con el servidor. Revisa tu conexión.");
            } finally {
                if (con != null) con.disconnect();
            }
        });
    }

    // ---------- ayudas ----------

    private static String codificar(Map<String, String> params) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                if (sb.length() > 0) sb.append('&');
                sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=')
                        .append(URLEncoder.encode(e.getValue() == null ? "" : e.getValue(), "UTF-8"));
            }
        }
        return sb.toString();
    }

    private static String leer(InputStream in) throws IOException {
        if (in == null) return "";
        try (InputStream is = in; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) out.write(buf, 0, n);
            return out.toString("UTF-8");
        }
    }

    /** Extrae "mensaje" de un JSON de error; null si no es JSON o no lo trae. */
    private static String mensajeDe(String texto) {
        try {
            String m = new JSONObject(texto).optString("mensaje", "");
            return m.isEmpty() ? null : m;
        } catch (Exception e) {
            return null;
        }
    }

    /** Si el cuerpo es un objeto JSON con "ok": false devuelve su mensaje; si no, null. */
    private static String mensajeSiFallo(String texto) {
        try {
            JSONObject o = new JSONObject(texto);
            if (o.has("ok") && !o.optBoolean("ok", true)) {
                String m = o.optString("mensaje", "");
                return m.isEmpty() ? "No se pudo completar la acción" : m;
            }
        } catch (Exception ignorado) { /* era un arreglo JSON u otro formato: éxito */ }
        return null;
    }

    private static void entregarExito(Respuesta r, String t) { PRINCIPAL.post(() -> r.exito(t)); }
    private static void entregarError(Respuesta r, String m) { PRINCIPAL.post(() -> r.error(m)); }
}
