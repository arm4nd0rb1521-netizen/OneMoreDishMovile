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
        public String comentarios = "";
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

    public static final class VentaDia {
        public final String titulo;
        public final String fecha;
        public int totalVentas;
        public double totalMonto;
        public final boolean esHoy;

        public VentaDia(String titulo, String fecha, int totalVentas, double totalMonto, boolean esHoy) {
            this.titulo = titulo;
            this.fecha = fecha;
            this.totalVentas = totalVentas;
            this.totalMonto = totalMonto;
            this.esHoy = esHoy;
        }
    }

    public static final class Empleado {
        public final int id;
        public String nombre;
        public String usuario;
        public String puesto;
        public String horario;
        public String estado;

        public Empleado(int id, String nombre, String usuario, String puesto, String horario, String estado) {
            this.id = id;
            this.nombre = nombre;
            this.usuario = usuario;
            this.puesto = puesto;
            this.horario = horario;
            this.estado = estado;
        }

        public String iniciales() {
            if (nombre == null || nombre.trim().isEmpty()) return "??";
            String[] partes = nombre.trim().split("\\s+");
            if (partes.length >= 2) {
                return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase(Locale.ROOT);
            }
            return nombre.substring(0, Math.min(2, nombre.length())).toUpperCase(Locale.ROOT);
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
    private static final List<VentaDia> VENTAS_DIARIAS = new ArrayList<>();
    private static final List<Empleado> PERSONAL = new ArrayList<>();

    static {
        PERSONAL.add(new Empleado(1, "Ortiz Guerrero Nelly", "ortiz.nelly", "Cocinero", "Mañana", "descansando"));
        PERSONAL.add(new Empleado(2, "Nieto Cambrón Fernando", "fernando.nieto", "Cocinero", "Tarde", "trabajando"));
        PERSONAL.add(new Empleado(3, "Bruno Martínez Sofía", "sofia.bruno", "Mesero", "Mañana", "trabajando"));
        PERSONAL.add(new Empleado(4, "Torres Mendoza Jenifer", "jenifer.torres", "Mesero", "Tarde", "trabajando"));
        VENTAS_DIARIAS.add(new VentaDia("Hoy · En curso", "2024-10-15", 18, 674.0, true));
        VENTAS_DIARIAS.add(new VentaDia("Ayer", "2024-10-14", 24, 1795.0, false));
        VENTAS_DIARIAS.add(new VentaDia("Anterior (13 Oct)", "2024-10-13", 22, 1543.0, false));
        VENTAS_DIARIAS.add(new VentaDia("Anterior (12 Oct)", "2024-10-12", 24, 1967.0, false));
        VENTAS_DIARIAS.add(new VentaDia("11 Oct 2024", "2024-10-11", 19, 1420.0, false));
        VENTAS_DIARIAS.add(new VentaDia("10 Oct 2024", "2024-10-10", 28, 2150.0, false));
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
    public static List<VentaDia> ventasDiarias() { return VENTAS_DIARIAS; }
    public static List<Empleado> personal() { return PERSONAL; }

    public static void registrarVenta(double monto) {
        if (!VENTAS_DIARIAS.isEmpty() && VENTAS_DIARIAS.get(0).esHoy) {
            VentaDia hoy = VENTAS_DIARIAS.get(0);
            hoy.totalVentas++;
            hoy.totalMonto += monto;
        } else {
            VENTAS_DIARIAS.add(0, new VentaDia("Hoy · En curso", "2024-10-15", 1, monto, true));
        }
    }

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