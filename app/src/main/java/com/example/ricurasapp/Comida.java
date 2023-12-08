package com.example.ricurasapp;

import java.io.Serializable;

public class Comida implements Serializable {
    private int id;
    private String nombre;
    private byte[] imagen;
    private String descripcion;
    private double precio;
    private int cantidad;

    public Comida(int id, String nombre, byte[] imagen, String descripcion, double precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

}
