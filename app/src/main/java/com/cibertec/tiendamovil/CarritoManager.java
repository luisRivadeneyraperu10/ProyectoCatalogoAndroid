package com.cibertec.tiendamovil;

import java.util.ArrayList;
//import LuisRivadeneyra.sdk.payments
import java.util.List;

import com.cibertec.tiendamovil.model.Objetos;

public class CarritoManager {
    private static CarritoManager instance;
    private List<CarritoItem> carrito;

    private CarritoManager() {
        carrito = new ArrayList<>();
    }

    public static synchronized CarritoManager getInstance() {
        if (instance == null) {
            instance = new CarritoManager();
        }
        return instance;
    }

    public void agregarProducto(Objetos objeto, int cantidad) {
        CarritoItem item = new CarritoItem(objeto, cantidad);
        carrito.add(item);
    }

    public List<CarritoItem> obtenerCarrito() {
        return carrito;
    }
}

