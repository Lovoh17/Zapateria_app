package com.example.zapateria_app.database;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.zapateria_app.DAO.CategoriaDAO;
import com.example.zapateria_app.DAO.ClienteDAO;
import com.example.zapateria_app.DAO.DetalleVentaDAO;
import com.example.zapateria_app.DAO.EmpleadoDAO;
import com.example.zapateria_app.DAO.InventarioActualDAO;
import com.example.zapateria_app.DAO.MovimientoInventarioDAO;
import com.example.zapateria_app.DAO.ProductoDAO;
import com.example.zapateria_app.DAO.VentaDAO;
import com.example.zapateria_app.Models.Categoria;
import com.example.zapateria_app.Models.Cliente;
import com.example.zapateria_app.Models.DetalleVenta;
import com.example.zapateria_app.Models.Empleado;
import com.example.zapateria_app.Models.InventarioActual;
import com.example.zapateria_app.Models.MovimientoInventario;
import com.example.zapateria_app.Models.Producto;
import com.example.zapateria_app.Models.Venta;

import java.io.File;

import io.reactivex.annotations.NonNull;

@Database(entities = {
        Categoria.class,
        Producto.class,
        Cliente.class,
        Empleado.class,
        Venta.class,
        DetalleVenta.class,
        MovimientoInventario.class,
        InventarioActual.class
}, version = 1, exportSchema = false)

public abstract class databaseZapateria extends RoomDatabase {

    // Métodos abstractos para obtener los DAOs
    public abstract CategoriaDAO categoriaDAO();
    public abstract ProductoDAO productoDAO();
    public abstract ClienteDAO clienteDAO();
    public abstract EmpleadoDAO empleadoDAO();
    public abstract VentaDAO ventaDAO();
    public abstract DetalleVentaDAO detalleVentaDAO();
    public abstract MovimientoInventarioDAO movimientoInventarioDAO();
    public abstract InventarioActualDAO inventarioActualDAO();

    private static databaseZapateria INSTANCE;

    public static synchronized databaseZapateria getInstance(Context context) {
        Log.d(TAG, "-> Inicializando base de datos ");

        if (INSTANCE == null) {
            Log.d(TAG, "-> Inicializando base de datos por primera vez");

            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            databaseZapateria.class, "zapateriaDB")
                    .fallbackToDestructiveMigration()
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Log.d(TAG, "-> Base de datos creada exitosamente");
                            insertInitialData(db);
                            logDatabaseInfo(db);
                        }

                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            super.onOpen(db);
                            Log.d(TAG, "-> Base de datos abierta");
                            logDatabaseInfo(db);
                        }
                    })
                    .build();

            // Información del archivo físico
            File dbFile = context.getDatabasePath("zapateriaDB");
            Log.d(TAG, "-> Ubicación física: " + dbFile.getAbsolutePath());
            Log.d(TAG, "-> Tamaño inicial: " + dbFile.length() + " bytes");
        } else {
            Log.d(TAG, "-> Usando instancia existente de la base de datos");
        }
        return INSTANCE;
    }

    private static void insertInitialData(SupportSQLiteDatabase db) {
        try {
            // 1. Insertar categorías (ampliado)
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Zapatos deportivos')");
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Zapatos formales')");
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Sandalias')");
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Botas')");
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Zapatillas casual')");
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Calzado infantil')");

            // 2. Insertar productos (ampliado)
            // Zapatos deportivos (categoría 1)
            db.execSQL("INSERT INTO productos (nombre, marca, talla, precio, id_categoria) " +
                    "VALUES ('Runner 2000', 'Nike', 42, 89.99, 1)");
            db.execSQL("INSERT INTO productos (nombre, marca, talla, precio, id_categoria) " +
                    "VALUES ('Air Max', 'Nike', 40, 119.99, 1)");
            db.execSQL("INSERT INTO productos (nombre, marca, talla, precio, id_categoria) " +
                    "VALUES ('Ultraboost', 'Adidas', 39, 129.99, 1)");

            // Zapatos formales (categoría 2)
            db.execSQL("INSERT INTO productos (nombre, marca, talla, precio, id_categoria) " +
                    "VALUES ('Executive', 'Ecco', 40, 129.99, 2)");
            db.execSQL("INSERT INTO productos (nombre, marca, talla, precio, id_categoria) " +
                    "VALUES ('Classic Oxford', 'Rockport', 41, 149.99, 2)");

            // Sandalias (categoría 3)
            db.execSQL("INSERT INTO productos (nombre, marca, talla, precio, id_categoria) " +
                    "VALUES ('Summer Comfort', 'Birkenstock', 38, 79.99, 3)");

            // Botas (categoría 4)
            db.execSQL("INSERT INTO productos (nombre, marca, talla, precio, id_categoria) " +
                    "VALUES ('Winter Trekker', 'Timberland', 43, 179.99, 4)");

            // 3. Insertar clientes (ampliado)
            db.execSQL("INSERT INTO clientes (nombre, telefono, correo) " +
                    "VALUES ('Juan Pérez', '5551234567', 'juan@example.com')");
            db.execSQL("INSERT INTO clientes (nombre, telefono, correo) " +
                    "VALUES ('María López', '5552345678', 'maria@example.com')");
            db.execSQL("INSERT INTO clientes (nombre, telefono, correo) " +
                    "VALUES ('Carlos Gómez', '5553456789', 'carlos@example.com')");
            db.execSQL("INSERT INTO clientes (nombre, telefono, correo) " +
                    "VALUES ('Ana Rodríguez', '5554567890', 'ana@example.com')");

            // 4. Insertar empleados (ampliado)
            db.execSQL("INSERT INTO empleados (nombre, puesto) " +
                    "VALUES ('María García', 'Vendedor')");
            db.execSQL("INSERT INTO empleados (nombre, puesto) " +
                    "VALUES ('Pedro Martínez', 'Gerente')");
            db.execSQL("INSERT INTO empleados (nombre, puesto) " +
                    "VALUES ('Luisa Fernández', 'Almacenista')");

            // 5. Insertar ventas (ampliado)
            // Venta 1
            db.execSQL("INSERT INTO ventas (id_cliente, id_empleado, fecha, total) " +
                    "VALUES (1, 1, strftime('%Y-%m-%d %H:%M:%S', 'now', '-2 days'), 219.98)");
            // Venta 2
            db.execSQL("INSERT INTO ventas (id_cliente, id_empleado, fecha, total) " +
                    "VALUES (2, 2, strftime('%Y-%m-%d %H:%M:%S', 'now', '-1 days'), 209.98)");
            // Venta 3
            db.execSQL("INSERT INTO ventas (id_cliente, id_empleado, fecha, total) " +
                    "VALUES (3, 1, strftime('%Y-%m-%d %H:%M:%S', 'now'), 129.99)");

            // 6. Insertar detalles de venta (ampliado)
            // Detalles Venta 1
            db.execSQL("INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                    "VALUES (1, 1, 1, 89.99, 89.99)");
            db.execSQL("INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                    "VALUES (1, 2, 1, 129.99, 129.99)");

            // Detalles Venta 2
            db.execSQL("INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                    "VALUES (2, 3, 1, 129.99, 129.99)");
            db.execSQL("INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                    "VALUES (2, 5, 1, 79.99, 79.99)");

            // Detalles Venta 3
            db.execSQL("INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                    "VALUES (3, 4, 1, 129.99, 129.99)");

            // 7. Insertar movimientos de inventario (ampliado)
            // Entradas de inventario
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (1, 'ENTRADA', 15, 45.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-10 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (2, 'ENTRADA', 10, 60.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-8 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (3, 'ENTRADA', 8, 65.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-6 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (4, 'ENTRADA', 12, 70.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-5 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (5, 'ENTRADA', 20, 35.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-4 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (6, 'ENTRADA', 15, 40.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-3 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (7, 'ENTRADA', 10, 90.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-2 days'))");

            // Salidas de inventario (ventas)
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (1, 'SALIDA', 1, 45.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-2 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (2, 'SALIDA', 1, 60.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-2 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (3, 'SALIDA', 1, 65.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-1 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (5, 'SALIDA', 1, 35.00, strftime('%Y-%m-%d %H:%M:%S', 'now', '-1 days'))");
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (4, 'SALIDA', 1, 70.00, strftime('%Y-%m-%d %H:%M:%S', 'now'))");

            // 8. Actualizar inventario actual (ampliado)
            db.execSQL("INSERT INTO inventario_actual (id_producto, stock, costo_promedio) " +
                    "VALUES (1, 14, 45.00)");
            db.execSQL("INSERT INTO inventario_actual (id_producto, stock, costo_promedio) " +
                    "VALUES (2, 9, 60.00)");
            db.execSQL("INSERT INTO inventario_actual (id_producto, stock, costo_promedio) " +
                    "VALUES (3, 7, 65.00)");
            db.execSQL("INSERT INTO inventario_actual (id_producto, stock, costo_promedio) " +
                    "VALUES (4, 11, 70.00)");
            db.execSQL("INSERT INTO inventario_actual (id_producto, stock, costo_promedio) " +
                    "VALUES (5, 19, 35.00)");
            db.execSQL("INSERT INTO inventario_actual (id_producto, stock, costo_promedio) " +
                    "VALUES (6, 15, 40.00)");
            db.execSQL("INSERT INTO inventario_actual (id_producto, stock, costo_promedio) " +
                    "VALUES (7, 10, 90.00)");

            Log.d(TAG, "-> Datos iniciales insertados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "-> Error al insertar datos iniciales", e);
        }
    }
    private static void logDatabaseInfo(SupportSQLiteDatabase db) {
        try {
            // Información básica de la base de datos
            Cursor cursor = db.query("PRAGMA database_list");
            while (cursor.moveToNext()) {
                Log.d(TAG, "-> Base de datos: " + cursor.getString(1) +
                        " | Ruta: " + cursor.getString(2));
            }
            cursor.close();

            // Solo número de tablas, sin detalles
            cursor = db.query("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'");
            if (cursor.moveToFirst()) {
                Log.d(TAG, "-> Número de tablas de la aplicación: " + cursor.getInt(0));
            }
            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "❌ Error al obtener información de la DB", e);
        }
    }

    public static void destroyInstance() {
        if (INSTANCE != null) {
            Log.d(TAG, "-> Destruyendo instancia de la base de datos");
            INSTANCE.close();
        }
        INSTANCE = null;
    }

}