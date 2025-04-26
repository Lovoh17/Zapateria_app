package com.example.zapateria_app.POJO;

public class InventarioConProducto {
    private int idProducto;
    private String nombreProducto;
    private int stock;
    private double costoPromedio;
    private double valorTotal;

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getCostoPromedio() { return costoPromedio; }
    public void setCostoPromedio(double costoPromedio) { this.costoPromedio = costoPromedio; }
    public double getValorTotal() { return stock * costoPromedio; }
}