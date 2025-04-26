package com.example.zapateria_app.POJO;

public class ProductoConCategoria {
    private int id;
    private String nombre;
    private String marca;
    private int talla;
    private double precio;
    private String nombreCategoria;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public int getTalla() { return talla; }
    public void setTalla(int talla) { this.talla = talla; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
}