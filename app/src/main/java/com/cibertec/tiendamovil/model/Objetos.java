package com.cibertec.tiendamovil.model;

public class Objetos {

    private String codigo; // Nuevo campo para el código
    private String titulo;
    private String fecha;
    private String duracion;
    private String genero;
    private String portada;
    private double precio;
    private int cantidad;
    private double total;

    // Getters y Setters para 'codigo'
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // Getters y Setters para 'titulo'
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Getters y Setters para 'fecha'
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    // Getters y Setters para 'duracion'
    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    // Getters y Setters para 'genero'
    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    // Getters y Setters para 'portada'
    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    // Getters y Setters para 'precio'
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    // Getters y Setters para 'cantidad'
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Getters y Setters para 'total'
    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Método para calcular el total basado en precio y cantidad
    public void calcularTotal() {
        this.total = this.precio * this.cantidad;
    }
}
