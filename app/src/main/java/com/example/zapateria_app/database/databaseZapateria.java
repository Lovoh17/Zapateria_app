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
            // 1. Insertar categorías
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Zapatos deportivos')");
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Zapatos formales')");
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Sandalias')");

            // 2. Insertar productos
            db.execSQL("INSERT INTO productos (nombre, marca, talla, precio, id_categoria) " +
                    "VALUES ('Runner 2000', 'Nike', 42, 89.99, 1)");
            db.execSQL("INSERT INTO productos (nombre, marca, talla, precio, id_categoria) " +
                    "VALUES ('Executive', 'Ecco', 40, 129.99, 2)");

            // 3. Insertar clientes
            db.execSQL("INSERT INTO clientes (nombre, telefono, correo) " +
                    "VALUES ('Juan Pérez', '5551234567', 'juan@example.com')");

            // 4. Insertar empleados
            db.execSQL("INSERT INTO empleados (nombre, puesto) " +
                    "VALUES ('María García', 'Vendedor')");

            // 5. Insertar venta
            db.execSQL("INSERT INTO ventas (id_cliente, id_empleado, fecha, total) " +
                    "VALUES (1, 1, strftime('%Y-%m-%d %H:%M:%S', 'now'), 219.98)");

            // 6. Insertar detalle de venta
            db.execSQL("INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                    "VALUES (1, 1, 1, 89.99, 89.99)");
            db.execSQL("INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                    "VALUES (1, 2, 1, 129.99, 129.99)");

            // 7. Insertar movimientos de inventario
            db.execSQL("INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, costo_unitario, fecha) " +
                    "VALUES (1, 'ENTRADA', 10, 45.00, strftime('%Y-%m-%d %H:%M:%S', 'now'))");

            // 8. Actualizar inventario actual
            db.execSQL("INSERT INTO inventario_actual (id_producto, stock, costo_promedio) " +
                    "VALUES (1, 10, 45.00)");

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