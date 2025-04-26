package com.example.zapateria_app.POJO;

import java.util.Date;

public class MovimientoConProducto {
    private int id;
    private String tipo;
    private int cantidad;
    private double costoUnitario;
    private String fecha;
    private String nombreProducto;
    private String marcaProducto;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public String getMarcaProducto() { return marcaProducto; }
    public void setMarcaProducto(String marcaProducto) { this.marcaProducto = marcaProducto; }
}