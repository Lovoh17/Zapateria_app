package com.example.zapateria_app.POJO;

public class DetalleVentaConProducto {
    private int id;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private String nombreProducto;
    private String marcaProducto;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public String getMarcaProducto() { return marcaProducto; }
    public void setMarcaProducto(String marcaProducto) { this.marcaProducto = marcaProducto; }
}