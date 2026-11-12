package com.example.onemoredish.ui.principal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.onemoredish.R;
import com.example.onemoredish.data.Sesion;
import com.example.onemoredish.ui.Navegacion;
import com.example.onemoredish.util.Pantalla;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Pantalla contenedora de todos los roles: barra superior + fragmento + barra inferior.
 * Cada rol solo cambia el menú inferior (res/menu/nav_*.xml) y los fragmentos que muestra.
 */
public class PrincipalActivity extends AppCompatActivity {

    private static final String ESTADO_TITULO = "titulo";
    private BottomNavigationView nav;
    private TextView txtTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this,
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        nav = findViewById(R.id.nav);
        txtTitulo = findViewById(R.id.txt_titulo);
        Pantalla.ajustar(findViewById(R.id.raiz), findViewById(R.id.barra), nav);
        findViewById(R.id.btn_salir).setOnClickListener(v -> Navegacion.cerrarSesion(this));

        nav.inflateMenu(menuDe(Sesion.de(this).rol()));

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_mas) {          // "Más" no cambia de pestaña: despliega su menú
                mostrarMas();
                return false;
            }
            rebotar(id);
            mostrar(id, true);
            return true;
        });
        // Si "Más" ya está marcado (viniendo de una de sus opciones), volver a tocarlo reabre el menú
        nav.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.nav_mas) mostrarMas();
        });

        if (savedInstanceState == null) {
            nav.getMenu().getItem(0).setChecked(true);
            mostrar(nav.getMenu().getItem(0).getItemId(), false);
        } else {
            txtTitulo.setText(savedInstanceState.getString(ESTADO_TITULO, ""));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out);
        out.putString(ESTADO_TITULO, txtTitulo.getText().toString());
    }

    /** Para que un fragmento cambie de pestaña (por ejemplo los botones de Inicio). */
    public void seleccionar(int idItem) {
        nav.setSelectedItemId(idItem);
    }

    private int menuDe(@Nullable String rol) {
        // PASOS SIGUIENTES: "MESERO" -> nav_mesero, "COCINERO" -> nav_cocina, "CLIENTE" -> nav_cliente
        return R.menu.nav_admin;
    }

    // ---------------------------------------------------------------- "Más" desplegable
    private void mostrarMas() {
        View ancla = nav.findViewById(R.id.nav_mas);
        PopupMenu pm = new PopupMenu(this, ancla != null ? ancla : nav, Gravity.END);
        pm.inflate(R.menu.menu_mas);
        pm.setOnMenuItemClickListener(op -> {
            nav.getMenu().findItem(R.id.nav_mas).setChecked(true);   // "Más" queda marcado
            rebotar(R.id.nav_mas);
            mostrar(op.getItemId(), true);
            return true;
        });
        pm.show();
    }

    // ---------------------------------------------------------------- animación del ícono
    /** El ítem tocado "rebota": empieza chico y crece con un pequeño rebote. */
    private void rebotar(int idItem) {
        View v = nav.findViewById(idItem);
        if (v == null) return;
        v.setScaleX(0.8f);
        v.setScaleY(0.8f);
        v.animate().scaleX(1f).scaleY(1f)
                .setDuration(320)
                .setInterpolator(new OvershootInterpolator(3f))
                .start();
    }

    // ---------------------------------------------------------------- fragmentos
    private void mostrar(int id, boolean animar) {
        Fragment f;
        String titulo;
        if (id == R.id.nav_inicio) {
            f = new InicioAdminFragment();
            titulo = getString(R.string.admin_resumen_titulo);
        } else if (id == R.id.nav_menu) {
            f = new MenuFragment();
            titulo = getString(R.string.titulo_menu);
        } else if (id == R.id.nav_mesas) {
            f = new MesasFragment();
            titulo = getString(R.string.titulo_mesas);
        } else {
            titulo = tituloPendiente(id);
            f = PendienteFragment.nuevo(titulo);
        }
        txtTitulo.setText(titulo);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        if (animar) t.setCustomAnimations(R.anim.entra, R.anim.sale);
        t.replace(R.id.contenedor, f).commit();
    }

    /**
     * Pantallas que aún no existen. Cuando crees una (por ejemplo PersonalFragment),
     * agrega aquí arriba un "else if (id == R.id.mas_personal) { f = new PersonalFragment(); ... }".
     */
    private String tituloPendiente(int id) {
        if (id == R.id.nav_ventas) return getString(R.string.titulo_ventas);
        if (id == R.id.mas_domicilio) return getString(R.string.titulo_domicilio);
        if (id == R.id.mas_personal) return getString(R.string.titulo_personal);
        if (id == R.id.mas_cocina) return getString(R.string.titulo_cocina);
        if (id == R.id.mas_proveedores) return getString(R.string.titulo_proveedores);
        return getString(R.string.titulo_mas);
    }
}