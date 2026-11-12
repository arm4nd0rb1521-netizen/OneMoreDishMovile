package com.example.onemoredish.util;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Con pantalla completa (edge-to-edge) separa el contenido de la barra de estado, la de navegación y el teclado. */
public final class Pantalla {
    private Pantalla() {}

    /**
     * @param raiz     vista raíz de la pantalla
     * @param superior vista que recibe padding extra arriba (barra de estado)
     * @param inferior vista que recibe padding extra abajo (barra de navegación / teclado)
     */
    public static void ajustar(View raiz, View superior, View inferior) {
        final int padSup = superior.getPaddingTop();
        final int padInf = inferior.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout()
                    | WindowInsetsCompat.Type.ime());
            superior.setPadding(superior.getPaddingLeft(), padSup + b.top,
                    superior.getPaddingRight(), superior.getPaddingBottom());
            inferior.setPadding(inferior.getPaddingLeft(), inferior.getPaddingTop(),
                    inferior.getPaddingRight(), padInf + b.bottom);
            return insets;
        });
    }
}