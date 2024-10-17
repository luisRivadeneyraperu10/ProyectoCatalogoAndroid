package com.cibertec.tiendamovil;

//import LuisRivadeneyra.sdk.payments
import com.cibertec.tiendamovil.model.Objetos;

public class CarritoItem {
    private Objetos objeto;
    private int cantidad;

    public CarritoItem(Objetos objeto, int cantidad) {
        this.objeto = objeto;
        this.cantidad = cantidad;
    }

    public Objetos getObjeto() {
        return objeto;
    }

    public int getCantidad() {
        return cantidad;
    }
}

