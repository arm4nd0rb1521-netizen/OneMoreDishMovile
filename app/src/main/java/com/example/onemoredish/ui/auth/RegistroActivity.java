package com.example.onemoredish.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onemoredish.R;
import com.example.onemoredish.net.Api;
import com.example.onemoredish.util.Pantalla;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilCorreo, tilTelefono, tilContrasena, tilConfirmar;
    private TextInputEditText txtNombre, txtCorreo, txtTelefono, txtContrasena, txtConfirmar;
    private MaterialCheckBox chkTerminos;
    private MaterialButton btnCrear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Pantalla.ajustar(findViewById(R.id.raiz), findViewById(R.id.fila_logo), findViewById(R.id.hoja));

        tilNombre = findViewById(R.id.til_nombre);
        tilCorreo = findViewById(R.id.til_correo);
        tilTelefono = findViewById(R.id.til_telefono);
        tilContrasena = findViewById(R.id.til_contrasena);
        tilConfirmar = findViewById(R.id.til_confirmar);
        txtNombre = findViewById(R.id.txt_nombre);
        txtCorreo = findViewById(R.id.txt_correo);
        txtTelefono = findViewById(R.id.txt_telefono);
        txtContrasena = findViewById(R.id.txt_contrasena);
        txtConfirmar = findViewById(R.id.txt_confirmar);
        chkTerminos = findViewById(R.id.chk_terminos);
        btnCrear = findViewById(R.id.btn_crear);

        btnCrear.setOnClickListener(v -> crearCuenta());
        txtConfirmar.setOnEditorActionListener((v, accion, ev) -> {
            if (accion == EditorInfo.IME_ACTION_DONE) { crearCuenta(); return true; }
            return false;
        });
        findViewById(R.id.btn_ir_login).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private static String texto(TextInputEditText t) {
        return t.getText() == null ? "" : t.getText().toString();
    }

    private void crearCuenta() {
        for (TextInputLayout t : new TextInputLayout[]{tilNombre, tilCorreo, tilTelefono, tilContrasena, tilConfirmar})
            t.setError(null);

        String nombreCompleto = texto(txtNombre).trim().replaceAll("\\s+", " ");
        final String correo = texto(txtCorreo).trim();
        String telefono = texto(txtTelefono).trim();
        String contra = texto(txtContrasena);
        String confirmar = texto(txtConfirmar);

        boolean valido = true;
        if (nombreCompleto.isEmpty()) { tilNombre.setError(getString(R.string.err_nombre)); valido = false; }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { tilCorreo.setError(getString(R.string.err_correo)); valido = false; }
        if (!telefono.matches("[0-9]{10}")) { tilTelefono.setError(getString(R.string.err_telefono)); valido = false; }
        if (contra.length() < 8) { tilContrasena.setError(getString(R.string.err_contrasena_corta)); valido = false; }
        else if (!contra.equals(confirmar)) { tilConfirmar.setError(getString(R.string.err_no_coinciden)); valido = false; }
        if (!chkTerminos.isChecked()) {
            Toast.makeText(this, R.string.err_terminos, Toast.LENGTH_SHORT).show();
            valido = false;
        }
        if (!valido) return;

        // La BD guarda nombre y apellido por separado: primera palabra = nombre, el resto = apellido
        int corte = nombreCompleto.indexOf(' ');
        String nombre = corte < 0 ? nombreCompleto : nombreCompleto.substring(0, corte);
        String apellido = corte < 0 ? "" : nombreCompleto.substring(corte + 1);

        cargando(true);
        Map<String, String> p = new LinkedHashMap<>();
        p.put("nombre", nombre);
        p.put("apellido", apellido);
        p.put("correo", correo);
        p.put("telefono", telefono);
        p.put("contrasena", contra);

        Api.post(this, "api/registro", p, new Api.Respuesta() {
            @Override public void exito(String cuerpo) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(RegistroActivity.this, R.string.cuenta_creada, Toast.LENGTH_LONG).show();
                Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                i.putExtra(LoginActivity.EXTRA_CORREO, correo);
                startActivity(i);
                finish();
            }

            @Override public void error(String mensaje) {
                if (isFinishing() || isDestroyed()) return;
                cargando(false);
                // Errores sobre el correo (p. ej. "ya está registrado") se muestran en su campo
                if (mensaje.toLowerCase().contains("correo")) tilCorreo.setError(mensaje);
                else Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargando(boolean si) {
        btnCrear.setEnabled(!si);
        btnCrear.setText(si ? R.string.creando : R.string.crear_cuenta);
    }
}