package com.example.zapateria_app.POJO;

import java.util.Date;

public class ReporteVentasPeriodo {
    private String fecha;
    private int cantidadVentas;
    private double totalVendido;
    private double promedioVenta;

    // Getters y Setters
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public int getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(int cantidadVentas) { this.cantidadVentas = cantidadVentas; }
    public double getTotalVendido() { return totalVendido; }
    public void setTotalVendido(double totalVendido) { this.totalVendido = totalVendido; }
    public double getPromedioVenta() { return promedioVenta; }
    public void setPromedioVenta(double promedioVenta) { this.promedioVenta = promedioVenta; }
}