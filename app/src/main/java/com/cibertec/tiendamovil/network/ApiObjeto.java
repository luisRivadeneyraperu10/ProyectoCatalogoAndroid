package com.cibertec.tiendamovil.network;

import java.util.List;

import com.cibertec.tiendamovil.model.Objetos;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiObjeto {
    //Ahora especifico el metodo de envio http que permita obtener el json
    //en este caso uso el metodo de envio get a la ruta /movies/list.php
    @GET("/movies/list.php")

    //solo traigo la lista de los objetos en este caso pelis en el json
    Call<List<Objetos>> getObjetos();


}
