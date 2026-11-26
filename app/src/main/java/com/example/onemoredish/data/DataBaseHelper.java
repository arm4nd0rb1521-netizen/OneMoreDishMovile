package com.example.onemoredish.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "one_more_dish.db";
    private static final int DATABASE_VERSION = 1;

    private static Context appContext;
    private static DataBaseHelper instancia;

    public static synchronized void init(Context context) {
        if (appContext == null && context != null) {
            appContext = context.getApplicationContext();
            instancia = new DataBaseHelper(appContext);
        }
    }

    public static synchronized DataBaseHelper de() {
        if (instancia == null && appContext != null) {
            instancia = new DataBaseHelper(appContext);
        }
        return instancia;
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE platillos (" +
                "id INTEGER PRIMARY KEY," +
                "nombre TEXT," +
                "categoria TEXT," +
                "precio REAL," +
                "descripcion TEXT," +
                "emoji TEXT)");

        db.execSQL("CREATE TABLE mesas (" +
                "numero INTEGER PRIMARY KEY," +
                "redonda INTEGER," +
                "estado TEXT," +
                "comentarios TEXT)");

        db.execSQL("CREATE TABLE pedido_mesa (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mesa_numero INTEGER," +
                "nombre TEXT," +
                "precio REAL)");

        db.execSQL("CREATE TABLE ventas (" +
                "id INTEGER PRIMARY KEY," +
                "fecha TEXT," +
                "hora TEXT," +
                "destino TEXT," +
                "total REAL," +
                "comentarios TEXT," +
                "estado_cocina TEXT)");

        db.execSQL("CREATE TABLE lineas_venta (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "venta_id INTEGER," +
                "nombre TEXT," +
                "cantidad INTEGER," +
                "precio_unitario REAL," +
                "subtotal REAL)");

        db.execSQL("CREATE TABLE ventas_diarias (" +
                "fecha TEXT PRIMARY KEY," +
                "titulo TEXT," +
                "total_ventas INTEGER," +
                "total_monto REAL," +
                "es_hoy INTEGER)");

        db.execSQL("CREATE TABLE empleados (" +
                "id INTEGER PRIMARY KEY," +
                "nombre TEXT," +
                "usuario TEXT," +
                "puesto TEXT," +
                "horario TEXT," +
                "estado TEXT)");

        db.execSQL("CREATE TABLE proveedores (" +
                "id INTEGER PRIMARY KEY," +
                "nombre TEXT," +
                "contacto TEXT," +
                "producto TEXT," +
                "avatar TEXT)");

        insertarDatosSemilla(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS platillos");
        db.execSQL("DROP TABLE IF EXISTS mesas");
        db.execSQL("DROP TABLE IF EXISTS pedido_mesa");
        db.execSQL("DROP TABLE IF EXISTS ventas");
        db.execSQL("DROP TABLE IF EXISTS lineas_venta");
        db.execSQL("DROP TABLE IF EXISTS ventas_diarias");
        db.execSQL("DROP TABLE IF EXISTS empleados");
        db.execSQL("DROP TABLE IF EXISTS proveedores");
        onCreate(db);
    }

    private void insertarDatosSemilla(SQLiteDatabase db) {
        Object[][] platillos = {
                {1, "Enchiladas Suizas", "Enchiladas", 145.0, "Con crema y queso gratinado", "🌯"},
                {2, "Tacos al Pastor", "Tacos", 125.0, "Con piña y cebolla", "🌮"},
                {3, "Mole Poblano", "Especialidades", 185.0, "Con pollo y ajonjolí", "🍲"},
                {4, "Chiles en Nogada", "Especialidades", 175.0, "De temporada", "🌶️"},
                {5, "Agua de Jamaica", "Bebidas", 90.0, "Jarra fría", "🥤"},
                {6, "Flan Napolitano", "Postres", 70.0, "Con caramelo", "🍮"}
        };
        for (Object[] p : platillos) {
            ContentValues cv = new ContentValues();
            cv.put("id", (Integer) p[0]);
            cv.put("nombre", (String) p[1]);
            cv.put("categoria", (String) p[2]);
            cv.put("precio", (Double) p[3]);
            cv.put("descripcion", (String) p[4]);
            cv.put("emoji", (String) p[5]);
            db.insert("platillos", null, cv);
        }

        String[] estados = {"Libre", "Ocupada", "Sucia", "Libre", "Libre", "Reservada",
                "Ocupada", "Libre", "Libre", "Libre", "Libre", "Ocupada"};
        for (int i = 0; i < estados.length; i++) {
            int n = i + 1;
            ContentValues cv = new ContentValues();
            cv.put("numero", n);
            cv.put("redonda", (n == 6 || n == 9) ? 1 : 0);
            cv.put("estado", estados[i]);
            cv.put("comentarios", "");
            db.insert("mesas", null, cv);

            if ("Ocupada".equals(estados[i])) {
                ContentValues cvp1 = new ContentValues();
                cvp1.put("mesa_numero", n);
                cvp1.put("nombre", "Enchiladas Suizas");
                cvp1.put("precio", 145.0);
                db.insert("pedido_mesa", null, cvp1);

                ContentValues cvp2 = new ContentValues();
                cvp2.put("mesa_numero", n);
                cvp2.put("nombre", "Agua de Jamaica");
                cvp2.put("precio", 90.0);
                db.insert("pedido_mesa", null, cvp2);
            }
        }

        Object[][] empleados = {
                {1, "Ortiz Guerrero Nelly", "ortiz.nelly", "Cocinero", "Mañana", "descansando"},
                {2, "Nieto Cambrón Fernando", "fernando.nieto", "Cocinero", "Tarde", "trabajando"},
                {3, "Bruno Martínez Sofía", "sofia.bruno", "Mesero", "Mañana", "trabajando"},
                {4, "Torres Mendoza Jenifer", "jenifer.torres", "Mesero", "Tarde", "trabajando"}
        };
        for (Object[] e : empleados) {
            ContentValues cv = new ContentValues();
            cv.put("id", (Integer) e[0]);
            cv.put("nombre", (String) e[1]);
            cv.put("usuario", (String) e[2]);
            cv.put("puesto", (String) e[3]);
            cv.put("horario", (String) e[4]);
            cv.put("estado", (String) e[5]);
            db.insert("empleados", null, cv);
        }

        Object[][] proveedores = {
                {1, "Don Julio Verduras", "55 4123 8900", "Verduras y Hortalizas frescas", "🥬"},
                {2, "Lácteos del Valle", "55 2233 4455", "Quesos, Crema y Mantequilla", "🥛"},
                {3, "Carnicería La Selecta", "55 9876 5432", "Cortes de Res, Pollo y Pastor", "🥩"},
                {4, "Distribuidora de Bebidas", "55 1122 3344", "Refrescos, Aguas y Jugos", "🥤"},
                {5, "Panadería Don Pan", "55 6677 8899", "Pan bolillo y repostería", "🥖"}
        };
        for (Object[] pr : proveedores) {
            ContentValues cv = new ContentValues();
            cv.put("id", (Integer) pr[0]);
            cv.put("nombre", (String) pr[1]);
            cv.put("contacto", (String) pr[2]);
            cv.put("producto", (String) pr[3]);
            cv.put("avatar", (String) pr[4]);
            db.insert("proveedores", null, cv);
        }

        Object[][] ventasDiarias = {
                {"2024-10-15", "Hoy · En curso", 3, 674.0, 1},
                {"2024-10-14", "Ayer", 3, 1810.0, 0},
                {"2024-10-13", "Anterior (13 Oct)", 3, 1575.0, 0},
                {"2024-10-12", "Anterior (12 Oct)", 3, 1950.0, 0}
        };
        for (Object[] vd : ventasDiarias) {
            ContentValues cv = new ContentValues();
            cv.put("fecha", (String) vd[0]);
            cv.put("titulo", (String) vd[1]);
            cv.put("total_ventas", (Integer) vd[2]);
            cv.put("total_monto", (Double) vd[3]);
            cv.put("es_hoy", (Integer) vd[4]);
            db.insert("ventas_diarias", null, cv);
        }

        ContentValues cvVenta = new ContentValues();
        cvVenta.put("id", 100);
        cvVenta.put("fecha", "2024-10-15");
        cvVenta.put("hora", "14:10");
        cvVenta.put("destino", "Mesa 1");
        cvVenta.put("total", 470.0);
        cvVenta.put("comentarios", "Sin cebolla en las enchiladas");
        cvVenta.put("estado_cocina", "Pendiente");
        db.insert("ventas", null, cvVenta);

        ContentValues cvL1 = new ContentValues();
        cvL1.put("venta_id", 100);
        cvL1.put("nombre", "Enchiladas Suizas");
        cvL1.put("cantidad", 2);
        cvL1.put("precio_unitario", 145.0);
        cvL1.put("subtotal", 290.0);
        db.insert("lineas_venta", null, cvL1);

        ContentValues cvL2 = new ContentValues();
        cvL2.put("venta_id", 100);
        cvL2.put("nombre", "Agua de Jamaica");
        cvL2.put("cantidad", 2);
        cvL2.put("precio_unitario", 90.0);
        cvL2.put("subtotal", 180.0);
        db.insert("lineas_venta", null, cvL2);
    }

    public List<Datos.Platillo> obtenerPlatillos() {
        List<Datos.Platillo> lista = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM platillos", null);
        if (c.moveToFirst()) {
            do {
                lista.add(new Datos.Platillo(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("nombre")),
                        c.getString(c.getColumnIndexOrThrow("categoria")),
                        c.getDouble(c.getColumnIndexOrThrow("precio")),
                        c.getString(c.getColumnIndexOrThrow("descripcion")),
                        c.getString(c.getColumnIndexOrThrow("emoji"))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public void guardarPlatillo(Datos.Platillo p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", p.id);
        cv.put("nombre", p.nombre);
        cv.put("categoria", p.categoria);
        cv.put("precio", p.precio);
        cv.put("descripcion", p.descripcion);
        cv.put("emoji", p.emoji);
        db.insertWithOnConflict("platillos", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void eliminarPlatillo(int id) {
        getWritableDatabase().delete("platillos", "id = ?", new String[]{String.valueOf(id)});
    }

    public List<Datos.Mesa> obtenerMesas() {
        List<Datos.Mesa> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM mesas ORDER BY numero ASC", null);
        if (c.moveToFirst()) {
            do {
                int num = c.getInt(c.getColumnIndexOrThrow("numero"));
                boolean redonda = c.getInt(c.getColumnIndexOrThrow("redonda")) == 1;
                String estado = c.getString(c.getColumnIndexOrThrow("estado"));
                String comentarios = c.getString(c.getColumnIndexOrThrow("comentarios"));

                Datos.Mesa m = new Datos.Mesa(num, redonda, estado);
                m.comentarios = comentarios != null ? comentarios : "";

                Cursor cp = db.rawQuery("SELECT * FROM pedido_mesa WHERE mesa_numero = ?", new String[]{String.valueOf(num)});
                if (cp.moveToFirst()) {
                    do {
                        m.pedido.add(new Datos.Linea(
                                cp.getString(cp.getColumnIndexOrThrow("nombre")),
                                cp.getDouble(cp.getColumnIndexOrThrow("precio"))
                        ));
                    } while (cp.moveToNext());
                }
                cp.close();

                lista.add(m);
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public void actualizarMesa(Datos.Mesa m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("estado", m.estado);
        cv.put("comentarios", m.comentarios);
        db.update("mesas", cv, "numero = ?", new String[]{String.valueOf(m.numero)});

        db.delete("pedido_mesa", "mesa_numero = ?", new String[]{String.valueOf(m.numero)});
        for (Datos.Linea l : m.pedido) {
            ContentValues cvp = new ContentValues();
            cvp.put("mesa_numero", m.numero);
            cvp.put("nombre", l.nombre);
            cvp.put("precio", l.precio);
            db.insert("pedido_mesa", null, cvp);
        }
    }

    public List<Datos.Empleado> obtenerEmpleados() {
        List<Datos.Empleado> lista = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM empleados", null);
        if (c.moveToFirst()) {
            do {
                lista.add(new Datos.Empleado(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("nombre")),
                        c.getString(c.getColumnIndexOrThrow("usuario")),
                        c.getString(c.getColumnIndexOrThrow("puesto")),
                        c.getString(c.getColumnIndexOrThrow("horario")),
                        c.getString(c.getColumnIndexOrThrow("estado"))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public void guardarEmpleado(Datos.Empleado e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", e.id);
        cv.put("nombre", e.nombre);
        cv.put("usuario", e.usuario);
        cv.put("puesto", e.puesto);
        cv.put("horario", e.horario);
        cv.put("estado", e.estado);
        db.insertWithOnConflict("empleados", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void eliminarEmpleado(int id) {
        getWritableDatabase().delete("empleados", "id = ?", new String[]{String.valueOf(id)});
    }

    public List<Datos.Proveedor> obtenerProveedores() {
        List<Datos.Proveedor> lista = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM proveedores", null);
        if (c.moveToFirst()) {
            do {
                lista.add(new Datos.Proveedor(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("nombre")),
                        c.getString(c.getColumnIndexOrThrow("contacto")),
                        c.getString(c.getColumnIndexOrThrow("producto")),
                        c.getString(c.getColumnIndexOrThrow("avatar"))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public void guardarProveedor(Datos.Proveedor p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", p.id);
        cv.put("nombre", p.nombre);
        cv.put("contacto", p.contacto);
        cv.put("producto", p.producto);
        cv.put("avatar", p.avatar);
        db.insertWithOnConflict("proveedores", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void eliminarProveedor(int id) {
        getWritableDatabase().delete("proveedores", "id = ?", new String[]{String.valueOf(id)});
    }

    public List<Datos.Venta> obtenerVentas() {
        List<Datos.Venta> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM ventas ORDER BY id DESC", null);
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                String fecha = c.getString(c.getColumnIndexOrThrow("fecha"));
                String hora = c.getString(c.getColumnIndexOrThrow("hora"));
                String destino = c.getString(c.getColumnIndexOrThrow("destino"));
                double total = c.getDouble(c.getColumnIndexOrThrow("total"));
                String comentarios = c.getString(c.getColumnIndexOrThrow("comentarios"));
                String estadoCocina = c.getString(c.getColumnIndexOrThrow("estado_cocina"));

                List<Datos.LineaVenta> lineas = new ArrayList<>();
                Cursor cl = db.rawQuery("SELECT * FROM lineas_venta WHERE venta_id = ?", new String[]{String.valueOf(id)});
                if (cl.moveToFirst()) {
                    do {
                        lineas.add(new Datos.LineaVenta(
                                cl.getString(cl.getColumnIndexOrThrow("nombre")),
                                cl.getInt(cl.getColumnIndexOrThrow("cantidad")),
                                cl.getDouble(cl.getColumnIndexOrThrow("precio_unitario"))
                        ));
                    } while (cl.moveToNext());
                }
                cl.close();

                Datos.Venta v = new Datos.Venta(id, fecha, hora, destino, lineas, total, comentarios);
                v.estadoCocina = estadoCocina != null ? estadoCocina : "Pendiente";
                lista.add(v);
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    public void actualizarEstadoCocina(int ventaId, String estado) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("estado_cocina", estado);
        db.update("ventas", cv, "id = ?", new String[]{String.valueOf(ventaId)});
    }

    public void insertarVenta(Datos.Venta v) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", v.id);
        cv.put("fecha", v.fecha);
        cv.put("hora", v.hora);
        cv.put("destino", v.destino);
        cv.put("total", v.total);
        cv.put("comentarios", v.comentarios);
        cv.put("estado_cocina", v.estadoCocina);
        db.insert("ventas", null, cv);

        for (Datos.LineaVenta l : v.lineas) {
            ContentValues cvl = new ContentValues();
            cvl.put("venta_id", v.id);
            cvl.put("nombre", l.nombre);
            cvl.put("cantidad", l.cantidad);
            cvl.put("precio_unitario", l.precioUnitario);
            cvl.put("subtotal", l.subtotal);
            db.insert("lineas_venta", null, cvl);
        }

        actualizarVentasDiarias(db, v.fecha, v.total);
    }

    private void actualizarVentasDiarias(SQLiteDatabase db, String fecha, double monto) {
        Cursor c = db.rawQuery("SELECT * FROM ventas_diarias WHERE fecha = ?", new String[]{fecha});
        if (c.moveToFirst()) {
            int ventas = c.getInt(c.getColumnIndexOrThrow("total_ventas")) + 1;
            double montoTotal = c.getDouble(c.getColumnIndexOrThrow("total_monto")) + monto;
            c.close();

            ContentValues cv = new ContentValues();
            cv.put("total_ventas", ventas);
            cv.put("total_monto", montoTotal);
            db.update("ventas_diarias", cv, "fecha = ?", new String[]{fecha});
        } else {
            c.close();
            ContentValues cv = new ContentValues();
            cv.put("fecha", fecha);
            cv.put("titulo", fecha);
            cv.put("total_ventas", 1);
            cv.put("total_monto", monto);
            cv.put("es_hoy", 1);
            db.insert("ventas_diarias", null, cv);
        }
    }

    public List<Datos.VentaDia> obtenerVentasDiarias() {
        List<Datos.VentaDia> lista = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM ventas_diarias ORDER BY fecha DESC", null);
        if (c.moveToFirst()) {
            do {
                lista.add(new Datos.VentaDia(
                        c.getString(c.getColumnIndexOrThrow("titulo")),
                        c.getString(c.getColumnIndexOrThrow("fecha")),
                        c.getInt(c.getColumnIndexOrThrow("total_ventas")),
                        c.getDouble(c.getColumnIndexOrThrow("total_monto")),
                        c.getInt(c.getColumnIndexOrThrow("es_hoy")) == 1
                ));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }
}
