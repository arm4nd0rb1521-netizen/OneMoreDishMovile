package com.example.onemoredish.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Datos de demostración en memoria (mesas y platillos) con lo que muestra el look and feel.
 * Cuando conectes los servlets, solo cambias esta clase por llamadas a i.
 */
public final class Datos {
    private Datos() {}

    public static final String LIBRE = "Libre", OCUPADA = "Ocupada", SUCIA = "Sucia", RESERVADA = "Reservada";

    public static final class Linea {
        public final String nombre;
        public final double precio;
        public Linea(String nombre, double precio) { this.nombre = nombre; this.precio = precio; }
    }

    public static final class Mesa {
        public final int numero;
        public final boolean redonda;
        public String estado;
        public final List<Linea> pedido = new ArrayList<>();

        Mesa(int numero, boolean redonda, String estado) {
            this.numero = numero; this.redonda = redonda; this.estado = estado;
        }

        public double total() {
            double t = 0;
            for (Linea l : pedido) t += l.precio;
            return t;
        }
    }

    public static final class Platillo {
        public final int id;
        public final String nombre, categoria, descripcion, emoji;
        public final double precio;
        public Platillo(int id, String nombre, String categoria, double precio, String descripcion, String emoji) {
            this.id = id; this.nombre = nombre; this.categoria = categoria;
            this.precio = precio; this.descripcion = descripcion; this.emoji = emoji;
        }
    }

    private static final List<Mesa> MESAS = new ArrayList<>();
    private static final List<Platillo> PLATILLOS = new ArrayList<>();

    static {
        PLATILLOS.add(new Platillo(1, "Enchiladas Suizas", "Enchiladas", 145, "Con crema y queso gratinado", "🌯"));
        PLATILLOS.add(new Platillo(2, "Tacos al Pastor", "Tacos", 125, "Con piña y cebolla", "🌮"));
        PLATILLOS.add(new Platillo(3, "Mole Poblano", "Especialidades", 185, "Con pollo y ajonjolí", "🍲"));
        PLATILLOS.add(new Platillo(4, "Chiles en Nogada", "Especialidades", 175, "De temporada", "🌶️"));
        PLATILLOS.add(new Platillo(5, "Agua de Jamaica", "Bebidas", 90, "Jarra fría", "🥤"));
        PLATILLOS.add(new Platillo(6, "Flan Napolitano", "Postres", 70, "Con caramelo", "🍮"));

        String[] estados = {LIBRE, OCUPADA, SUCIA, LIBRE, LIBRE, RESERVADA,
                OCUPADA, LIBRE, LIBRE, LIBRE, LIBRE, OCUPADA};
        for (int i = 0; i < estados.length; i++) {
            int n = i + 1;
            Mesa m = new Mesa(n, n == 6 || n == 9, estados[i]);
            if (OCUPADA.equals(m.estado)) {
                m.pedido.add(new Linea("Enchiladas Suizas", 145));
                m.pedido.add(new Linea("Agua de Jamaica", 90));
            }
            MESAS.add(m);
        }
    }

    public static List<Mesa> mesas() { return MESAS; }
    public static List<Platillo> platillos() { return PLATILLOS; }

    public static int ocupadas() {
        int c = 0;
        for (Mesa m : MESAS) if (OCUPADA.equals(m.estado)) c++;
        return c;
    }

    /** $145 si es entero, $145.50 si trae centavos. */
    public static String peso(double v) {
        return v == Math.rint(v) ? String.format(Locale.US, "$%,.0f", v) : String.format(Locale.US, "$%,.2f", v);
    }

    /** Siempre con dos decimales: $145.00 */
    public static String peso2(double v) { return String.format(Locale.US, "$%,.2f", v); }
}