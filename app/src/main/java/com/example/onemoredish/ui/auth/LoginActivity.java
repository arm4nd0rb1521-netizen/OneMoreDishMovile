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
import com.example.onemoredish.data.Sesion;
import com.example.onemoredish.net.Api;
import com.example.onemoredish.ui.Navegacion;
import com.example.onemoredish.util.Pantalla;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.onemoredish.net.Config;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_CORREO = "correo";

    private TextInputLayout tilCorreo, tilContrasena;
    private TextInputEditText txtCorreo, txtContrasena;
    private MaterialCheckBox chkRecordar;
    private MaterialButton btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Pantalla.ajustar(findViewById(R.id.raiz), findViewById(R.id.fila_logo), findViewById(R.id.hoja));

        tilCorreo = findViewById(R.id.til_correo);
        tilContrasena = findViewById(R.id.til_contrasena);
        txtCorreo = findViewById(R.id.txt_correo);
        txtContrasena = findViewById(R.id.txt_contrasena);
        chkRecordar = findViewById(R.id.chk_recordar);
        btnEntrar = findViewById(R.id.btn_entrar);

        String correoPrevio = getIntent().getStringExtra(EXTRA_CORREO);
        if (correoPrevio != null) txtCorreo.setText(correoPrevio);

        btnEntrar.setOnClickListener(v -> iniciarSesion());
        txtContrasena.setOnEditorActionListener((v, accion, ev) -> {
            if (accion == EditorInfo.IME_ACTION_DONE) { iniciarSesion(); return true; }
            return false;
        });

        findViewById(R.id.btn_olvide).setOnClickListener(v ->
                Toast.makeText(this, R.string.proximamente, Toast.LENGTH_SHORT).show());
        findViewById(R.id.btn_ir_registro).setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
            finish();
        });
    }

    private void iniciarSesion() {
        tilCorreo.setError(null);
        tilContrasena.setError(null);

        String correo = txtCorreo.getText() == null ? "" : txtCorreo.getText().toString().trim();
        String contra = txtContrasena.getText() == null ? "" : txtContrasena.getText().toString();

        boolean valido = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError(getString(R.string.err_correo));
            valido = false;
        }
        if (contra.isEmpty()) {
            tilContrasena.setError(getString(R.string.err_contrasena_vacia));
            valido = false;
        }
        if (!valido) return;
        if (Config.MODO_DEMO) {
            String c = correo.toLowerCase();
            String rolDemo = c.startsWith("admin@") ? "ADMIN"
                    : c.startsWith("mesero@") ? "MESERO"
                      : c.startsWith("cocina@") ? "COCINERO" : "CLIENTE";
            Sesion.de(this).guardar("demo", 1, rolDemo, rolDemo, chkRecordar.isChecked());
            Navegacion.irAInicio(this, rolDemo);
            return;
        }

        cargando(true);
        Map<String, String> p = new LinkedHashMap<>();
        p.put("correo", correo);
        p.put("contrasena", contra);

        final boolean recordar = chkRecordar.isChecked();
        Api.post(this, "api/login", p, new Api.Respuesta() {
            @Override public void exito(String cuerpo) {
                if (isFinishing() || isDestroyed()) return;
                try {
                    JSONObject o = new JSONObject(cuerpo);
                    String rol = o.getString("rol");
                    Sesion.de(LoginActivity.this).guardar(o.getString("token"), o.getInt("id"),
                            rol, o.optString("nombre", ""), recordar);
                    if (!Navegacion.irAInicio(LoginActivity.this, rol)) cargando(false);
                } catch (Exception e) {
                    cargando(false);
                    Toast.makeText(LoginActivity.this, "Respuesta inesperada del servidor",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override public void error(String mensaje) {
                if (isFinishing() || isDestroyed()) return;
                cargando(false);
                tilContrasena.setError(mensaje);
            }
        });
    }

    private void cargando(boolean si) {
        btnEntrar.setEnabled(!si);
        btnEntrar.setText(si ? R.string.entrando : R.string.iniciar_sesion);
    }
}