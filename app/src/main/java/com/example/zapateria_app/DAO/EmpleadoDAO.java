package com.example.zapateria_app.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.zapateria_app.Models.Empleado;

import java.util.List;

@Dao
public interface EmpleadoDAO {
    @Insert
    long insertEmpleado(Empleado empleado);

    @Update
    int updateEmpleado(Empleado empleado);

    @Delete
    int deleteEmpleado(Empleado empleado);

    @Query("SELECT * FROM empleados")
    List<Empleado> getAllEmpleados();

    @Query("SELECT * FROM empleados WHERE id = :id")
    Empleado getEmpleadoById(int id);

    @Query("SELECT COUNT(*) FROM empleados")
    int countEmpleados();

    @Query("SELECT COUNT(*) FROM ventas WHERE id_empleado = :empleadoId")
    int countVentasByEmpleado(int empleadoId);

    @Query("SELECT * FROM empleados WHERE nombre = :nombre LIMIT 1")
    Empleado getEmpleadoByNombre(String nombre);
}