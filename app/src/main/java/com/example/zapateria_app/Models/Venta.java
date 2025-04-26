package com.example.zapateria_app.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "ventas",
        foreignKeys = {
                @ForeignKey(entity = Cliente.class,
                        parentColumns = "id",
                        childColumns = "id_cliente",
                        onDelete = ForeignKey.RESTRICT),
                @ForeignKey(entity = Empleado.class,
                        parentColumns = "id",
                        childColumns = "id_empleado",
                        onDelete = ForeignKey.RESTRICT)
        },
        indices = {
                @Index(value = "id_cliente"),
                @Index(value = "id_empleado")
        })
public class Venta implements java.io.Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "id_cliente")
    private int idCliente;

    @ColumnInfo(name = "id_empleado")
    private int idEmpleado;

    @ColumnInfo
    private String fecha;

    @ColumnInfo
    private double total;

    public Venta() {
    }

    @Ignore
    public Venta(int id, int idCliente, int idEmpleado, String fecha, double total) {
        this.id = id;
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.fecha = fecha;
        this.total = total;
    }

    @Ignore
    public Venta(int idCliente, int idEmpleado, String fecha, double total) {
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.fecha = fecha;
        this.total = total;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}