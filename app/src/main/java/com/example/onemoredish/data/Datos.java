package com.example.onemoredish.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Capa de datos respaldada por SQLite a través de DataBaseHelper.
 * Todos los cambios (platillos, mesas, ventas, personal, proveedores) se guardan en la base de datos.
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
        public final List<Linea> pedido;

        public Mesa(int numero, boolean redonda, String estado) {
            this.numero = numero;
            this.redonda = redonda;
            this.estado = estado;
            this.pedido = new java.util.ArrayList<>();
        }

        public double total() {
            double t = 0;
            for (Linea l : pedido) t += l.precio;
            return t;
        }

        public void guardar() {
            DataBaseHelper.de().actualizarMesa(this);
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

    public static final class LineaVenta {
        public final String nombre;
        public final int cantidad;
        public final double precioUnitario;
        public final double subtotal;

        public LineaVenta(String nombre, int cantidad, double precioUnitario) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = cantidad * precioUnitario;
        }
    }

    public static final class Venta {
        public final int id;
        public final String fecha; // Formato: "YYYY-MM-DD"
        public final String hora;  // Formato: "HH:mm"
        public final String destino; // Ej. "Mesa 2" o "Para llevar"
        public final List<LineaVenta> lineas;
        public final double total;
        public final String comentarios;
        public String estadoCocina = "Pendiente";

        public Venta(int id, String fecha, String hora, String destino, List<LineaVenta> lineas, double total, String comentarios) {
            this.id = id;
            this.fecha = fecha;
            this.hora = hora;
            this.destino = destino;
            this.lineas = lineas;
            this.total = total;
            this.comentarios = comentarios;
        }

        public String resumenArticulos() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lineas.size(); i++) {
                LineaVenta l = lineas.get(i);
                if (i > 0) sb.append(", ");
                sb.append(l.cantidad).append("x ").append(l.nombre);
            }
            return sb.toString();
        }

        public void actualizarEstadoCocina(String nuevoEstado) {
            this.estadoCocina = nuevoEstado;
            DataBaseHelper.de().actualizarEstadoCocina(id, nuevoEstado);
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

        public void guardar() {
            DataBaseHelper.de().guardarEmpleado(this);
        }

        public void eliminar() {
            DataBaseHelper.de().eliminarEmpleado(id);
        }
    }

    public static final class Proveedor {
        public final int id;
        public String nombre;
        public String contacto;
        public String producto;
        public String avatar;

        public Proveedor(int id, String nombre, String contacto, String producto, String avatar) {
            this.id = id;
            this.nombre = nombre;
            this.contacto = contacto;
            this.producto = producto;
            this.avatar = avatar;
        }

        public void guardar() {
            DataBaseHelper.de().guardarProveedor(this);
        }

        public void eliminar() {
            DataBaseHelper.de().eliminarProveedor(id);
        }
    }

    public static final class Platillo {
        public final int id;
        public final String nombre, categoria, descripcion, emoji;
        public final double precio;

        public Platillo(int id, String nombre, String categoria, double precio, String descripcion, String emoji) {
            this.id = id;
            this.nombre = nombre;
            this.categoria = categoria;
            this.precio = precio;
            this.descripcion = descripcion;
            this.emoji = emoji;
        }

        public void guardar() {
            DataBaseHelper.de().guardarPlatillo(this);
        }

        public void eliminar() {
            DataBaseHelper.de().eliminarPlatillo(id);
        }
    }

    // ==========================================
    // GETTERS Y MÉTODOS DE ACCESO A BASE DE DATOS
    // ==========================================

    public static List<Mesa> mesas() {
        return DataBaseHelper.de().obtenerMesas();
    }

    public static List<Platillo> platillos() {
        return DataBaseHelper.de().obtenerPlatillos();
    }

    public static List<VentaDia> ventasDiarias() {
        return DataBaseHelper.de().obtenerVentasDiarias();
    }

    public static List<Venta> detalleVentas() {
        return DataBaseHelper.de().obtenerVentas();
    }

    public static List<Empleado> personal() {
        return DataBaseHelper.de().obtenerEmpleados();
    }

    public static List<Proveedor> proveedores() {
        return DataBaseHelper.de().obtenerProveedores();
    }

    private static int generarIdVenta() {
        List<Venta> ventas = detalleVentas();
        int max = 100;
        for (Venta v : ventas) max = Math.max(max, v.id + 1);
        return max;
    }

    public static Venta registrarVentaCompleta(String destino, List<LineaVenta> lineas, String comentarios) {
        double total = 0;
        for (LineaVenta lv : lineas) total += lv.subtotal;

        String hoyFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String hoyHora = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());

        int idVenta = generarIdVenta();
        Venta v = new Venta(idVenta, hoyFecha, hoyHora, destino, lineas, total, comentarios);
        DataBaseHelper.de().insertarVenta(v);

        // Si se envió a una mesa, actualizar la mesa en BD
        if (destino != null && destino.startsWith("Mesa ")) {
            try {
                int numMesa = Integer.parseInt(destino.replace("Mesa ", "").trim());
                for (Mesa m : mesas()) {
                    if (m.numero == numMesa) {
                        m.estado = OCUPADA;
                        if (comentarios != null && !comentarios.isEmpty()) {
                            m.comentarios = comentarios;
                        }
                        for (LineaVenta lv : lineas) {
                            for (int k = 0; k < lv.cantidad; k++) {
                                m.pedido.add(new Linea(lv.nombre, lv.precioUnitario));
                            }
                        }
                        m.guardar();
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }
        return v;
    }

    public static List<Venta> ventasPorFecha(String fecha) {
        List<Venta> resultado = new java.util.ArrayList<>();
        List<Venta> todas = detalleVentas();
        if (fecha == null || fecha.isEmpty()) return todas;
        for (Venta v : todas) {
            if (fecha.equals(v.fecha)) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    public static double totalMontoPorFecha(String fecha) {
        double total = 0;
        for (Venta v : detalleVentas()) {
            if (fecha == null || fecha.isEmpty() || fecha.equals(v.fecha)) {
                total += v.total;
            }
        }
        return total;
    }

    public static int ocupadas() {
        int c = 0;
        for (Mesa m : mesas()) if (OCUPADA.equals(m.estado)) c++;
        return c;
    }

    /** $145 si es entero, $145.50 si trae centavos. */
    public static String peso(double v) {
        return v == Math.rint(v) ? String.format(Locale.US, "$%,.0f", v) : String.format(Locale.US, "$%,.2f", v);
    }

    /** Siempre con dos decimales: $145.00 */
    public static String peso2(double v) { return String.format(Locale.US, "$%,.2f", v); }
}
